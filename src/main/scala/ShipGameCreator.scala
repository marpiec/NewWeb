import akka.actor.{PoisonPill, ActorRef, Props, Actor}
import scala.collection.mutable.ListBuffer
import scala.collection.mutable

/**
 *
 */

class ShipGameCreator extends Actor {

  var gamesCounter = 12

  var lastGameOption: Option[ActorRef] = None

  def receive: Actor.Receive = {
    case message: JoinAGameMessage => {

      val currentGameId = gamesCounter


      lastGameOption match {
        case None => lastGameOption = Some(context.actorOf(Props(classOf[ShipGame], gamesCounter, UserJoined(0, message.ships)), "game" + currentGameId))
          sender ! UserJoinedGame(currentGameId, 0)
        case Some(game) =>
          game ! UserJoined(1, message.ships)
          lastGameOption = None
          gamesCounter += 1
          sender ! UserJoinedGame(currentGameId, 1)
      }

    }
    case _ => println(receive)
  }

}


case class UserJoined(playerId: Int, playerShips: List[XY])

case class RegisterListener(playerId: Int, eventsHandled: Int, listener: ActorRef)

case class PlayerFired(playerId: Int, location: XY)

class GameEvent(val event: AnyRef) {
  val name = event.getClass.getSimpleName
}


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

case class GetGameEvents(gameId: Int, playerId: Int, eventsHandled: Int)

case class GameEvents(events: List[GameEvent])

class GameEventListener(counter: Int) extends Actor {
  var respondTo: ActorRef = _

  var userId: Int = -1

  def receive = {
    case getGameEvents: GetGameEvents => {
      val game = context.actorSelection("akka://example/user/listener/gameCreator/game" + getGameEvents.gameId)
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


















