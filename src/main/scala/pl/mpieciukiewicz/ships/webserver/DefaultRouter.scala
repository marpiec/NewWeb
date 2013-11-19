package pl.mpieciukiewicz.ships.webserver

import akka.actor._
import akka.util.Timeout
import pl.mpieciukiewicz.ships.actors.{SelectActor, GameEventListener, ShipGameCreator}

import spray.routing._


import pl.mpieciukiewicz.ships.webserver.sprayutil.ActorDirectives
import pl.mpieciukiewicz.ships.actors.GameEventListener.GetGameEvents
import pl.mpieciukiewicz.ships.actors.ShipGameCreator.JoinAGame
import pl.mpieciukiewicz.ships.actors.ShipGame.PlayerFired
import pl.mpieciukiewicz.ships.util.{MyXMLSerializer, MyJSONSerializer}
import spray.http.HttpHeaders.`Content-Type`
import spray.http.ContentType
import pl.mpieciukiewicz.ships.model.XY


class DefaultRouter extends HttpService with Actor with ActorDirectives {

  var listenerCounter = 0

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

  def actorRefFactory = context

  def receive = runRoute(route)


  val contentType = optionalHeaderValuePF { case `Content-Type`(ct) => ct }

  val route = {
    pathPrefix("rest") {
      get {
        path("") {
          complete("This is index!")
        } ~
        path("gameEvents" / IntNumber / IntNumber / IntNumber) { (gameId, playerId, eventsHandled) => {
          contentType { ct =>
              implicit val serializer = getSerializer(ct)
              complete {
                val gameEventListener = context.actorOf(Props(classOf[GameEventListener], listenerCounter))
                listenerCounter += 1
                askActor(gameEventListener, GetGameEvents(gameId, playerId, eventsHandled), Timeout(30000))
              }
            }
          }
        }
      } ~
      post {
        contentType { ct =>
          implicit val serializer = getSerializer(ct)
          path("joinAGame") {
            askActorWithMessage[JoinAGame](SelectActor.gameCreator())
          } ~
          path("game" / IntNumber / "fire") { gameId =>
            tellActorTheMessage[PlayerFired](SelectActor.game(gameId))
          }
        }
      }
    }
  }


  def getSerializer(contentType:Option[ContentType]) = {
    if(contentType.isDefined && contentType.get.mediaType.value.equals("application/xml")) {
      MyXMLSerializer
    } else {
      MyJSONSerializer
    }
  }


}



