package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.net.URI
import org.xmlpull.v1.XmlSerializer

class LLSDURI : LLSDNode {
    private val value: URI

    constructor(str: String) {
        this.value = URI.create(str)
    }

    constructor(uri: URI) {
        this.value = uri
    }

    override fun asURI(): URI {
        return this.value
    }

    override fun toBinary(dataOutputStream: DataOutputStream) {
        val uri = this.value.toString()
        dataOutputStream.writeByte(108)
        if (uri.isEmpty()) {
            dataOutputStream.writeInt(0)
            return
        }
        val stringToVariableUTF = SLMessage.stringToVariableUTF(uri)
        dataOutputStream.writeInt(stringToVariableUTF.size)
        dataOutputStream.write(stringToVariableUTF)
    }

    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "uri")
        xmlSerializer.text(this.value.toString())
        xmlSerializer.endTag("", "uri")
    }
}
