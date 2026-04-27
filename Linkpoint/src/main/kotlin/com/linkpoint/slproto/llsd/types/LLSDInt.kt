package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDInt(private val value: Int) : LLSDNode() {

    constructor(text: String) : this(text.toInt())

    override fun asInt(): Int = value

    override fun asBoolean(): Boolean = value != 0

    override fun asString(): String = value.toString()

    @Throws(IOException::class)
    override fun toBinary(stream: DataOutputStream) {
        stream.writeByte('i'.code)
        stream.writeInt(value)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "integer")
        serializer.text(value.toString())
        serializer.endTag("", "integer")
    }
}
