package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.google.common.base.Strings
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.google.common.logging.nano.Vr
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDInvalidKeyException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDSerialized
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDValueTypeException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
import java.io.DataOutputStream
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.URI
import java.util.ArrayList
import java.util.Date
import java.util.HashMap
import java.util.UUID
import javax.annotation.Nonnull
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlSerializer

class LLSDMap : LLSDNode {
    @Nonnull
    private val items: ImmutableMap<String, LLSDNode>

    class LLSDMapEntry(val key: String, val value: LLSDNode)

    constructor(map: Map<String, LLSDNode>) {
        items = ImmutableMap.copyOf(map)
    }

    @Throws(LLSDXMLException::class, XmlPullParserException::class, IOException::class)
    constructor(parser: XmlPullParser) {
        val map = HashMap<String, LLSDNode>()
        while (parser.nextTag() != 3) {
            parser.require(2, null, "key")
            val key = parser.nextText()
            parser.nextTag()
            map[key] = LLSDNodeFactory.parseNode(parser)
        }
        items = ImmutableMap.copyOf(map)
    }

    constructor(vararg entries: LLSDMapEntry) {
        val map = HashMap<String, LLSDNode>(entries.size)
        for (entry in entries) {
            map[entry.key] = entry.value
        }
        items = ImmutableMap.copyOf(map)
    }

    @Throws(LLSDInvalidKeyException::class)
    override fun byKey(key: String): LLSDNode {
        return items[key]
            ?: throw LLSDInvalidKeyException("Map key not found, requested \"$key\"")
    }

    fun entrySet(): Set<Map.Entry<String, LLSDNode>> = items.entries

    override fun keyExists(key: String): Boolean = items.containsKey(key)

    @Throws(IOException::class)
    override fun toBinary(output: DataOutputStream) {
        output.writeByte(Vr.VREvent.VrCore.ErrorCode.CONTROLLER_GATT_CHARACTERISTIC_NOT_FOUND)
        val entries: ImmutableSet<Map.Entry<String, LLSDNode>> = items.entries
        output.writeInt(entries.size)
        for (entry in entries) {
            output.writeByte(107)
            val bytes = SLMessage.stringToVariableUTF(entry.key)
            output.writeInt(bytes.size)
            output.write(bytes)
            entry.value.toBinary(output)
        }
        output.writeByte(Vr.VREvent.VrCore.ErrorCode.CONTROLLER_BATTERY_READ_FAILED)
    }

    @Throws(LLSDException::class)
    override fun <T> toObject(cls: Class<out T>): T {
        try {
            val instance = cls.newInstance()
            for (field in cls.declaredFields) {
                val serialized = field.getAnnotation(LLSDSerialized::class.java)
                if (serialized != null) {
                    var name = serialized.name
                    if (Strings.isNullOrEmpty(name)) {
                        name = field.name
                    }
                    val type = field.type
                    if (keyExists(name)) {
                        val valueNode = byKey(name)
                        when {
                            type == Boolean::class.javaPrimitiveType -> field.setBoolean(instance, valueNode.asBoolean())
                            type == Int::class.javaPrimitiveType -> field.setInt(instance, valueNode.asInt())
                            type == Double::class.javaPrimitiveType -> field.setDouble(instance, valueNode.asDouble())
                            type == Long::class.javaPrimitiveType -> field.setLong(instance, valueNode.asLong())
                            type == String::class.java -> field.set(instance, valueNode.asString())
                            type == UUID::class.java -> field.set(instance, valueNode.asUUID())
                            type == URI::class.java -> field.set(instance, valueNode.asURI())
                            type == Date::class.java -> field.set(instance, valueNode.asDate())
                            type == ByteArray::class.java -> field.set(instance, valueNode.asBinary())
                            type.isAssignableFrom(List::class.java) -> {
                                val genericType = field.genericType
                                if (genericType !is ParameterizedType) {
                                    throw LLSDValueTypeException(type.name, valueNode)
                                }
                                val actualTypeArguments = genericType.actualTypeArguments
                                if (actualTypeArguments.size != 1) {
                                    throw LLSDValueTypeException(type.name, valueNode)
                                }
                                val elementType: Type = actualTypeArguments[0]
                                if (elementType !is Class<*>) {
                                    throw LLSDValueTypeException(type.name, valueNode)
                                }
                                val count = valueNode.getCount()
                                val list = ArrayList<Any?>(count)
                                for (index in 0 until count) {
                                    list.add(valueNode.byIndex(index).toObject(elementType))
                                }
                                field.set(instance, list)
                            }
                        }
                    }
                }
            }
            return instance
        } catch (e: IllegalAccessException) {
            throw LLSDException(e.message ?: "Illegal access")
        } catch (e: InstantiationException) {
            throw LLSDException(e.message ?: "Instantiation failed")
        }
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "map")
        for (entry in items.entries) {
            serializer.startTag("", "key")
            serializer.text(entry.key)
            serializer.endTag("", "key")
            entry.value.toXML(serializer)
        }
        serializer.endTag("", "map")
    }
}
