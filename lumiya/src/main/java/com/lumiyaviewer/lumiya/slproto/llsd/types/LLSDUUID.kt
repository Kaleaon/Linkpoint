package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import java.util.UUID
import org.xmlpull.v1.XmlSerializer

class LLSDUUID : LLSDNode {
    private val value: UUID

    constructor() {
        this.value = UUID(0, 0)
    }

    constructor(str: String) {
        this.value = UUID.fromString(str)
    }

    constructor(uuid: UUID) {
        this.value = uuid
    }

    override fun asString(): String {
        return this.value.toString()
    }

    override fun asUUID(): UUID {
        return this.value
    }

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(117)
        dataOutputStream.writeLong(this.value.mostSignificantBits)
        dataOutputStream.writeLong(this.value.leastSignificantBits)
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "uuid")
        xmlSerializer.text(this.value.toString())
        xmlSerializer.endTag("", "uuid")
    }
}
