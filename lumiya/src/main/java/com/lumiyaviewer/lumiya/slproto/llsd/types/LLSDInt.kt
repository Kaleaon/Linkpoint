package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDInt : LLSDNode {
    private Int value

    LLSDInt(Int i) {
        this.value = i
    }

    LLSDInt(String str) {
        try {
            this.value = Integer.parseInt(str)
        } catch (Exception e) {
            this.value = 0
        }
    }

    Boolean asBoolean() {
        return this.value != 0
    }

    Int asInt() {
        return this.value
    }

    Unit toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(105)
        dataOutputStream.writeInt(this.value)
    }

    Unit toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "integer")
        xmlSerializer.text(Integer.toString(this.value))
        xmlSerializer.endTag("", "integer")
    }
}
