package python.pythonshell

import monix.eval.Task
import monix.execution.{Callback, Cancelable}
import python.pythonshell.PythonShellJS.PythonShell


object PythonShellS {


  def run(script:String,pythonOption:Seq[String] = Seq.empty):Task[String] = Task.create { (_, callback) =>
    println("start PY")
    PythonShell.runString(
      script,
      new PythonShellOptions {
        override val pythonOptions = pythonOption
      },
      reportBack(callback)
    )

    Cancelable.empty
  }

  private def reportBack(callback:Callback[Exception,String])(err: scalajs.js.UndefOr[String] = scalajs.js.undefined,result :scalajs.js.UndefOr[String] =  scalajs.js.undefined) :Unit  ={

    if(!err.isDefined && !err.get.isEmpty) callback.onError(new Exception(err.get))
    else  callback.onSuccess(result.getOrElse(""))
  }



}
