package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDInt : LLSDNode {
    private val value: Int

    constructor(i: Int) {
        this.value = i
    }

    constructor(str: String) {
        this.value = try {
            str.toInt()
        } catch (e: Exception) {
            0
        }
    }

    override fun asBoolean(): Boolean {
        return this.value != 0
    }

    override fun asInt(): Int {
        return this.value
    }

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(105)
        dataOutputStream.writeInt(this.value)
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "integer")
        xmlSerializer.text(this.value.toString())
        xmlSerializer.endTag("", "integer")
    }
}
