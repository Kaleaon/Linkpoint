package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import java.net.URI
import org.xmlpull.v1.XmlSerializer

class LLSDURI : LLSDNode {
    private val value: URI

    constructor(str: String) {
        this.value = try {
            URI.create(str)
        } catch (e: Exception) {
            URI.create("")
        }
    }

    constructor(uri: URI) {
        this.value = uri
    }

    fun asURI(): URI {
        return this.value
    }

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        val uri = this.value.toString()
        dataOutputStream.writeByte(108) // 'l'
        if (uri.isEmpty()) {
            dataOutputStream.writeInt(0)
            return
        }
        val stringToVariableUTF = SLMessage.stringToVariableUTF(uri)
        dataOutputStream.writeInt(stringToVariableUTF.size)
        dataOutputStream.write(stringToVariableUTF)
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "uri")
        xmlSerializer.text(this.value.toString())
        xmlSerializer.endTag("", "uri")
    }
    
    override fun asString(): String {
        return this.value.toString()
    }
}
