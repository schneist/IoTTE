package demo

import cats.implicits._
import definitions.J5Definitions._
import dsl.elements.IoTOperation
import dsl.interpreter.{IoTTaskInterpreter, MarshalledTaskInterpreter}
import johnnyfivescalajs.{AbstractBinding, AndroidCompBindings}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import python.runner.PythonRunner

import scala.language.{higherKinds, implicitConversions, postfixOps}
import scala.scalajs.js.UndefOr

object Main2 {
  def main4(args: Array[String]): Unit = {

    import johnnyfivescalajs.JohnnyFive._

    implicit val i = new IoTTaskInterpreter[Board]
    implicit val pius = new MarshalledTaskInterpreter[String]
    implicit val run = new PythonRunner[None.type]()
    val bind = new AndroidCompBindings
    val board  = new Board(new BoardOption {
      override val repl =false;
      override val bindings  = bind
    })

    val LedPin = 10
    val ButtonPin = 2



    def program[F[_]:cats.Monad]
    (implicit o :IoTOperation.Aux[F,Board]): F[Unit ] = {
      import cats.syntax.functor._
      import o._

      for {

        button <- source(ButtonPin,sourceButtonDownLength,board)
        subs <- sink[(Int,Double),Double]((LedPin,100),sinkBlinkLEDLength,board)

      } yield button.subscribe(subs)

    }

    board.on("ready", () => {
      println("Johnny ready.")
      program[Task]
        .runAsyncAndForget
    })


  }
}