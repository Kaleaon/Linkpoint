package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDNode
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

    fun asBoolean(): Boolean {
        return this.value != 0
    }

    fun asInt(): Int {
        return this.value
    }

    @Throws(IOException::class)

    fun toBinary(DataOutputStream dataOutputStream) {
        dataOutputStream.writeByte(105)
        dataOutputStream.writeInt(this.value)
    }

    @Throws(IOException::class)

    fun toXML(XmlSerializer xmlSerializer) {
        xmlSerializer.startTag("", "integer")
        xmlSerializer.text(Integer.toString(this.value))
        xmlSerializer.endTag("", "integer")
    }
}
