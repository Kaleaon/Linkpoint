package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import java.net.URI
import org.xmlpull.v1.XmlSerializer

class LLSDURI : LLSDNode {
    private var value: URI

    constructor(text: String) {
        value = URI.create(text)
    }

    constructor(uri: URI) {
        value = uri
    }

    override fun asURI(): URI = value

    override fun asString(): String = value.toString()

    @Throws(IOException::class)
    override fun toBinary(output: DataOutputStream) {
        output.writeByte(108)
        val text = value.toString()
        if (text.isEmpty()) {
            output.writeInt(0)
            return
        }
        val bytes = SLMessage.stringToVariableUTF(text)
        output.writeInt(bytes.size)
        output.write(bytes)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "uri")
        serializer.text(value.toString())
        serializer.endTag("", "uri")
    }
}
