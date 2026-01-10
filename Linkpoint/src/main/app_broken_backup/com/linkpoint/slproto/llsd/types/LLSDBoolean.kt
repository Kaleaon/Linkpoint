package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDBoolean : LLSDNode {
    private Boolean value

    LLSDBoolean(String str) {
        var z: Boolean = true
        if (str.equalsIgnoreCase("true")) {
            this.value = true
        } else if (str.equalsIgnoreCase("false")) {
            this.value = false
        } else {
            this.value = Integer.parseInt(str) == 0 ? false : z
        }
    }

    LLSDBoolean(Boolean z) {
        this.value = z
    }

    fun asBoolean(): Boolean {
        return this.value
    }

    @Throws(IOException::class)

    fun toBinary(DataOutputStream dataOutputStream) {
        dataOutputStream.writeByte(this.value ? 49 : 48)
    }

    @Throws(IOException::class)

    fun toXML(XmlSerializer xmlSerializer) {
        xmlSerializer.startTag("", "Boolean")
        xmlSerializer.text(this.value ? "1" : "0")
        xmlSerializer.endTag("", "Boolean")
    }
}
