package pl.mpieciukiewicz.ships.actors

import pl.mpieciukiewicz.ships.model.{GameEvent, XY}
import akka.actor.{Actor, ActorRef}
import ShipGame._
import scala.collection.mutable.ListBuffer
import pl.mpieciukiewicz.ships.actors.GameEventListener.GameEvents

object ShipGame {
  case class UserJoined(playerId: Int, playerShips: List[XY])
  case class RegisterListener(playerId: Int, eventsHandled: Int, listener: ActorRef)
  case class PlayerFired(playerId: Int, location: XY)
  
  
  case class CurrentUserJoined(playerShips: List[XY])
  case class OpponentJoined()
  case class Fired(playerId: Int, location: XY, hit: Boolean)
}

/**
 * @author Marcin Pieciukiewicz
 */
class ShipGame(val gameId: Int, val firstUserJoined: UserJoined) extends Actor {


  val playerAEvents = ListBuffer[GameEvent](new GameEvent(CurrentUserJoined(firstUserJoined.playerShips)))
  val playerBEvents = ListBuffer[GameEvent](new GameEvent(OpponentJoined()))

  val eventOneTimeListeners = ListBuffer[RegisterListener]()

  var playerA = firstUserJoined.playerId
  var playerB = -1

  var playerAShips = firstUserJoined.playerShips
  var playerBShips = List[XY]()

  var playerTurn = playerA

  def receive: Actor.Receive = {

    case secondUserJoined: UserJoined => {
      playerAEvents += new GameEvent(OpponentJoined())
      playerBEvents += new GameEvent(CurrentUserJoined(secondUserJoined.playerShips))
      broadcastEvents()
      playerB = secondUserJoined.playerId
      playerBShips = secondUserJoined.playerShips
    }
    case userFired: PlayerFired => {
      if (userFired.playerId == playerTurn) {

        val hit = if (userFired.playerId == playerA)
          playerBShips.contains(userFired.location) else
          playerAShips.contains(userFired.location)

        val fired = Fired(userFired.playerId, userFired.location, hit)

        playerAEvents += new GameEvent(fired)
        playerBEvents += new GameEvent(fired)

        broadcastEvents()

        playerTurn = if (playerTurn == playerA) playerB else playerA
      }
    }

    case registerListener: RegisterListener => {
      eventOneTimeListeners += registerListener
      println("+++ Registering lister for user " + registerListener.playerId + " now there are " + eventOneTimeListeners.size + " listeners")
      broadcastEventsToListener(registerListener)
    }

    case message => println(message)
  }


  def broadcastEvents() {
    eventOneTimeListeners.toList.foreach(broadcastEventsToListener)
  }


  def broadcastEventsToListener(listener: ShipGame.RegisterListener) {
    val events = if (listener.playerId == playerA) playerAEvents else playerBEvents
    broadcastEvents(events, listener)
  }

  def broadcastEvents(events:  ListBuffer[GameEvent], listener: RegisterListener) {
    val eventsToSend: List[GameEvent] = events.slice(listener.eventsHandled, events.size).toList
    if (eventsToSend.nonEmpty) {
      println("--- Bradcasting " + eventsToSend.size + " messages to user " + listener.playerId + "listeners count " + eventOneTimeListeners.size)
      eventOneTimeListeners -= listener //???
      listener.listener ! GameEvents(eventsToSend)
    }
  }
}
