package definitions

import dsl.elements.OperationDefinition
import johnnyfivescalajs.JohnnyFive.{Board, _}
import org.reactivestreams.{Publisher, Subscriber, Subscription}

import scala.scalajs.js.Date

object J5Definitions {

  type ProdIntBool =  Product2[Int,Boolean]

  def switchLED = new OperationDefinition[ProdIntBool , Boolean ,(ProdIntBool,Board) => Boolean ](
    (in, board) => {
      board.digitalWrite(in._1,if( in._2) 1 else 0)
      !in._2
    }
  )

  def sinkBlinkLEDLength :OperationDefinition[(Int,Double) , Double ,((Int,Double),Board) => Subscriber[Double] ]= new OperationDefinition[(Int,Double) , Double ,((Int,Double),Board) => Subscriber[Double] ](
    (in, _) => {
      val led =  Led(new LedOption { pin = in._1.toDouble})
      val s = new Subscriber[Double] {
        override def onSubscribe(s: Subscription): Unit = {}

        override def onNext(elem: Double): Unit = {
          led.pulse(elem)
        }

        override def onError(ex: Throwable): Unit = {led.blink(110.1)}

        override def onComplete(): Unit = led.off()
      }
      s.onNext(in._2)
      s
    }
  )

  def waitForButtonJ5 : (Int,Board,Subscriber[Unit]) => Unit = (pin:Int,b :Board, cb:Subscriber[Unit]) =>{
    Button(pin)
      .on("hold", () => {
        cb.onNext()
        cb.onComplete
      })
  }

  def waitForButton =
    new OperationDefinition[Int, Unit ,(Int,Board,Subscriber[Unit]) => Unit ](waitForButtonJ5)


  def sourceButtonDownLength =
    new OperationDefinition[Int, Double ,(Int,Board) => Publisher[Double] ]((pinI:Int,_)=> {
      (subscriber: Subscriber[_ >: Double]) => {
        val b = Button(new ButtonOption {pin = pinI;isPullup = true})
        var downtime :Double = Date.now()
        b.on("down", () => {
          downtime = Date.now()
        })
        b.on("up", () => {
          subscriber.onNext( Date.now() - downtime )
        })
      }
    })
}