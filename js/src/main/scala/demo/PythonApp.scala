package demo

import dsl.elements.{MarshalledOperation, OperationDefinition}
import dsl.interpreter.MarshalledTaskInterpreter
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import python.runner.PythonRunner
import runners.MarshallingRunner
import shapeless.Coproduct

import scala.concurrent.duration._
import scala.language.{higherKinds, implicitConversions, postfixOps}


object PythonApp{

  val pr = new PythonRunner[Unit]()


  implicit val mr = new MarshallingRunner[String,Unit,String=>String] {
    override def runInternal(inR: String, ctx: Option[Unit], definition: String => String): Task[String] = {
      pr.runInternal(Right.apply(Coproduct.apply(inR)),None,definition(inR))
    }.map(_.right.get.select[String].get)
  }

  implicit val mo = new MarshalledTaskInterpreter[String]

  val od = new OperationDefinition[String,String,String => String](a =>"print(2*"+a+")")

  def program[F[_]]
  (implicit o :MarshalledOperation[F]): F[String ] = {
    import o._

    Run("3432432",od)

  }

  def main(args: Array[String]): Unit = {
    program(mo)
      .timeout(10 second)
      .runToFuture
  }
}
