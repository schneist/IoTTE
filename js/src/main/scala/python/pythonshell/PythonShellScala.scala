package python.pythonshell

import monix.eval.Task
import monix.execution.Cancelable
import python.pythonshell.ModeEnum.Mode
import python.pythonshell.PythonShellIO.PythonShellScalaIO
import python.pythonshell.PythonShellJS.{PythonShell, PythonShellJSIO}
import scalaz.zio.{ExitResult, IO}
import shapeless.{:+:, CNil, Coproduct}

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.{UndefOr, |}

object PythonShellIO{

  type PythonShellScalaIO = Either[String, PythonShellResult]
  type PythonShellResult =  String :+: Seq[Byte] :+: CNil

  implicit def from: String|Seq[Byte] => PythonShellScalaIO =
    p => {
       println ((p:Any))
      (p:Any)  match {
        case s:String => Right(Coproduct[PythonShellResult](s))
        case b:Seq[Byte] => Right(Coproduct[PythonShellResult](b))
        case pp:Any => Right(Coproduct(pp.toString))
      }
    }
}

class PythonShellScala(mode: Mode) {

  def runIO(script:String, pythonShellOptions: PythonShellOptions):IO[Exception,PythonShellScalaIO] =  {
    IO.async[Exception,PythonShellScalaIO](callback  => {
      runString(
        script,
        pythonShellOptions,
        _ match {
          case left: Left[String,PythonShellJSIO] => callback.apply(IO.done( ExitResult.checked(new Exception(left.value))))
          case right: Right[String,PythonShellJSIO] => callback.apply(IO.done( ExitResult.succeeded(PythonShellIO.from(right.value))))
        }
      )
    })
  }

  def runString(script:String, options:PythonShellOptions, callback:(Either[String, PythonShellJSIO]) => Unit) :PythonShell = {
    def transform :(js.UndefOr[String], js.UndefOr[PythonShellJSIO] ) => Either[String,PythonShellJSIO] = (err, result) => {
      if(err.isDefined  && err != null) {
        Left.apply[String,PythonShellJSIO](err.get)
      }
      else {
        Right.apply[String,PythonShellJSIO](result.get)
      }
    }
    PythonShell.runString(script,options.toPY,(x:js.UndefOr[String],y:js.UndefOr[PythonShellJSIO]) => callback.apply(transform(x,y)))
  }

  def runTask(script:String,pythonShellOptions:PythonShellOptions):Task[PythonShellScalaIO] = {
    Task.create { (_, callback) =>
      runString(
        script,
        pythonShellOptions,
        _ match {
          case left: Left[String,PythonShellJSIO] => callback.onError(new Exception(left.value))
          case right: Right[String,PythonShellJSIO] => callback.onSuccess(PythonShellIO.from(right.value))
        }
      )
      Cancelable.empty
    }
  }


}

object EventEnum  extends Enumeration {
  type  Event = Value
  val message,stderr,close,error = Value
}

object ModeEnum  extends Enumeration {
  type Mode = Value
  val text,json,binary = Value
}

case class PythonShellOptions(
                               mode: Option[ Mode],
                               pythonPath :Option[String],
                               scriptPath : Option[String],
                               pythonOptions : Option[Seq[String]],
                               encoding : Option[String],
                               args : Option[Seq[String]]
                             ){

  private[pythonshell] implicit def toPY : PythonShellJS.PythonShellOptions = new PythonShellJS.PythonShellOptions {

    override val mode: UndefOr[String] = mode
    override val args: UndefOr[Seq[String]] = args
    override val encoding: UndefOr[String] = encoding
    override val pythonOptions: UndefOr[Seq[String]] = pythonOptions
    override val pythonPath: UndefOr[String] = pythonPath
    override val scriptPath: UndefOr[String] = scriptPath

  }
}



object PythonShellOptions{


  def empty:PythonShellOptions = new PythonShellOptions (
    mode = None,
    pythonPath = None,
    scriptPath = None,
    pythonOptions = None,
    encoding = None,
    args = None
  )

}