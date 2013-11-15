package pl.mpieciukiewicz.ships.webserver

import akka.actor._
import akka.util.Timeout
import pl.mpieciukiewicz.ships.actors.{SelectActor, GameEventListener, ShipGameCreator}

import spray.routing._


import pl.mpieciukiewicz.ships.webserver.sprayutil.ActorDirectives
import pl.mpieciukiewicz.ships.actors.GameEventListener.GetGameEvents
import pl.mpieciukiewicz.ships.actors.ShipGameCreator.JoinAGameMessage
import pl.mpieciukiewicz.ships.actors.ShipGame.PlayerFired



class DefaultRouter extends HttpService with Actor with ActorDirectives {

  var listenerCounter = 0

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

  def actorRefFactory = context

  def receive = runRoute(route)

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
              askActor(gameEventListener, GetGameEvents(gameId, playerId, eventsHandled), Timeout(30000))
            }
          }
        }
      } ~
      post {
        path("joinAGame") {
          askActorWithMessage[JoinAGameMessage](SelectActor.gameCreator())//shipGameCreator)
        } ~
        path("game" / IntNumber / "fire") { gameId =>
          tellActorTheMessage[PlayerFired](SelectActor.game(gameId))
        }
      }
    }
  }




}



