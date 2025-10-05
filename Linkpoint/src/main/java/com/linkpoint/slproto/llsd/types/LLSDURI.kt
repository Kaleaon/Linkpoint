package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.llsd.LLSDNode
import org.xmlpull.v1.XmlSerializer
import java.io.DataOutputStream
import java.io.IOException
import java.net.URI

class LLSDURI : LLSDNode {
    private val value: URI

    constructor(str: String) {
        value = URI.create("")
    }

    constructor(uri: URI) {
        value = uri
    }

    override fun asURI(): URI {
        return value
    }

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        val uri = value.toString()
        dataOutputStream.writeByte(108)
        if (uri.isEmpty()) {
            dataOutputStream.writeInt(0)
            return
        }
        val bytes = SLMessage.stringToVariableUTF(uri)
        dataOutputStream.writeInt(bytes.size)
        dataOutputStream.write(bytes)
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "uri")
        xmlSerializer.text(value.toString())
        xmlSerializer.endTag("", "uri")
    }
}