package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDDouble : LLSDNode {
    private double value

    LLSDDouble(double d) {
        this.value = d
    }

    LLSDDouble(String str) {
        this.value = Double.parseDouble(str)
    }

    double asDouble() {
        return this.value
    }

    Unit toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(114)
        dataOutputStream.writeDouble(this.value)
    }

    Unit toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "real")
        xmlSerializer.text(Double.toString(this.value))
        xmlSerializer.endTag("", "real")
    }
}
