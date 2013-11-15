import akka.actor.{ActorRef, Props, Actor}
import scala.collection.mutable.ListBuffer
import scala.collection.mutable

/**
 *
 */

class ShipGameCreator extends Actor {

  var gamesCounter = 12

  var lastGameOption:Option[ActorRef] = None

  def receive: Actor.Receive = {
    case message: JoinAGameMessage => {

      val currentGameId = gamesCounter


      lastGameOption match {
        case None => lastGameOption = Some(context.actorOf(Props(classOf[ShipGame], gamesCounter, UserJoined(0, message.ships)), "game"+currentGameId))
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


case class RegisterListener(playerId: Int, listener:ActorRef)

class GameEvent(val event: AnyRef) {
  val name = event.getClass.getSimpleName
}



class ShipGame(val gameId: Int, val userAJoined:UserJoined) extends Actor {

  val gameEvents = ListBuffer[GameEvent](new GameEvent(userAJoined))
  val eventsSent = mutable.Map(0 -> 0, 1 -> 0)
  val eventListeners = ListBuffer[RegisterListener]()
  
  
  def receive: Actor.Receive = {

    case userJoined: UserJoined => {
      println("Second user Joined!")
      gameEvents += new GameEvent(userJoined)
    }
//    case getGameEvents:GetGameEvents => {
//      println("GetGameEvents received "+getGameEvents)
//      sender ! gameEvents.slice(eventsSent(getGameEvents.playerId), gameEvents.size)
//      eventsSent(getGameEvents.playerId) = gameEvents.size
//    }
    case registerListener: RegisterListener => {
      eventListeners += registerListener
      broadcastEvents(registerListener)
    }

    case message => println(message)
  }


  def addGameEventAndBroadcastIt(event: AnyRef) {
    gameEvents += new GameEvent(event)
    
    eventListeners.foreach(listener => {
      broadcastEvents(listener)
    })
  }

  def broadcastEvents(listener: RegisterListener) {
    val eventsToSend: List[GameEvent] = gameEvents.slice(eventsSent(listener.playerId), gameEvents.size).toList
    if(eventsToSend.nonEmpty) {
      sender ! GameEvents(eventsToSend)
      eventsSent(listener.playerId) = gameEvents.size
    }
  }
}

case class GetGameEvents(val gameId: Int, val playerId:Int)
case class GameEvents(events:List[GameEvent])

class GameEventListener extends Actor {
  var respondTo: ActorRef = _

  def receive = {
    case getGameEvents: GetGameEvents => {
      val game = context.actorSelection("akka://example/user/listener/gameCreator/game"+getGameEvents.gameId)
      game ! RegisterListener(getGameEvents.playerId, self)
      respondTo = sender
    }
    case gameEvents: GameEvents => {
      respondTo ! gameEvents.events
    }
    case message => println(message)
  }
}


















