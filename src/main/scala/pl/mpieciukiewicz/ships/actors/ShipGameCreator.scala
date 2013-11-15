package pl.mpieciukiewicz.ships.actors

import akka.actor.{ActorRef, Actor, Props}
import pl.mpieciukiewicz.ships.model.XY
import ShipGameCreator._
import pl.mpieciukiewicz.ships.actors.ShipGame.UserJoined


object ShipGameCreator {
  case class JoinAGameMessage(ships: List[XY])
  case class UserJoinedGame(gameId: Int, playerId: Int)
}

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

