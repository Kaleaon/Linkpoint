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

    Boolean asBoolean() {
        return "true".equalsIgnoreCase(this.value)
    }

    String asString() {
        return this.value
    }

    UUID asUUID() {
        return UUID.fromString(this.value)
    }

    Unit toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(115)
        if (this.value.isEmpty()) {
            dataOutputStream.writeInt(0)
            return
        }
        byte[] stringToVariableUTF = SLMessage.stringToVariableUTF(this.value)
        dataOutputStream.writeInt(stringToVariableUTF.length)
        dataOutputStream.write(stringToVariableUTF)
    }

    Unit toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "string")
        xmlSerializer.text(this.value)
        xmlSerializer.endTag("", "string")
    }
}
