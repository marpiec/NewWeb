import akka.actor.Actor

/**
 *
 */
class ShipGame extends Actor {

  def receive: Actor.Receive = {
    case message: JoinAGameMessage => {
      println("UserJoinedMessage")
      sender ! UserJoinedGame(142)
    }
    case _ => println(receive)
  }

}
