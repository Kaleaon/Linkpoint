package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDNode
import org.xmlpull.v1.XmlSerializer
import java.io.DataOutputStream
import java.io.IOException

class LLSDDouble : LLSDNode {
    private val value: Double

    constructor(value: Double) {
        this.value = value
    }

    constructor(str: String) {
        this.value = str.toDouble()
    }

    override fun asDouble(): Double {
        return value
    }

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(114)
        dataOutputStream.writeDouble(value)
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "real")
        xmlSerializer.text(value.toString())
        xmlSerializer.endTag("", "real")
    }
}