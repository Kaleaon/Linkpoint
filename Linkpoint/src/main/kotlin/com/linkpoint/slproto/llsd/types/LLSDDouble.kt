package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDDouble(private val value: Double) : LLSDNode() {

    constructor(text: String) : this(text.toDouble())

    override fun asDouble(): Double = value

    override fun asInt(): Int = value.toInt()

    override fun asString(): String = value.toString()

    @Throws(IOException::class)
    override fun toBinary(stream: DataOutputStream) {
        stream.writeByte('r'.code)
        stream.writeDouble(value)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "real")
        serializer.text(value.toString())
        serializer.endTag("", "real")
    }
}
