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
case class GetGameEvents(gameId:Int, playerId:Int)

class GameEvent(val event: AnyRef) {
  val name = event.getClass.getSimpleName
}


class ShipGame(val gameId: Int, val userAJoined:UserJoined) extends Actor {

  var gameEvents = ListBuffer[GameEvent](new GameEvent(userAJoined))
  val eventsSent = mutable.Map(0 -> 0, 1 -> 0)

  def receive: Actor.Receive = {

    case message:GetGameEvents => {
      println("GetGameEvents received "+message)
      sender ! gameEvents.slice(eventsSent(message.playerId), gameEvents.size)
      eventsSent(message.playerId) = gameEvents.size
    }


    case _ => println(receive)
  }
}
