package python.pythonshell

import java.util.UUID

import monix.eval.{Callback, Task}
import monix.execution.Cancelable
import java.nio.file.{Files, Path, Paths}
import java.nio.charset.StandardCharsets


object PythonShell {

  def run(script:String,pythonOption:Seq[String] = Seq.empty):Task[String] = Task.create { (_, callback) =>

    val fileName :String = System.getProperty("java.io.tmpdir") + "/"+ UUID.randomUUID().toString+".py"
    val pathToScript : Path = Files.write(Paths.get( fileName), script.getBytes(StandardCharsets.UTF_8))

    PythonShellJS.run(
      pathToScript.getFileName.toString,
      new PythonShellOptions {
        override val pythonOptions = pythonOption
      },
      reportBackAndDelete(callback,pathToScript)
    )

    Cancelable.empty
  }

  private def reportBack(callback:Callback[String])(err: String, result: String) :Unit  ={
    if(!err.isEmpty) callback.onError(new Exception(err))
    else  callback.onSuccess(result)
  }

  private def reportBackAndDelete(callback:Callback[String],pathToScript:Path)(err: String, result: String) :Unit  = {
    Files.delete(pathToScript)
    reportBack(callback)(err,result)
  }


}
