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

    URI asURI() {
        return this.value
    }

    Unit toBinary(DataOutputStream dataOutputStream) throws IOException {
        String uri = this.value.toString()
        dataOutputStream.writeByte(108)
        if (uri.isEmpty()) {
            dataOutputStream.writeInt(0)
            return
        }
        byte[] stringToVariableUTF = SLMessage.stringToVariableUTF(uri)
        dataOutputStream.writeInt(stringToVariableUTF.length)
        dataOutputStream.write(stringToVariableUTF)
    }

    Unit toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "uri")
        xmlSerializer.text(this.value.toString())
        xmlSerializer.endTag("", "uri")
    }
}
