package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import java.util.UUID
import org.xmlpull.v1.XmlSerializer

class LLSDString : LLSDNode {
    private var value: String

    constructor(text: String) {
        value = text
    }

    override fun asBoolean(): Boolean = value.equals("true", ignoreCase = true)

    override fun asString(): String = value

    override fun asUUID(): UUID = UUID.fromString(value)

    @Throws(IOException::class)
    override fun toBinary(output: DataOutputStream) {
        output.writeByte(115)
        if (value.isEmpty()) {
            output.writeInt(0)
            return
        }
        val bytes = SLMessage.stringToVariableUTF(value)
        output.writeInt(bytes.size)
        output.write(bytes)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "string")
        serializer.text(value)
        serializer.endTag("", "string")
    }
}
