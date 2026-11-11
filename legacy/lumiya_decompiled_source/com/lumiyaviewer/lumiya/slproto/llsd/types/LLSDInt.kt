package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDInt : LLSDNode {
    private var value: Int

    constructor(text: String) {
        value = text.toInt()
    }

    constructor(value: Int) {
        this.value = value
    }

    override fun asInt(): Int = value

    override fun asLong(): Long = value.toLong()

    @Throws(IOException::class)
    override fun toBinary(output: DataOutputStream) {
        output.writeByte(105)
        output.writeInt(value)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "integer")
        serializer.text(value.toString())
        serializer.endTag("", "integer")
    }
}
