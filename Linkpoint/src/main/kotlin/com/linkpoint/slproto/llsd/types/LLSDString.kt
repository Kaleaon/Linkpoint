package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import java.util.UUID
import org.xmlpull.v1.XmlSerializer

class LLSDString(private val value: String) : LLSDNode() {

    override fun asBoolean(): Boolean = value.equals("true", ignoreCase = true)

    override fun asString(): String = value

    override fun asUUID(): UUID = UUID.fromString(value)

    @Throws(IOException::class)
    override fun toBinary(stream: DataOutputStream) {
        stream.writeByte('s'.code)
        if (value.isEmpty()) {
            stream.writeInt(0)
            return
        }
        val bytes = SLMessage.stringToVariableUTF(value)
        stream.writeInt(bytes.size)
        stream.write(bytes)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "string")
        serializer.text(value)
        serializer.endTag("", "string")
    }
}
