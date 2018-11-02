import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.unmarshalling.StringUnmarshallers;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.util.concurrent.CompletionStage;


public class HttpServerActorInteractionExample extends AllDirectives {

    private final ActorRef temperatureSensor;

    public static void main(String[] args) throws Exception {
        // boot up server using the route as defined below
        ActorSystem actorSystem = ActorSystem.create("routes");

        final Http http = httputil.getHttp(actorSystem);
        final ActorMaterializer materializer = ActorMaterializer.create(actorSystem);

        //In order to access all directives we need an instance where the routes are define.
        HttpServerActorInteractionExample app = new HttpServerActorInteractionExample(actorSystem);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(actorSystem, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost("localnpm revbrunhost", 8080), materializer);

        System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
        System.in.read(); // let it run until user presses return

        binding
                .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
                .thenAccept(unbound -> actorSystem.terminate()); // and shutdown when done
    }

    private HttpServerActorInteractionExample(final ActorSystem system) {
        temperatureSensor = system.actorOf(TemperatureSensor.props("1"), "temperaturesys");
    }

    private Route createRoute() {
        return route(
                path("temperature", () -> route(
                        post(() ->
                                parameter(StringUnmarshallers.DOUBLE, "temperature", tempValue -> {
                                            // place a bid, fire-and-forget
                                            temperatureSensor.tell(new TemperatureSensor.RecordTemperature(System.currentTimeMillis(), tempValue), ActorRef.noSender());
                                            return complete(StatusCodes.ACCEPTED, "temperature placed");
                                        }
                                ))

                )));
    }


}