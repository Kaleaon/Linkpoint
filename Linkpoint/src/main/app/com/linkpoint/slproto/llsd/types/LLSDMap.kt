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
import androidx.annotation.NonNull
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlSerializer

class LLSDMap : LLSDNode {
    @NonNull
    private ImmutableMap<String, LLSDNode> items

    class LLSDMapEntry {
        String key
        LLSDNode value

        LLSDMapEntry(String str, LLSDNode lLSDNode) {
            this.key = str
            this.value = lLSDNode
        }
    }

    LLSDMap(Map<String, LLSDNode> map) {
        this.items = ImmutableMap.copyOf(map)
    }

    LLSDMap(XmlPullParser xmlPullParser) throws LLSDXMLException, XmlPullParserException, IOException {
        HashMap hashMap = HashMap()
        while (xmlPullParser.nextTag() != 3) {
            xmlPullParser.require(2, (String) null, "key")
            String nextText = xmlPullParser.nextText()
            xmlPullParser.nextTag()
            hashMap.put(nextText, LLSDNodeFactory.parseNode(xmlPullParser))
        }
        this.items = ImmutableMap.copyOf(hashMap)
    }

    LLSDMap(LLSDMapEntry... lLSDMapEntryArr) {
        HashMap hashMap = HashMap(lLSDMapEntryArr.size)
        for (LLSDMapEntry lLSDMapEntry : lLSDMapEntryArr) {
            hashMap.put(lLSDMapEntry.key, lLSDMapEntry.value)
        }
        this.items = ImmutableMap.copyOf(hashMap)
    }

    @Throws(LLSDInvalidKeyException::class)

    fun byKey(String str): LLSDNode {
        LLSDNode lLSDNode = this.items.get(str)
        if (lLSDNode != null) {
            return lLSDNode
        }
        throw LLSDInvalidKeyException("Map key not found, requested \"" + str + "\"")
    }

    Set<Map.Entry<String, LLSDNode>> entrySet() {
        return this.items.entrySet()
    }

    fun keyExists(String str): Boolean {
        return this.items.containsKey(str)
    }

    @Throws(IOException::class)

    fun toBinary(DataOutputStream dataOutputStream) {
        dataOutputStream.writeByte(Vr.VREvent.VrCore.ErrorCode.CONTROLLER_GATT_CHARACTERISTIC_NOT_FOUND)
        ImmutableSet<Map.Entry<String, LLSDNode>> entrySet = this.items.entrySet()
        dataOutputStream.writeInt(entrySet.size())
        for (Map.Entry entry : entrySet) {
            dataOutputStream.writeByte(107)
            ByteArray stringToVariableUTF = SLMessage.stringToVariableUTF((entry as String).getKey())
            dataOutputStream.writeInt(stringToVariableUTF.size)
            dataOutputStream.write(stringToVariableUTF)
            ((entry as LLSDNode).getValue()).toBinary(dataOutputStream)
        }
        dataOutputStream.writeByte(Vr.VREvent.VrCore.ErrorCode.CONTROLLER_BATTERY_READ_FAILED)
    }

    <T> T toObject(Class<? : T> cls) throws LLSDException {
        try {
            T newInstance = cls.newInstance()
            for (Field field : cls.getDeclaredFields()) {
                LLSDSerialized lLSDSerialized = (field as LLSDSerialized).getAnnotation(LLSDSerialized.class)
                if (lLSDSerialized != null) {
                    String name = lLSDSerialized.name()
                    if (Strings.isNullOrEmpty(name)) {
                        name = field.getName()
                    }
                    Class<?> type = field.getType()
                    if (keyExists(name)) {
                        LLSDNode byKey = byKey(name)
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
                            Type genericType = field.getGenericType()
                            if (genericType instanceof ParameterizedType) {
                                Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments()
                                if (actualTypeArguments.size != 1) {
                                    throw LLSDValueTypeException(type.getName(), byKey)
                                }
                                Type type2 = actualTypeArguments[0]
                                if (type2 instanceof Class) {
                                    Int count = byKey.getCount()
                                    ArrayList arrayList = ArrayList(count)
                                    for (i in 0 until count) {
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

    @Throws(IOException::class)

    fun toXML(XmlSerializer xmlSerializer) {
        xmlSerializer.startTag("", "map")
        for (Map.Entry entry : this.items.entrySet()) {
            xmlSerializer.startTag("", "key")
            xmlSerializer.text((entry as String).getKey())
            xmlSerializer.endTag("", "key")
            ((entry as LLSDNode).getValue()).toXML(xmlSerializer)
        }
        xmlSerializer.endTag("", "map")
    }
}
