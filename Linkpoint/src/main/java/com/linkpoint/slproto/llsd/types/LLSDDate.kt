package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDNode
import org.xmlpull.v1.XmlSerializer
import java.io.DataOutputStream
import java.io.IOException
import java.util.Date

class LLSDDate : LLSDNode {
    private val value: Date

    constructor(str: String) {
        value = try {
            Date(str)
        } catch (e: Exception) {
            Date()
        }
    }

    constructor(date: Date) {
        this.value = date
    }

    override fun asDate(): Date {
        return value
    }

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(100)
        dataOutputStream.writeDouble((value.time / 1000).toDouble())
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "date")
        @Suppress("DEPRECATION")
        xmlSerializer.text(value.toGMTString())
        xmlSerializer.endTag("", "date")
    }
}