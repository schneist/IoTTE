import akka.actor.ActorSystem
import akka.http.javadsl.Http

object httputil {

  def getHttp(system: ActorSystem) = Http.get(system);
}
