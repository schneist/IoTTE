package python.pythonshell

import java.util.UUID

import monix.eval.Task
import monix.execution.{Callback, Cancelable}
import python.pythonshell.PythonShellJS.PythonShell


object PythonShellS {

  import scala.scalajs.js
  import js.Dynamic.{global => g}
  import js.DynamicImplicits._

  val fs = g.require("fs")

  def run(script:String,pythonOption:Seq[String] = Seq.empty):Task[String] = Task.create { (_, callback) =>
    val scriptsPath= """/tmp/scripts/"""
    val fileName = """script"""  + UUID.randomUUID().toString + ".py"

    fs.writeFile(scriptsPath+fileName,script + sys.props("line.separator") , { err: js.Dynamic =>
      if (err) {
        callback.onError(new Exception("Error creating script "+ scriptsPath+fileName +" " +err.toString))

      }else{
        PythonShell.run(
          fileName,
          new PythonShellOptions {
            override val pythonOptions = if (pythonOption.isEmpty)   js.undefined else pythonOption
            override val scriptPath = scriptsPath
          },
          reportBack(callback)
        )

      }
    })
    Cancelable.empty
  }

  private def reportBack(callback:Callback[Exception,String])(err: scalajs.js.UndefOr[String] = scalajs.js.undefined,result :scalajs.js.UndefOr[String] =  scalajs.js.undefined) :Unit  ={
    if(err.isDefined  && err != null) {
       callback.onError(new Exception(err.toString))
    }
    else {
      callback.onSuccess(result.getOrElse(""))
    }
  }



}
