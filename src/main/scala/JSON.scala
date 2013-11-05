import pl.marpiec.mpjsons.MPJson

/**
 * @author Marcin Pieciukiewicz
 */
object JSON {

  def toJson(entity:Any):String = MPJson.serialize(entity.asInstanceOf[AnyRef])

  def fromJson[T](json: String, clazz: Class[T]):T = MPJson.deserialize(json, clazz).asInstanceOf[T]

}
