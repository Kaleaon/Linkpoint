package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDBoolean(private val value: Boolean) : LLSDNode() {

    constructor(text: String) : this(text.equals("true", ignoreCase = true) || text == "1")

    override fun asBoolean(): Boolean = value

    override fun asString(): String = value.toString()

    override fun asInt(): Int = if (value) 1 else 0

    @Throws(IOException::class)
    override fun toBinary(stream: DataOutputStream) {
        stream.writeByte(if (value) '1'.code else '0'.code)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "boolean")
        serializer.text(if (value) "true" else "false")
        serializer.endTag("", "boolean")
    }
}
