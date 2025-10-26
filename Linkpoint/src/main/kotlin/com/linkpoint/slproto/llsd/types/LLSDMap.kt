package com.linkpoint.slproto.llsd.types

import com.google.common.base.Strings
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.google.common.logging.nano.Vr
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
import java.util.HashMap
import java.util.List
import java.util.Map
import java.util.Set
import java.util.UUID
import javax.annotation.Nonnull
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlSerializer

class LLSDMap : LLSDNode() {
    private val ImmutableMap<String, LLSDNode> items

    @JvmStatic
    class LLSDMapEntry {
        final String key
        final LLSDNode value

        public LLSDMapEntry(String str, LLSDNode lLSDNode) {
            this.key = str
            this.value = lLSDNode
        }
    }

    public LLSDMap(Map<String, LLSDNode> map) {
        this.items = ImmutableMap.copyOf(map)
    }

    public LLSDMap(XmlPullParser xmlPullParser) throws LLSDXMLException, XmlPullParserException, IOException {
        val hashMap: HashMap = HashMap()
        while (xmlPullParser.nextTag() != 3) {
            xmlPullParser.require(2, (String) null, "key")
            val nextText: String = xmlPullParser.nextText()
            xmlPullParser.nextTag()
            hashMap.put(nextText, LLSDNodeFactory.parseNode(xmlPullParser))
        }
        this.items = ImmutableMap.copyOf(hashMap)
    }

    public LLSDMap(vararg lLSDMapEntryArr: LLSDMapEntry) {
        val hashMap: HashMap = HashMap(lLSDMapEntryArr.length)
        for (LLSDMapEntry lLSDMapEntry : lLSDMapEntryArr) {
            hashMap.put(lLSDMapEntry.key, lLSDMapEntry.value)
        }
        this.items = ImmutableMap.copyOf(hashMap)
    }

     public fun byKey(str: String) throws LLSDInvalidKeyException {
        val lLSDNode: LLSDNode = this.items.get(str)
        if (lLSDNode != null) {
            return lLSDNode
        }
        throw LLSDInvalidKeyException("Map key not found, requested \"" + str + "\"")
    }

    public Set<Map.Entry<String, LLSDNode>> entrySet() {
        return this.items.entrySet()
    }

     public fun keyExists(str: String): Boolean {
        return this.items.containsKey(str)
    }

    fun toBinary(dataOutputStream: DataOutputStream) throws IOException {
        dataOutputStream.writeByte(Vr.VREvent.VrCore.ErrorCode.CONTROLLER_GATT_CHARACTERISTIC_NOT_FOUND)
        ImmutableSet<Map.Entry<String, LLSDNode>> entrySet = this.items.entrySet()
        dataOutputStream.writeInt(entrySet.size())
        for (Map.Entry entry : entrySet) {
            dataOutputStream.writeByte(107)
            val stringToVariableUTF: ByteArray = SLMessage.stringToVariableUTF((String) entry.getKey())
            dataOutputStream.writeInt(stringToVariableUTF.length)
            dataOutputStream.write(stringToVariableUTF)
            ((LLSDNode) entry.getValue()).toBinary(dataOutputStream)
        }
        dataOutputStream.writeByte(Vr.VREvent.VrCore.ErrorCode.CONTROLLER_BATTERY_READ_FAILED)
    }

    public <T> T toObject(Class<? : T> cls) throws LLSDException {
        try {
            T newInstance = cls.newInstance()
            for (Field field : cls.getDeclaredFields()) {
                val lLSDSerialized: LLSDSerialized = (LLSDSerialized) field.getAnnotation(LLSDSerialized.class)
                if (lLSDSerialized != null) {
                    val name: String = lLSDSerialized.name()
                    if (Strings.isNullOrEmpty(name)) {
                        name = field.getName()
                    }
                    val type: Class<?> = field.getType()
                    if (keyExists(name)) {
                        val byKey: LLSDNode = byKey(name)
                        if (type.equals(Boolean.TYPE)) {
                            field.setBoolean(newInstance, byKey.asBoolean())
                        } else if (type.equals(Integer.TYPE)) {
                            field.setInt(newInstance, byKey.asInt())
                        } else if (type.equals(Double.TYPE)) {
                            field.setDouble(newInstance, byKey.asDouble())
                        } else if (type.equals(Long.TYPE)) {
                            field.setLong(newInstance, byKey.asLong())
                        } else if (type.equals(String.class)) {
                            field.set(newInstance, byKey.asString())
                        } else if (type.equals(UUID.class)) {
                            field.set(newInstance, byKey.asUUID())
                        } else if (type.equals(URI.class)) {
                            field.set(newInstance, byKey.asURI())
                        } else if (type.equals(Date.class)) {
                            field.set(newInstance, byKey.asDate())
                        } else if (type.equals(ByteArray.class)) {
                            field.set(newInstance, byKey.asBinary())
                        } else if (type.isAssignableFrom(List.class)) {
                            val genericType: Type = field.getGenericType()
                            if (genericType instanceof ParameterizedType) {
                                val actualTypeArguments: Array<Type> = ((ParameterizedType) genericType).getActualTypeArguments()
                                if (actualTypeArguments.length != 1) {
                                    throw LLSDValueTypeException(type.getName(), byKey)
                                }
                                val type2: Type = actualTypeArguments[0]
                                if (type2 instanceof Class) {
                                    val count: Int = byKey.getCount()
                                    val arrayList: ArrayList = ArrayList(count)
                                    for (Int i = 0; i < count; i++) {
                                        arrayList.add(byKey.byIndex(i).toObject((Class) type2))
                                    }
                                    field.set(newInstance, arrayList)
                                } else {
                                    throw LLSDValueTypeException(type.getName(), byKey)
                                }
                            } else {
                                throw LLSDValueTypeException(type.getName(), byKey)
                            }
                        } else {
                            continue
                        }
                    } else {
                        continue
                    }
                }
            }
            return newInstance
        } catch (IllegalAccessException e) {
            throw LLSDException(e.getMessage())
        } catch (InstantiationException e2) {
            throw LLSDException(e2.getMessage())
        }
    }

    fun toXML(xmlSerializer: XmlSerializer) throws IOException {
        xmlSerializer.startTag("", "map")
        for (Map.Entry entry : this.items.entrySet()) {
            xmlSerializer.startTag("", "key")
            xmlSerializer.text((String) entry.getKey())
            xmlSerializer.endTag("", "key")
            ((LLSDNode) entry.getValue()).toXML(xmlSerializer)
        }
        xmlSerializer.endTag("", "map")
    }
}
