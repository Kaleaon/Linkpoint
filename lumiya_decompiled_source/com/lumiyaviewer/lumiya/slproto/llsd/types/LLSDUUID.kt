package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import java.util.UUID
import org.xmlpull.v1.XmlSerializer

class LLSDUUID : LLSDNode {
    private var value: UUID

    constructor(text: String) {
        value = UUID.fromString(text)
    }

    constructor(uuid: UUID) {
        value = uuid
    }

    constructor() {
        value = UUID(0, 0)
    }

    override fun asUUID(): UUID = value

    override fun asString(): String = value.toString()

    @Throws(IOException::class)
    override fun toBinary(output: DataOutputStream) {
        output.writeByte(117)
        output.writeLong(value.mostSignificantBits)
        output.writeLong(value.leastSignificantBits)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "uuid")
        serializer.text(value.toString())
        serializer.endTag("", "uuid")
    }
}
