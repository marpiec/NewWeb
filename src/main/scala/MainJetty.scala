import org.eclipse.jetty.server.Server

/**
 * @author Marcin Pieciukiewicz
 */
object MainJetty extends App {

  val server = new Server(8080)
  server.start()
  server.join()

}
