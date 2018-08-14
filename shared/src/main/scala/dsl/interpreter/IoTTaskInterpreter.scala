package dsl.interpreter

import dsl.elements.{IoTOperation, OperationDefinition}
import monix.eval.Task
import monix.execution.Cancelable
import org.reactivestreams.{Publisher, Subscriber, Subscription}

class IoTTaskInterpreter[Board] extends  IoTOperation[Task] {

  override type Context = Board

  override def doOnce[In, Out](input: In,
                               operationDefinition: OperationDefinition[In, Out, (In, Board) => Out],
                               ctx: Option[Board] = None
                              ): Task[Out] = {
    Task.create { (_, callback) =>
      callback.onSuccess(operationDefinition.definition(input, ctx.get))
      Cancelable.empty
    }
  }

  override def waitFor[In, Out](input: In,
                                operationDefinition: OperationDefinition[In, Out, (In, Board, Subscriber[Out]) => Unit],
                                ctx: Option[Board] = None
                               ): Task[Out] = {
    Task.create[Out] {
      (_, callback) => {
        val subs =  new Subscriber[Out] {
          override def onSubscribe(s: Subscription): Unit = {s.request(1)}

          override def onNext(t: Out): Unit = callback.onSuccess(t)

          override def onError(t: Throwable): Unit = callback.onError(t)

          override def onComplete(): Unit = {}
        }
        operationDefinition.definition(input, ctx.get, subs)
        Cancelable.empty
      }
    }
  }
  override def source[In, Out](input: In,
                               operationDefinition: OperationDefinition[In, Out, (In, Board) => Publisher[Out]],
                               ctx: Option[Board]
                              ): Task[Publisher[Out]] = {
    Task.pure(operationDefinition.definition(input,ctx.get))
  }

  override def sink[In, Out](
                              input: In,
                              operationDefinition: OperationDefinition[In, Out, (In, Board) => Subscriber[Out]],
                             ctx: Option[Board]): Task[Subscriber[Out]] = {
    Task.pure(operationDefinition.definition(input,ctx.get))
  }
}
