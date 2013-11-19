package pl.mpieciukiewicz.ships.util

import pl.marpiec.mpjsons.MPJson

/**
 *
 */
trait Serializer {

  def serialize[T](entity:T):String

  def deserialize[T](json: String, clazz: Class[T]):T
}
