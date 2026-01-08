package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import java.util.Date
import org.xmlpull.v1.XmlSerializer

class LLSDDate : LLSDNode {
    private val value: Date

    constructor(str: String) {
        // Deprecated Date constructor, but maintaining compatibility
        this.value = try {
            Date(str)
        } catch (e: Exception) {
            Date()
        }
    }

    constructor(date: Date) {
        this.value = date
    }

    override fun asDate(): Date {
        return this.value
    }

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(100)
        dataOutputStream.writeDouble((this.value.time / 1000).toDouble())
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "date")
        // toGMTString is deprecated, but using it for now as per original code
        @Suppress("DEPRECATION")
        xmlSerializer.text(this.value.toGMTString())
        xmlSerializer.endTag("", "date")
    }
}
