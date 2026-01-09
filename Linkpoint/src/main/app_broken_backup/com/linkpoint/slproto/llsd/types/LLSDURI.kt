package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import java.net.URI
import org.xmlpull.v1.XmlSerializer

class LLSDURI : LLSDNode {
    private URI value

    LLSDURI(String str) {
        this.value = URI.create("")
    }

    LLSDURI(URI uri) {
        this.value = uri
    }

    fun asURI(): URI {
        return this.value
    }

    @Throws(IOException::class)

    fun toBinary(DataOutputStream dataOutputStream) {
        var uri: String = this.value.toString()
        dataOutputStream.writeByte(108)
        if (uri.isEmpty()) {
            dataOutputStream.writeInt(0)
            return
        }
        ByteArray stringToVariableUTF = SLMessage.stringToVariableUTF(uri)
        dataOutputStream.writeInt(stringToVariableUTF.size)
        dataOutputStream.write(stringToVariableUTF)
    }

    @Throws(IOException::class)

    fun toXML(XmlSerializer xmlSerializer) {
        xmlSerializer.startTag("", "uri")
        xmlSerializer.text(this.value.toString())
        xmlSerializer.endTag("", "uri")
    }
}
