package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.llsd.LLSDNode
import org.xmlpull.v1.XmlSerializer
import java.io.DataOutputStream
import java.io.IOException
import java.util.UUID

class LLSDString(private val value: String) : LLSDNode() {

    override fun asBoolean(): Boolean {
        return "true".equals(value, ignoreCase = true)
    }

    override fun asString(): String {
        return value
    }

    override fun asUUID(): UUID {
        return UUID.fromString(value)
    }

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(115)
        if (value.isEmpty()) {
            dataOutputStream.writeInt(0)
            return
        }
        val bytes = SLMessage.stringToVariableUTF(value)
        dataOutputStream.writeInt(bytes.size)
        dataOutputStream.write(bytes)
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "string")
        xmlSerializer.text(value)
        xmlSerializer.endTag("", "string")
    }
}