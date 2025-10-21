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

    public Boolean asBoolean() {
        return this.value != 0
    }

    public Int asInt() {
        return this.value
    }

    fun toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(105)
        dataOutputStream.writeInt(this.value)
    }

    fun toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "integer")
        xmlSerializer.text(Integer.toString(this.value))
        xmlSerializer.endTag("", "integer")
    }
}
