package com.xten.tide.runtime.example

import com.xten.tide.runtime.api.environment.ExecutionEnvironment
import com.xten.tide.runtime.api.event.{IEvent, IntEvent}
import com.xten.tide.runtime.api.functions.source.{SourceContext, SourceFunction}
import com.xten.tide.runtime.api.functions.MapFunction

/**
  * Created with IntelliJ IDEA.
  * User: kongqingyu
  * Date: 2017/5/3
  */
object WordCount extends App{

  val env = ExecutionEnvironment.getExecutionEnvironment()

  val source = env.addSource(new WordCountSourceFunction)
  source.map(new WordCountMapFunction).name("WordCountMap")
    .print()

  env.execute("wordCount")


}

class WordCountMapFunction extends MapFunction{

  override def map(value: IEvent): IEvent = {
    println(value)
    value
  }

}

class WordCountSourceFunction extends SourceFunction{
  @throws[Exception]
  override def run(ctx: SourceContext): Unit = {
    var i: Int = 0
    while (i < 1) {
      {
        println(s"source is ${i}")
        ctx.collect(IntEvent(i))
        Thread.sleep(100)
      }
      {
        i += 1;
      }
    }
  }

  override def cancel(): Unit = ???
}

