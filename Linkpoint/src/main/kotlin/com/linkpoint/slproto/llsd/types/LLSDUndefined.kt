package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDUndefined : LLSDNode() {

    @Throws(IOException::class)
    override fun toBinary(stream: DataOutputStream) {
        stream.writeByte('!'.code)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "undef")
        serializer.endTag("", "undef")
    }
}
