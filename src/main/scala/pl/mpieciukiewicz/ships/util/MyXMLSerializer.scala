package pl.mpieciukiewicz.ships.util


import net.sf.json.xml.XMLSerializer
import net.sf.json.{JSON, JSONSerializer}

/**
 *
 */
object MyXMLSerializer extends Serializer{



  override def serialize[T](entity:T):String = {

    val serializer: XMLSerializer  = new XMLSerializer()

    val json  = JSONSerializer.toJSON(MyJSONSerializer.serialize(entity))

    serializer.setRootName(entity.getClass.getSimpleName)
    serializer.setTypeHintsEnabled(false)
    serializer.write( json )

  }

  override def deserialize[T](xml: String, clazz: Class[T]):T = {

    val serializer: XMLSerializer  = new XMLSerializer()

    val json: JSON = serializer.read(xml)

    MyJSONSerializer.deserialize(json.toString(0), clazz)
  }

}
