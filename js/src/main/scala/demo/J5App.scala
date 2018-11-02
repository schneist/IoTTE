package demo
import dsl.elements.IoTOperation
import dsl.interpreter.IoTTaskInterpreter
import johnnyfivescalajs.JohnnyFive.Board
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import scala.language.{higherKinds, implicitConversions, postfixOps}

/**


abstract class J5App extends App{

  override def main(args: Array[String]): Unit =
  {

    import johnnyfivescalajs.JohnnyFive._

    implicit val i = new IoTTaskInterpreter[Board]

    val board  = Board(new BoardOption {override val repl =false})

    board.on("ready", () => {
      println("Johnny ready.")
      program[Task](board)
        .runAsync
    })

  }

  def program[F[_]:cats.Monad](board:Board)(implicit o :IoTOperation.Aux[F,Board]): F[Unit ]
}


require('should');
var mockFirmata = require('mock-firmata');
var five = require('johnny-five');
var Board = five.Board;
var Accelerometer = five.Accelerometer;

var board = new Board({
  io: new mockFirmata.Firmata(),
  debug: false,
  repl: false
});
  */