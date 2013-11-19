package pl.mpieciukiewicz.ships.webserver.sprayutil

import spray.routing.HttpService
import akka.util.Timeout
import akka.actor.{ActorSelection, ActorRef}
import scala.reflect.ClassTag
import scala.concurrent.ExecutionContext
import akka.pattern.ask
import pl.mpieciukiewicz.ships.util.Serializer


/**
 * @author Marcin Pieciukiewicz
 */
trait ActorDirectives {
  this: HttpService =>

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler

  implicit val defaultTimeout = Timeout(5000)
  val emptyObject = ""

  def askActorWithMessage[T](actor: ActorSelection)(implicit messageType: ClassTag[T], ec: ExecutionContext, serializer: Serializer) = {
    extract(_.request.entity.data.asString) {
      dataString =>
        complete {
          (actor ? serializer.deserialize(dataString, messageType.runtimeClass)).map(serializer.serialize)
        }
    }
  }

  def tellActorTheMessage[T](actor: ActorSelection)(implicit messageType: ClassTag[T], ec: ExecutionContext, serializer: Serializer) = {
    extract(_.request.entity.data.asString) {
      dataString =>
        complete {
          actor ! serializer.deserialize(dataString, messageType.runtimeClass)
          emptyObject
        }
    }
  }

  def askActor(actor: ActorRef, message: AnyRef)(implicit ec: ExecutionContext, serializer: Serializer) = {
    actor.ask(message).map(serializer.serialize)
  }

  def askActor(actor: ActorRef, message: AnyRef, timeout: Timeout)(implicit ec: ExecutionContext, serializer: Serializer) = {
    actor.ask(message)(timeout).map(serializer.serialize)
  }

}
