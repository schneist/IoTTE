import akka.*;
import akka.actor.*;
import akka.actor.ActorRef;
import akka.event.*;
import org.junit.*;

import akka.testkit.javadsl.TestKit;
import org.scalatest.junit.JUnitSuite;

import java.util.Optional;

public class DevTest  extends JUnitSuite{

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testReplyWithLatestTemperatureReading() {
        TestKit probe = new TestKit(system);
        ActorRef deviceActor = system.actorOf(TemperatureSensor.props( "device"));

        deviceActor.tell(new TemperatureSensor.RecordTemperature(1L, 24.0), probe.getRef());
        Assert.assertEquals(1L, probe.expectMsgClass(TemperatureSensor.TemperatureRecorded.class).requestId);

        deviceActor.tell(new TemperatureSensor.ReadTemperature(2L), probe.getRef());
        TemperatureSensor.RespondTemperature response1 = probe.expectMsgClass(TemperatureSensor.RespondTemperature.class);
        Assert.assertEquals(2L, response1.requestId);
        Assert.assertEquals(Optional.of(24.0), response1.value);

        deviceActor.tell(new TemperatureSensor.RecordTemperature(3L, 55.0), probe.getRef());
        Assert.assertEquals(3L, probe.expectMsgClass(TemperatureSensor.TemperatureRecorded.class).requestId);

        deviceActor.tell(new TemperatureSensor.ReadTemperature(4L), probe.getRef());
        TemperatureSensor.RespondTemperature response2 = probe.expectMsgClass(TemperatureSensor.RespondTemperature.class);
        Assert.assertEquals(4L, response2.requestId);
        Assert.assertEquals(Optional.of(55.0), response2.value);


    }
}

