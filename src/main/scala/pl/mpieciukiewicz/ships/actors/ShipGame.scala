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
}

/**
 * @author Marcin Pieciukiewicz
 */
class ShipGame(val gameId: Int, val firstUserJoined: UserJoined) extends Actor {



  val gameEvents = ListBuffer[GameEvent](new GameEvent(firstUserJoined))
  val eventOneTimeListeners = ListBuffer[RegisterListener]()

  var playerA = firstUserJoined.playerId
  var playerB = -1

  var playerAShips = firstUserJoined.playerShips
  var playerBShips = List[XY]()

  var playerTurn = playerA

  def receive: Actor.Receive = {

    case userJoined: UserJoined => {
      addGameEventAndBroadcastIt(userJoined)
      playerB = userJoined.playerId
      playerBShips = userJoined.playerShips
    }
    case userFired: PlayerFired => {
      if (userFired.playerId == playerTurn) {
        addGameEventAndBroadcastIt(userFired)
        playerTurn = if (playerTurn == playerA) playerB else playerA
      }
    }

    case registerListener: RegisterListener => {
      eventOneTimeListeners += registerListener
      println("+++ Registering lister for user " + registerListener.playerId + " now there are " + eventOneTimeListeners.size + " listeners")
      broadcastEvents(registerListener)
    }

    case message => println(message)
  }


  def addGameEventAndBroadcastIt(event: AnyRef) {
    gameEvents += new GameEvent(event)

    eventOneTimeListeners.toList.foreach(listener => {
      broadcastEvents(listener)
    })
  }

  def broadcastEvents(listener: RegisterListener) {
    val eventsToSend: List[GameEvent] = gameEvents.slice(listener.eventsHandled, gameEvents.size).toList
    if (eventsToSend.nonEmpty) {
      println("--- Bradcasting " + eventsToSend.size + " messages to user " + listener.playerId + "listeners count " + eventOneTimeListeners.size)
      eventOneTimeListeners -= listener //???
      listener.listener ! GameEvents(eventsToSend)
    }
  }
}
