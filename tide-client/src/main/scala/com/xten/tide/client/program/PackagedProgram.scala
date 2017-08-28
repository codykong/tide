package com.xten.tide.client.program

import java.io._
import java.lang.reflect.{InvocationTargetException, Method, Modifier}
import java.net.{MalformedURLException, URISyntaxException, URL}
import java.util.jar.{JarEntry, JarFile, Manifest}

import scala.collection.mutable.ListBuffer
import scala.util.Random


/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/8/24 
  */
class PackagedProgram (jarFile : File,classpaths : List[URL],var entryPointClassName : String ,args : String *) {
  private var jarFileUrl :URL = _
  private var program :String = null
  private var mainClass : Class[_] = null
  private var extractedTempLibraries : List[File] = null
  private var userCodeClassLoader : TideUserClassLoader = null

  init()

  def init() = {
    if (jarFile == null) {
      throw new IllegalArgumentException("The jar file must not be null.")
    }
    var jarFileUrl :URL = null
    try{
      jarFileUrl = jarFile.getAbsoluteFile.toURI.toURL
    }catch {
      case e: MalformedURLException => {
        throw new IllegalArgumentException("The jar file path is invalid.")
      }
    }

    checkJarFile(jarFileUrl)

    this.jarFileUrl = jarFileUrl

    if (entryPointClassName == null) {
      entryPointClassName = getEntryPointClassNameFromJar(jarFileUrl)
    }

    extractedTempLibraries = PackagedProgram.extractContainedLibraries(jarFileUrl)

    userCodeClassLoader = JobWithJars.buildUserCodeClassLoader(getAllLibraries(),classpaths,getClass.getClassLoader)

    this.mainClass = loadMainClass(entryPointClassName,userCodeClassLoader)

  }

  def getUserCodeClassLoader() : TideUserClassLoader = userCodeClassLoader

  /**
    * Returns all provided libraries needed to run the program.
    */
  def getAllLibraries(): List[URL] = {
    val libs = new ListBuffer[URL]()
    if (jarFile != null) {
      libs += jarFileUrl
    }
    for (tmpLib <- this.extractedTempLibraries) {
      try
        libs += (tmpLib.getAbsoluteFile.toURI.toURL)

      catch {
        case e: MalformedURLException => {
          throw new RuntimeException("URL is invalid. This should not happen.", e)
        }
      }
    }
    libs.toList
  }


  @throws[ProgramInvocationException]
  private def checkJarFile(jarfile: URL) {
    try
      JobWithJars.checkJarFile(jarfile)

    catch {
      case e: IOException => {
        throw new ProgramInvocationException(e.getMessage)
      }
      case t: Throwable => {
        throw new ProgramInvocationException(s"Cannot access jar file${(if (t.getMessage == null) "."
        else ": " + t.getMessage)}" , t)
      }
    }
  }

  @throws[ProgramInvocationException]
  private def getEntryPointClassNameFromJar(jarFile: URL):String = {
    var jar : JarFile = null
    var manifest : Manifest = null
    var className : String = null
    // Open jar file
    try{
      jar = new JarFile(new File(jarFile.toURI))
    }catch {
      case use: URISyntaxException => {
        throw new ProgramInvocationException(s"Invalid file path '${jarFile.getPath}'", use)
      }
      case ioex: IOException => {
        throw new ProgramInvocationException("Error while opening jar file '" + jarFile.getPath + "'. " + ioex.getMessage, ioex)
      }
    }
    // jar file must be closed at the end
    try{
      // Read from jar manifest
      try{
        manifest = jar.getManifest
      }catch {
        case ioex: IOException => {
          throw new ProgramInvocationException(s"The Manifest in the jar file could not be accessed '${jarFile.getPath}'.${ioex.getMessage}", ioex)
        }
      }

      if (manifest == null) throw new ProgramInvocationException("No manifest found in jar file '" + jarFile.getPath + "'. The manifest is need to point to the program's main class.")
      val attributes = manifest.getMainAttributes

      // check for a "program-class" entry first
      className = attributes.getValue(PackagedProgram.MANIFEST_ATTRIBUTE_ASSEMBLER_CLASS)
      if (className != null) {
        return className
      }
      // check for a main class
      className = attributes.getValue(PackagedProgram.MANIFEST_ATTRIBUTE_MAIN_CLASS)
      if (className != null) return className
      else throw new ProgramInvocationException("Neither a '" + PackagedProgram.MANIFEST_ATTRIBUTE_MAIN_CLASS + "', nor a '" + PackagedProgram.MANIFEST_ATTRIBUTE_ASSEMBLER_CLASS + "' entry was found in the jar file.")
    }finally {
      try{
        jar.close()
      }catch {
        case t: Throwable => {
          throw new ProgramInvocationException("Could not close the JAR file: " + t.getMessage, t)
        }
      }
    }

  }

  /**
    *
    * This method assumes that the context environment is prepared, or the execution
    * will be a local execution by default.
    */
  @throws[ProgramInvocationException]
  def invokeInteractiveModeForExecution() {
    PackagedProgram.callMainMethod(mainClass,args.toArray)
  }

  @throws[ProgramInvocationException]
  private def loadMainClass(className: String, cl: ClassLoader) = {
    var contextCl : ClassLoader = null
    try {
      contextCl = Thread.currentThread.getContextClassLoader
      Thread.currentThread.setContextClassLoader(cl)
      Class.forName(className, false, cl)
    }catch {
      case e: ClassNotFoundException => {
        throw new ProgramInvocationException("The program's entry point class '" + className + "' was not found in the jar file.", e)
      }
      case e: ExceptionInInitializerError => {
        throw new ProgramInvocationException("The program's entry point class '" + className + "' threw an error during initialization.", e)
      }
      case e: LinkageError => {
        throw new ProgramInvocationException("The program's entry point class '" + className + "' could not be loaded due to a linkage failure.", e)
      }
      case t: Throwable => {
        throw new ProgramInvocationException("The program's entry point class '" + className + "' caused an exception during initialization: " + t.getMessage, t)
      }
    } finally if (contextCl != null) Thread.currentThread.setContextClassLoader(contextCl)
  }

}

