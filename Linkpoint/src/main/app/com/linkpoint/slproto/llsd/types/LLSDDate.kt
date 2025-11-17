package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import java.util.Date
import org.xmlpull.v1.XmlSerializer

class LLSDDate : LLSDNode {
    private Date value

    LLSDDate(String str) {
        try {
            this.value = Date(str)
        } catch (Exception e) {
            this.value = Date()
        }
    }

    LLSDDate(Date date) {
        this.value = date
    }

    Date asDate() {
        return this.value
    }

    Unit toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(100)
        dataOutputStream.writeDouble((double) (this.value.getTime() / 1000))
    }

    Unit toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "date")
        xmlSerializer.text(this.value.toGMTString())
        xmlSerializer.endTag("", "date")
    }
}
