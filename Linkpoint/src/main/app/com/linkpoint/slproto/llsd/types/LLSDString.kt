package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import java.util.UUID
import org.xmlpull.v1.XmlSerializer

class LLSDString : LLSDNode {
    private String value

    LLSDString(String str) {
        this.value = str
    }

    fun asBoolean(): Boolean {
        return "true".equalsIgnoreCase(this.value)
    }

    fun asString(): String {
        return this.value
    }

    fun asUUID(): UUID {
        return UUID.fromString(this.value)
    }

    @Throws(IOException::class)

    fun toBinary(DataOutputStream dataOutputStream) {
        dataOutputStream.writeByte(115)
        if (this.value.isEmpty()) {
            dataOutputStream.writeInt(0)
            return
        }
        ByteArray stringToVariableUTF = SLMessage.stringToVariableUTF(this.value)
        dataOutputStream.writeInt(stringToVariableUTF.size)
        dataOutputStream.write(stringToVariableUTF)
    }

    @Throws(IOException::class)

    fun toXML(XmlSerializer xmlSerializer) {
        xmlSerializer.startTag("", "string")
        xmlSerializer.text(this.value)
        xmlSerializer.endTag("", "string")
    }
}
