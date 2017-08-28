package com.xten.tide.function.examples.base

import com.xten.tide.runtime.api.environment.ExecutionEnvironment
import com.xten.tide.runtime.api.event.{EmptyEvent, IEvent, IntEvent}
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
  source.map(new WordCountMapFunction).print()

  env.execute("wordCount")


}

class WordCountMapFunction extends MapFunction{

  override def map(value: IEvent): IEvent = {

    EmptyEvent()
  }

}

class WordCountSourceFunction extends SourceFunction{
  @throws[Exception]
  override def run(ctx: SourceContext): Unit = {
    var i: Int = 0
    while (i < 10000) {
      {

        ctx.collect(IntEvent(i))
        Thread.sleep(100)
      }
      {
        i += 1; i - 1
      }
    }
  }

  override def cancel(): Unit = ???
}

