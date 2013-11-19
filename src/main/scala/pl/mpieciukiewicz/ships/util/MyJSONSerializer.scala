package pl.mpieciukiewicz.ships.util

import pl.marpiec.mpjsons.MPJson

/**
 * @author Marcin Pieciukiewicz
 */
object MyJSONSerializer extends Serializer {

  override def serialize[T](entity:T):String = MPJson.serialize(entity.asInstanceOf[AnyRef])

  override def deserialize[T](json: String, clazz: Class[T]):T = MPJson.deserialize(json, clazz).asInstanceOf[T]

}
