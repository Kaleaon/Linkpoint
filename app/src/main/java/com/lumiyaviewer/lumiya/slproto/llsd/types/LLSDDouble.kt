package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import org.xmlpull.v1.XmlSerializer

class LLSDDouble : LLSDNode {
    private val value: Double

    constructor(d: Double) {
        this.value = d
    }

    constructor(str: String) {
        this.value = Double.parseDouble(str)
    }

    override fun asDouble(): Double {
        return this.value
    }

    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(114)
        dataOutputStream.writeDouble(this.value)
    }

    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "real")
        xmlSerializer.text(Double.toString(this.value))
        xmlSerializer.endTag("", "real")
    }
}
