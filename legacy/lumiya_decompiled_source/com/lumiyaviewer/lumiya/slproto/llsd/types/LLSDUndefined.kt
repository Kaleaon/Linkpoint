package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDUndefined : LLSDNode() {
    @Throws(IOException::class)
    override fun toBinary(output: DataOutputStream) {
        output.writeByte(33)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "undef")
        serializer.endTag("", "undef")
    }
}
