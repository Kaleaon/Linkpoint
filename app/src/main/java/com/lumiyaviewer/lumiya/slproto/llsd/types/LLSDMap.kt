package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.google.common.base.Strings
import com.google.common.collect.ImmutableMap
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
import java.lang.reflect.Field
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

    companion object {
        // LLSD binary format constants
        private const val LLSD_MAP_BEGIN = 123 // '{'
        private const val LLSD_MAP_END = 125   // '}'
        private const val LLSD_KEY = 107       // 'k'
    }

    class LLSDMapEntry(val key: String, val value: LLSDNode)

    constructor(map: Map<String, LLSDNode>) {
        this.items = ImmutableMap.copyOf(map)
    }

    @Throws(LLSDXMLException::class, XmlPullParserException::class, IOException::class)
    constructor(xmlPullParser: XmlPullParser) {
        val hashMap = HashMap<String, LLSDNode>()
        while (xmlPullParser.nextTag() != 3) {
            xmlPullParser.require(2, null, "key")
            val nextText = xmlPullParser.nextText()
            xmlPullParser.nextTag()
            hashMap[nextText] = LLSDNodeFactory.parseNode(xmlPullParser)
        }
        this.items = ImmutableMap.copyOf(hashMap)
    }

    constructor(vararg lLSDMapEntryArr: LLSDMapEntry) {
        val hashMap = HashMap<String, LLSDNode>(lLSDMapEntryArr.size)
        for (lLSDMapEntry in lLSDMapEntryArr) {
            hashMap[lLSDMapEntry.key] = lLSDMapEntry.value
        }
        this.items = ImmutableMap.copyOf(hashMap)
    }

    @Throws(LLSDInvalidKeyException::class)
    override fun byKey(str: String): LLSDNode {
        val lLSDNode = this.items[str]
        if (lLSDNode != null) {
            return lLSDNode
        }
        throw LLSDInvalidKeyException("Map key not found, requested \"$str\"")
    }

    fun entrySet(): Set<Map.Entry<String, LLSDNode>> {
        return this.items.entries
    }

    override fun keyExists(str: String): Boolean {
        return this.items.containsKey(str)
    }

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(LLSD_MAP_BEGIN)
        val entrySet = this.items.entries
        dataOutputStream.writeInt(entrySet.size)
        for (entry in entrySet) {
            dataOutputStream.writeByte(LLSD_KEY)
            val stringToVariableUTF = SLMessage.stringToVariableUTF(entry.key)
            dataOutputStream.writeInt(stringToVariableUTF.size)
            dataOutputStream.write(stringToVariableUTF)
            entry.value.toBinary(dataOutputStream)
        }
        dataOutputStream.writeByte(LLSD_MAP_END)
    }

    @Throws(LLSDException::class)
    override fun <T> toObject(cls: Class<out T>): T {
        try {
            val newInstance = cls.newInstance()
            for (field in cls.declaredFields) {
                val lLSDSerialized = field.getAnnotation(LLSDSerialized::class.java)
                if (lLSDSerialized != null) {
                    var name = lLSDSerialized.name
                    if (Strings.isNullOrEmpty(name)) {
                        name = field.name
                    }
                    val type = field.type
                    if (keyExists(name)) {
                        val byKey = byKey(name)
                        when {
                            type == Boolean::class.javaPrimitiveType -> field.setBoolean(newInstance, byKey.asBoolean())
                            type == Int::class.javaPrimitiveType -> field.setInt(newInstance, byKey.asInt())
                            type == Double::class.javaPrimitiveType -> field.setDouble(newInstance, byKey.asDouble())
                            type == Long::class.javaPrimitiveType -> field.setLong(newInstance, byKey.asLong())
                            type == String::class.java -> field.set(newInstance, byKey.asString())
                            type == UUID::class.java -> field.set(newInstance, byKey.asUUID())
                            type == URI::class.java -> field.set(newInstance, byKey.asURI())
                            type == Date::class.java -> field.set(newInstance, byKey.asDate())
                            type == ByteArray::class.java -> field.set(newInstance, byKey.asBinary())
                            List::class.java.isAssignableFrom(type) -> {
                                val genericType = field.genericType
                                if (genericType is ParameterizedType) {
                                    val actualTypeArguments = genericType.actualTypeArguments
                                    if (actualTypeArguments.size != 1) {
                                        throw LLSDValueTypeException(type.name, byKey)
                                    }
                                    val type2 = actualTypeArguments[0]
                                    if (type2 is Class<*>) {
                                        val count = byKey.getCount()
                                        val arrayList = ArrayList<Any>(count)
                                        for (i in 0 until count) {
                                            arrayList.add(byKey.byIndex(i).toObject(type2))
                                        }
                                        field.set(newInstance, arrayList)
                                    } else {
                                        throw LLSDValueTypeException(type.name, byKey)
                                    }
                                } else {
                                    throw LLSDValueTypeException(type.name, byKey)
                                }
                            }
                        }
                    }
                }
            }
            return newInstance
        } catch (e: IllegalAccessException) {
            throw LLSDException(e.message ?: "IllegalAccessException")
        } catch (e: InstantiationException) {
            throw LLSDException(e.message ?: "InstantiationException")
        }
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "map")
        for (entry in this.items.entries) {
            xmlSerializer.startTag("", "key")
            xmlSerializer.text(entry.key)
            xmlSerializer.endTag("", "key")
            entry.value.toXML(xmlSerializer)
        }
        xmlSerializer.endTag("", "map")
    }
}

