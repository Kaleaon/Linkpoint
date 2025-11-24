package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import java.util.HashMap
import org.xmlpull.v1.XmlSerializer

class LLSDMap() : LLSDNode {
    val items: MutableMap<String, LLSDNode> = HashMap()

    constructor(map: Map<String, LLSDNode>) : this() {
        this.items.putAll(map)
    }

    operator fun set(key: String, value: LLSDNode) {
        this.items[key] = value
    }

    operator fun get(key: String): LLSDNode? {
        return this.items[key]
    }

    override fun byKey(key: String): LLSDNode? {
        return this.items[key]
    }

    override fun keyExists(key: String): Boolean {
        return this.items.containsKey(key)
    }

    val value: Map<String, LLSDNode>
        get() = items

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte('{'.toInt())
        dataOutputStream.writeInt(items.size)
        for ((key, value) in items) {
            dataOutputStream.writeByte('k'.toInt())
            val keyBytes = SLMessage.stringToVariableUTF(key)
            dataOutputStream.writeInt(keyBytes.size)
            dataOutputStream.write(keyBytes)
            value.toBinary(dataOutputStream)
        }
        dataOutputStream.writeByte('}'.toInt())
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "map")
        for ((key, value) in items) {
            xmlSerializer.startTag("", "key")
            xmlSerializer.text(key)
            xmlSerializer.endTag("", "key")
            value.toXML(xmlSerializer)
        }
        xmlSerializer.endTag("", "map")
    }
}
