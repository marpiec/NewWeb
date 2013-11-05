import akka.actor._
import akka.io.IO
import akka.util.Timeout
import scala.reflect.ClassTag
import spray.can.Http
import spray.http.{HttpForm, MultipartFormData, HttpRequest}
import spray.httpx.unmarshalling._
import spray.routing.HttpService
import JSON._
import scala.concurrent.duration.Duration

import akka.pattern.ask

case class User(name: String, password: String)

class DefaultListener extends Actor with DefaultRouter {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing,
  // timeout handling or alternative handler registration
  def receive = runRoute(route)

}


class JsonUnmarshaller[T](implicit m: ClassTag[T]) extends FromRequestUnmarshaller[T]() {
  def apply(v1: HttpRequest): _root_.spray.httpx.unmarshalling.Deserialized[T] = {
    Right(fromJson(v1.entity.asString, m.runtimeClass).asInstanceOf[T])
  }
}


trait DefaultRouter extends HttpService { self:DefaultListener =>


  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

  implicit val timeout = Timeout(5000000)

  private val shipGame = actorRefFactory.actorOf(Props[ShipGame], "game")

  val route = {
    pathPrefix("rest") {
    get {
      pathPrefix("rest") {
        path("") {
          complete("This is index!")
        } ~
        path("hello") {
          complete("Hello better world!")
        } ~
        path("user") {
          complete(toJson(User("Marcin","Haslo")))
        }
      }
    } ~
    post {
      path("joinAGame") {
        entity(new JsonUnmarshaller[JoinAGameMessage]) { message =>
          complete {
            (shipGame ? message).map(toJson(_))
          }
        }
      }
    }
    }

  }

}



