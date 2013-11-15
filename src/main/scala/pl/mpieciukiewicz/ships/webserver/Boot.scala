package pl.mpieciukiewicz.ships.webserver

import akka.actor.{Props, ActorSystem}
import spray.servlet.WebBoot
import pl.mpieciukiewicz.ships.actors.Initialization

/**
 * @author Marcin Pieciukiewicz
 */
class Boot extends WebBoot {

  // we need an ActorSystem to host our application in
  val system = ActorSystem("game")

  // the service actor replies to incoming HttpRequests
  val serviceActor = system.actorOf(Props[DefaultRouter], "listener")

  Initialization.createActors(system)

  system.registerOnTermination {
    // put additional cleanup code here
    system.log.info("Application shut down")
  }
}