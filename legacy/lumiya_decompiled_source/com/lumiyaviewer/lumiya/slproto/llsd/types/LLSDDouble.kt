package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDDouble : LLSDNode {
    private var value: Double

    constructor(text: String) {
        value = text.toDouble()
    }

    constructor(value: Double) {
        this.value = value
    }

    override fun asDouble(): Double = value

    @Throws(IOException::class)
    override fun toBinary(output: DataOutputStream) {
        output.writeByte(114)
        output.writeDouble(value)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "real")
        serializer.text(value.toString())
        serializer.endTag("", "real")
    }
}
