package pl.mpieciukiewicz.ships.actors

import pl.mpieciukiewicz.ships.model.GameEvent
import akka.actor.{PoisonPill, ActorRef, Actor}
import GameEventListener._
import pl.mpieciukiewicz.ships.actors.ShipGame.RegisterListener


object GameEventListener {
  case class GetGameEvents(gameId: Int, playerId: Int, eventsHandled: Int)
  case class GameEvents(events: List[GameEvent])
}


class GameEventListener(counter: Int) extends Actor {



  var respondTo: ActorRef = _

  var userId: Int = -1

  def receive = {
    case getGameEvents: GetGameEvents => {
      val game = SelectActor.game(getGameEvents.gameId)
      game ! RegisterListener(getGameEvents.playerId, getGameEvents.eventsHandled, self)
      userId = getGameEvents.playerId
      respondTo = sender
      println("Listening to events for user " + userId + " in listener " + counter)
    }
    case gameEvents: GameEvents => {
      println("Event occured, broadcasting to " + userId + " from listener " + counter)
      respondTo ! gameEvents.events
      self ! PoisonPill
    }
    case message => println(message)
  }
}
