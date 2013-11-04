import akka.actor._
import akka.io.IO
import spray.can.Http
import spray.routing.HttpService
import JSON._

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



trait DefaultRouter extends HttpService {


  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

  val route = {
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

    }
  }

}




//object Main extends App {
//
//  val system = ActorSystem()
//
//  private val httpListener: ActorRef = system.actorOf(Props[DefaultListener], "listener")
//
//  IO(Http)(system) ! Http.Bind(httpListener, interface = "localhost", port = 8080)
//
//
//
//
//  println("Hello World!")
//
//}