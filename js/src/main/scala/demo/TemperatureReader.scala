package demo

import cats.implicits._
import definitions.J5Definitions._
import dsl.elements.{IoTOperation, OperationDefinition}
import dsl.interpreter.{IoTTaskInterpreter, MarshalledTaskInterpreter}
import fr.hmil.roshttp.{HttpRequest, Method}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.reactivestreams.{Publisher, Subscriber, Subscription}

import scala.language.{higherKinds, implicitConversions, postfixOps}

object Main3 {
  def main2(args: Array[String]): Unit = {

    import johnnyfivescalajs.JohnnyFive._

    implicit val i = new IoTTaskInterpreter[Board]
    implicit val pius = new MarshalledTaskInterpreter[String]

    val board  = Board(new BoardOption {override val repl =false})

    val ThermoPin = 10



    val request = HttpRequest("http://localhost:8080/temperature").withMethod(Method.POST)

    def sinkhttp : OperationDefinition[HttpRequest,Double,(HttpRequest,Board)=> Subscriber[Double]] = new OperationDefinition[HttpRequest,Double,(HttpRequest,Board) => Subscriber[Double]](
      (request: HttpRequest,_) => new Subscriber[Double] {
        override def onSubscribe(s: Subscription): Unit = {}

        override def onNext(elem: Double): Unit = {
          //request.withQueryParameter("temperature",elem.toString)
          println(elem)
          //request.send()
        }

          override def onError(ex: Throwable): Unit = {}

        override def onComplete(): Unit = {}
      }
    )


    def program[F[_]:cats.Monad]
    (implicit o :IoTOperation.Aux[F,Board]): F[Unit ] = {
      import cats.syntax.functor._
      import o._
      for {
        thermo <- source((1,"DHT11_I2C_NANO_BACKPACK"),sourceTemperatureSensor,board)
        http <- sink(request,sinkhttp,board)
      } yield thermo.subscribe(http)

    }



    board.on("ready", () => {
      println("Johnny ready.")
      program[Task]
        .runAsyncAndForget
    })

    //System.in.read // let it run until user presses return


  }
}