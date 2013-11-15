package pl.mpieciukiewicz.ships.actors

import akka.actor.{ActorSystem, Props, ActorContext}

/**
 * @author Marcin Pieciukiewicz
 */
object Initialization {

  def createActors(context: ActorSystem) {

    context.actorOf(Props[ShipGameCreator], "gameCreator")

  }

}
