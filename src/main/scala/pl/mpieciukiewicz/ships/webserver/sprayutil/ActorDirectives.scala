package pl.mpieciukiewicz.ships.webserver.sprayutil

import spray.routing.HttpService
import akka.util.Timeout
import akka.actor.{ActorSelection, ActorRef}
import scala.reflect.ClassTag
import scala.concurrent.ExecutionContext
import pl.mpieciukiewicz.ships.util.JSON
import JSON._
import akka.pattern.ask


/**
 * @author Marcin Pieciukiewicz
 */
trait ActorDirectives {
  this: HttpService =>

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler

  implicit val defaultTimeout = Timeout(5000)
  val emptyJsonObject = "{}"



  def askActorWithMessage[T](actor: ActorSelection)(implicit messageType: ClassTag[T], ec: ExecutionContext) = {
    extract(_.request.entity.data.asString) {
      json =>
        complete {
          (actor ? fromJson(json, messageType.runtimeClass)).map(toJson)
        }
    }
  }

  def tellActorTheMessage[T](actor: ActorSelection)(implicit messageType: ClassTag[T], ec: ExecutionContext) = {
    extract(_.request.entity.data.asString) {
      json =>
        complete {
          actor ! fromJson(json, messageType.runtimeClass)
          emptyJsonObject
        }
    }
  }

  def askActor(actor: ActorRef, message: AnyRef)(implicit ec: ExecutionContext) = {
    actor.ask(message).map(toJson)
  }

  def askActor(actor: ActorRef, message: AnyRef, timeout: Timeout)(implicit ec: ExecutionContext) = {
    actor.ask(message)(timeout).map(toJson)
  }

}
