package demo

import cats.implicits._
import dsl.elements.IoTOperation
import dsl.interpreter.{IoTTaskInterpreter, MarshalledTaskInterpreter}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import python.runner.PythonRunner

import scala.language.implicitConversions
import scala.language.{higherKinds, postfixOps}
import definitions.J5Definitions._

object Main {
  def main(args: Array[String]): Unit = {

    import johnnyfivescalajs.JohnnyFive._

    implicit val i = new IoTTaskInterpreter[Board]
    implicit val pius = new MarshalledTaskInterpreter[String]
    implicit val run = new PythonRunner[None.type]()


    val board  = Board(new BoardOption {override val repl =false})

    val LedPin = 10
    val ButtonPin = 2

    def program[F[_]:cats.Monad]
    (implicit o :IoTOperation.Aux[F,Board]): F[Unit ] = {
      import cats.syntax.functor._
      import o._

      for {

        button <- source(ButtonPin,sourceButtonDownLength,Some(board))
        subs <- sink[(Int,Double),Double]((LedPin,100),sinkBlinkLEDLength,Some(board))

      } yield button.subscribe(subs)

    }



    board.on("ready", () => {
      println("Johnny ready.")
      program[Task]
        .runAsync
    })


  }
}