object PackagedProgram {
  /**
    * Property name of the entry in JAR manifest file that describes the Flink specific entry point.
    */
  val MANIFEST_ATTRIBUTE_ASSEMBLER_CLASS = "program-class"
  /**
    * Property name of the entry in JAR manifest file that describes the class with the main method.
    */
  val MANIFEST_ATTRIBUTE_MAIN_CLASS = "Main-Class"

  /**
    * Takes all JAR files that are contained in this program's JAR file and extracts them
    * to the system's temp directory.
    *
    * @return The file names of the extracted temporary files.
    * @throws ProgramInvocationException Thrown, if the extraction process failed.
    */
  @throws[ProgramInvocationException]
  def extractContainedLibraries(jarFile: URL): List[File] = {
    val rnd = new Random
    var jar : JarFile = null
    try{
      jar = new JarFile(new File(jarFile.toURI))
      val containedJarFileEntries = new ListBuffer[JarEntry]
      val entries = jar.entries
      while (entries.hasMoreElements) {
        val entry = entries.nextElement
        val name = entry.getName
        if (name.length > 8 && name.startsWith("lib/") && name.endsWith(".jar")) {
          containedJarFileEntries += entry
        }
      }
      if (containedJarFileEntries.isEmpty) {
        return List.empty[File]
      }else {
        // go over all contained jar files
        val extractedTempLibraries = new ListBuffer[File]()
        val buffer = new Array[Byte](4096)
        var incomplete = true
        try {
          for (i <- 0 to containedJarFileEntries.size) {
            {
              val entry = containedJarFileEntries(i)
              var name = entry.getName
              name = name.replace(File.separatorChar, '_')
              var tempFile: File = null
              try {
                tempFile = File.createTempFile(rnd.nextInt(Integer.MAX_VALUE) + "_", name)
                tempFile.deleteOnExit()
              } catch {
                case e: IOException => {
                  throw new ProgramInvocationException("An I/O error occurred while creating temporary file to extract nested library '" + entry.getName + "'.", e)
                }
              }
              extractedTempLibraries += tempFile
              // copy the temp file contents to a temporary File
              var out: OutputStream = null
              var in: InputStream = null
              try {
                out = new FileOutputStream(tempFile)
                in = new BufferedInputStream(jar.getInputStream(entry))
                var numRead = 0
                while ((numRead = in.read(buffer)) != -1) out.write(buffer, 0, numRead)
              } catch {
                case e: IOException => {
                  throw new ProgramInvocationException("An I/O error occurred while extracting nested library '" + entry.getName + "' to temporary file '" + tempFile.getAbsolutePath + "'.")
                }
              } finally {
                if (out != null) out.close()
                if (in != null) in.close()
              }
            }
          }
          incomplete = false
        }finally {
          if (incomplete) {
            deleteExtractedLibraries(extractedTempLibraries.toList)
          }
        }
        return extractedTempLibraries.toList
      }

    }catch {
      case t: Throwable => {
        throw new ProgramInvocationException("Unknown I/O error while extracting contained jar files.", t)
      }
    } finally if (jar != null) try
      jar.close()

    catch {
      case t: Throwable => {
      }
    }
  }

  def deleteExtractedLibraries(tempLibraries:List[File]) {
    for (f <- tempLibraries) {
      f.delete
    }
  }

  def callMainMethod(entryClass : Class[_] ,args : Array[String]) ={

    var mainMethod : Method = null

    if(!Modifier.isPublic(entryClass.getModifiers)) {
      throw new ProgramInvocationException("The class " + entryClass.getName + " must be public.")
    }

    try{
      mainMethod = entryClass.getMethod("main",classOf[Array[String]])

    }catch {
      case e: NoSuchElementException => throw new ProgramInvocationException("The class " + entryClass.getName + " has no main(String[]) method.")
      case t: Throwable =>{
        throw new ProgramInvocationException("Could not look up the main(String[]) method from the class " + entryClass.getName + ": " + t.getMessage, t)
      }
    }
    if (!Modifier.isStatic(mainMethod.getModifiers)) throw new ProgramInvocationException("The class " + entryClass.getName + " declares a non-static main method.")
    if (!Modifier.isPublic(mainMethod.getModifiers)) throw new ProgramInvocationException("The class " + entryClass.getName + " declares a non-public main method.")

    try {
      mainMethod.invoke(null, args)
    } catch {
      case e: IllegalArgumentException=> throw new ProgramInvocationException("Could not invoke the main method, arguments are not matching.", e)
      case e: IllegalAccessException => throw new ProgramInvocationException("Access to the main method was denied: " + e.getMessage, e)
      case e: InvocationTargetException => {
        val exceptionInMethod = e.getTargetException
        if (exceptionInMethod.isInstanceOf[Error]) throw exceptionInMethod.asInstanceOf[Error]
        else if (exceptionInMethod.isInstanceOf[ProgramParametrizationException]) throw exceptionInMethod.asInstanceOf[ProgramParametrizationException]
        else if (exceptionInMethod.isInstanceOf[ProgramInvocationException]) throw exceptionInMethod.asInstanceOf[ProgramInvocationException]
        else throw new ProgramInvocationException("The main method caused an error.", exceptionInMethod)
      }
      case t: Throwable => {
        throw new ProgramInvocationException("An error occurred while invoking the program's main method: " + t.getMessage, t)
      }
    }
  }

}
