package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import java.util.UUID
import org.xmlpull.v1.XmlSerializer

class LLSDUUID(private val value: UUID) : LLSDNode() {

    constructor(text: String) : this(UUID.fromString(text))

    override fun asUUID(): UUID = value

    override fun asString(): String = value.toString()

    @Throws(IOException::class)
    override fun toBinary(stream: DataOutputStream) {
        stream.writeByte('u'.code)
        stream.writeLong(value.mostSignificantBits)
        stream.writeLong(value.leastSignificantBits)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "uuid")
        serializer.text(value.toString())
        serializer.endTag("", "uuid")
    }
}
