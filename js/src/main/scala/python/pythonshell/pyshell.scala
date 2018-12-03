package python.pythonshell

import python.pythonshell.Mode.Mode

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSImport("python-shell", JSImport.Namespace)
object PythonShellJS extends js.Object{

  @js.native
  object PythonShell extends js.Object {

    def run(pathToScript:String,options:PythonShellOptions,callback:js.Function2[js.UndefOr[String],js.UndefOr[String],Unit]) :Unit = js.native
    def runString(Script:String,options:PythonShellOptions,callback:js.Function2[js.UndefOr[String], js.UndefOr[String] ,Unit]) :Unit = js.native

  }

}

object Mode  extends Enumeration {
  type Mode = Value
  val text,json,binary = Value
}

trait PythonShellOptions extends js.Object {
  val mode: js.UndefOr[ Mode] = js.undefined
  val pythonPath : js.UndefOr[String] = js.undefined
  val scriptPath : js.UndefOr[String] = js.undefined
  val pythonOptions : js.UndefOr[Seq[String]] = js.undefined
}
