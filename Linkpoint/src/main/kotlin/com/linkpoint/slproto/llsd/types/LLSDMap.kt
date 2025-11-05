package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDInvalidKeyException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.LLSDNodeFactory
import com.linkpoint.slproto.llsd.LLSDSerialized
import com.linkpoint.slproto.llsd.LLSDValueTypeException
import com.linkpoint.slproto.llsd.LLSDXMLException
import java.io.DataOutputStream
import java.io.IOException
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.URI
import java.util.ArrayList
import java.util.Date
import java.util.LinkedHashMap
import java.util.List
import java.util.UUID
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlSerializer

class LLSDMap : LLSDNode {

    private val items: LinkedHashMap<String, LLSDNode>

    constructor() : super() {
        items = LinkedHashMap()
    }

    constructor(map: Map<String, LLSDNode>) : super() {
        items = LinkedHashMap(map)
    }

    @Throws(XmlPullParserException::class, IOException::class, LLSDXMLException::class)
    constructor(parser: XmlPullParser) : this() {
        while (parser.nextTag() != XmlPullParser.END_TAG) {
            parser.require(XmlPullParser.START_TAG, null, "key")
            val key = parser.nextText()
            parser.nextTag()
            items[key] = LLSDNodeFactory.parseNode(parser)
        }
    }

    constructor(vararg entries: LLSDMapEntry) : this() {
        for (entry in entries) {
            items[entry.key] = entry.value
        }
    }

    data class LLSDMapEntry(val key: String, val value: LLSDNode)

    override fun byKey(key: String): LLSDNode {
        return items[key] ?: throw LLSDInvalidKeyException("Map key not found: $key")
    }

    override fun keyExists(key: String): Boolean = items.containsKey(key)

    override fun getCount(): Int = items.size

    fun entrySet(): Set<Map.Entry<String, LLSDNode>> = items.entries

    @Throws(IOException::class)
    override fun toBinary(stream: DataOutputStream) {
        stream.writeByte('{'.code)
        stream.writeInt(items.size)
        for ((key, value) in items) {
            stream.writeByte('k'.code)
            val keyBytes = SLMessage.stringToVariableUTF(key)
            stream.writeInt(keyBytes.size)
            stream.write(keyBytes)
            value.toBinary(stream)
        }
        stream.writeByte('}'.code)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "map")
        for ((key, value) in items) {
            serializer.startTag("", "key")
            serializer.text(key)
            serializer.endTag("", "key")
            value.toXML(serializer)
        }
        serializer.endTag("", "map")
    }

    @Throws(LLSDException::class)
    override fun <T> toObject(type: Class<out T>): T {
        val instance = type.getDeclaredConstructor().newInstance()
        for (field in type.declaredFields) {
            val annotation = field.getAnnotation(LLSDSerialized::class.java) ?: continue
            val key = annotation.name.ifEmpty { field.name }
            if (!keyExists(key)) {
                continue
            }
            val node = byKey(key)
            field.isAccessible = true
            try {
                when (field.type) {
                    java.lang.Boolean.TYPE, Boolean::class.java -> field.setBoolean(instance, node.asBoolean())
                    java.lang.Integer.TYPE, Int::class.java -> field.setInt(instance, node.asInt())
                    java.lang.Double.TYPE, Double::class.java -> field.setDouble(instance, node.asDouble())
                    java.lang.Long.TYPE, Long::class.java -> field.setLong(instance, node.asLong())
                    String::class.java -> field.set(instance, node.asString())
                    UUID::class.java -> field.set(instance, node.asUUID())
                    URI::class.java -> field.set(instance, node.asURI())
                    Date::class.java -> field.set(instance, node.asDate())
                    ByteArray::class.java -> field.set(instance, node.asBinary())
                    else -> {
                        if (List::class.java.isAssignableFrom(field.type)) {
                            assignListField(instance, field, node)
                        }
                    }
                }
            } catch (ex: Exception) {
                throw LLSDException(ex.message ?: "LLSD reflection error")
            }
        }
        return instance
    }

    @Throws(LLSDException::class)
    private fun assignListField(instance: Any, field: Field, node: LLSDNode) {
        val genericType = field.genericType
        if (genericType !is ParameterizedType) {
            throw LLSDValueTypeException(field.type.name, node)
        }
        val elementType: Type = genericType.actualTypeArguments.firstOrNull()
            ?: throw LLSDValueTypeException(field.type.name, node)
        if (elementType !is Class<*>) {
            throw LLSDValueTypeException(field.type.name, node)
        }
        val count = node.countOrThrow()
        val list = ArrayList<Any>(count)
        for (i in 0 until count) {
            val entryNode = node.byIndex(i)
            list.add(entryNode.toObject(elementType as Class<out Any>))
        }
        field.set(instance, list)
    }

    private fun LLSDNode.countOrThrow(): Int {
        return try {
            getCount()
        } catch (ex: LLSDValueTypeException) {
            throw ex
        }
    }
}
