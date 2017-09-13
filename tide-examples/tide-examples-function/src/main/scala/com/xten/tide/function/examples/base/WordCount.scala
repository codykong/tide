package com.xten.tide.function.examples.base

import com.xten.tide.runtime.api.environment.ExecutionEnvironment
import com.xten.tide.runtime.api.event.{EmptyEvent, IEvent, IntEvent}
import com.xten.tide.runtime.api.functions.source.{SourceContext, SourceFunction}
import com.xten.tide.runtime.api.functions.MapFunction
import org.slf4j.LoggerFactory

/**
  * Created with IntelliJ IDEA.
  * User: kongqingyu
  * Date: 2017/5/3
  */
object WordCount extends App{

  val env = ExecutionEnvironment.getExecutionEnvironment()

  val source = env.addSource(new WordCountSourceFunction)
  source.map(new WordCountMapFunction).print()

  env.execute("wordCount")


}

class WordCountMapFunction extends MapFunction{
  val LOG = LoggerFactory.getLogger(this.getClass)

  override def map(value: IEvent): IEvent = {

    LOG.info(s"${this} send ${value}")
    EmptyEvent()
  }

}

class WordCountSourceFunction extends SourceFunction{
  val LOG = LoggerFactory.getLogger(this.getClass)

  @throws[Exception]
  override def run(ctx: SourceContext): Unit = {
    var i: Int = 0
    while (i < 10) {
      {

        ctx.collect(IntEvent(i))
        LOG.info(s"${this} send ${i}")
        Thread.sleep(100)
      }
      {
        i += 1; i - 1
      }
    }
  }

  override def cancel(): Unit = ???
}

