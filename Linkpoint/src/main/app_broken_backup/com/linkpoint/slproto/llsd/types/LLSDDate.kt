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

    fun asDate(): Date {
        return this.value
    }

    @Throws(IOException::class)

    fun toBinary(DataOutputStream dataOutputStream) {
        dataOutputStream.writeByte(100)
        dataOutputStream.writeDouble((double) (this.value.getTime() / 1000))
    }

    @Throws(IOException::class)

    fun toXML(XmlSerializer xmlSerializer) {
        xmlSerializer.startTag("", "date")
        xmlSerializer.text(this.value.toGMTString())
        xmlSerializer.endTag("", "date")
    }
}
