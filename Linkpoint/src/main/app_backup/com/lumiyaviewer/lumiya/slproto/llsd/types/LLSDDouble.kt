package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDDouble(private val value: Double) : LLSDNode {

    constructor(str: String) : this(str.toDouble())

    fun asDouble(): Double {
        return this.value
    }

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(114)
        dataOutputStream.writeDouble(this.value)
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "real")
        xmlSerializer.text(this.value.toString())
        xmlSerializer.endTag("", "real")
    }
}
