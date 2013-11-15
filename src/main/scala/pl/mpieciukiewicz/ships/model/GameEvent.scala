package pl.mpieciukiewicz.ships.model

/**
 * @author Marcin Pieciukiewicz
 */
class GameEvent(val event: AnyRef) {
  val name = event.getClass.getSimpleName
}
