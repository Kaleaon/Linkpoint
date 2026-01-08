package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDNode
import org.xmlpull.v1.XmlSerializer
import java.io.DataOutputStream
import java.io.IOException

class LLSDUndefined : LLSDNode() {
    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(33)
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "undef")
        xmlSerializer.endTag("", "undef")
    }
}