import pl.marpiec.mpjsons.MPJson

/**
 * @author Marcin Pieciukiewicz
 */
object JSON {

  def toJson(entity:AnyRef):String = MPJson.serialize(entity)

  def fromJson[T](json: String, clazz: Class[T]):T = MPJson.deserialize(json, clazz).asInstanceOf[T]

}
