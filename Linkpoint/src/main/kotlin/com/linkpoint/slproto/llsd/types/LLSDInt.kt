package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDInt : LLSDNode() {
    private Int value

    public LLSDInt(Int i) {
        this.value = i
    }

    public LLSDInt(String str) {
        try {
            this.value = Integer.parseInt(str)
        } catch (Exception e) {
            this.value = 0
        }
    }

     public fun asBoolean(): Boolean {
        return this.value != 0
    }

     public fun asInt(): Int {
        return this.value
    }

    fun toBinary(dataOutputStream: DataOutputStream) throws IOException {
        dataOutputStream.writeByte(105)
        dataOutputStream.writeInt(this.value)
    }

    fun toXML(xmlSerializer: XmlSerializer) throws IOException {
        xmlSerializer.startTag("", "integer")
        xmlSerializer.text(Integer.toString(this.value))
        xmlSerializer.endTag("", "integer")
    }
}
