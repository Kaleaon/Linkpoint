package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import java.net.URI
import org.xmlpull.v1.XmlSerializer

class LLSDURI(private val value: String) : LLSDNode() {

    override fun asURI(): URI = URI.create(value)

    override fun asString(): String = value

    @Throws(IOException::class)
    override fun toBinary(stream: DataOutputStream) {
        stream.writeByte('l'.code)
        if (value.isEmpty()) {
            stream.writeInt(0)
            return
        }
        val bytes = SLMessage.stringToVariableUTF(value)
        stream.writeInt(bytes.size)
        stream.write(bytes)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "uri")
        serializer.text(value)
        serializer.endTag("", "uri")
    }
}
