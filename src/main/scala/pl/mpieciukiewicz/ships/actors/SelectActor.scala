package pl.mpieciukiewicz.ships.actors

import akka.actor.ActorContext

/**
 * @author Marcin Pieciukiewicz
 */
object SelectActor {


  def gameCreator()(implicit context: ActorContext) = {
    context.actorSelection("akka://game/user/gameCreator")
  }

  def game(gameId: Int)(implicit context: ActorContext) = {
    context.actorSelection("akka://game/user/gameCreator/game" + gameId)
  }

  
}
