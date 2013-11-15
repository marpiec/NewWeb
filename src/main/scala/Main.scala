import akka.actor._
import akka.util.Timeout
import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag
import spray.http.StatusCodes
import spray.routing._


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



trait ActorDirectives { this: HttpService =>
  import JSON._

  implicit val timeout = Timeout(5000)

  def askActor[T](actor: ActorRef)(implicit messageType: ClassTag[T], ec: ExecutionContext) = {
    extract(_.request.entity.data.asString) { json =>
      complete {
        (actor ? fromJson(json, messageType.runtimeClass)).map(toJson)
      }
    }
  }
  def tellActor[T](actor: ActorSelection)(implicit messageType: ClassTag[T], ec: ExecutionContext) = {
    extract(_.request.entity.data.asString) { json =>
      complete {
        (actor ! fromJson(json, messageType.runtimeClass))
        "{}"
      }
    }
  }
}


trait DefaultRouter extends HttpService with ActorDirectives {
  self: DefaultListener =>
  import JSON._

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

  var listenerCounter = 0

  private val shipGameCreator = actorRefFactory.actorOf(Props[ShipGameCreator], "gameCreator")

  val route = {
    pathPrefix("rest") {
      get {
        path("") {
          complete("This is index!")
        } ~
        path("gameEvents" / IntNumber / IntNumber / IntNumber) { (gameId, playerId, eventsHandled) => {
            complete {
              val gameEventListener = context.actorOf(Props(classOf[GameEventListener], listenerCounter))
              listenerCounter += 1
              (gameEventListener.?(GetGameEvents(gameId, playerId, eventsHandled))(Timeout(30000)).map(toJson))
            }
          }
        }
      } ~
      post {
        path("joinAGame") {
          askActor[JoinAGameMessage](shipGameCreator)
        } ~
        path("game" / IntNumber / "fire") { gameId =>
          val game = context.actorSelection("akka://example/user/listener/gameCreator/game" + gameId)
          tellActor[PlayerFired](game)
        }
      }
    }
  }




}



