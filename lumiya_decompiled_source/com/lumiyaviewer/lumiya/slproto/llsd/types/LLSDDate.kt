package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import java.util.Date
import org.xmlpull.v1.XmlSerializer

class LLSDDate : LLSDNode {
    private var value: Date

    constructor(text: String) {
        value = try {
            Date(text)
        } catch (_: Exception) {
            Date()
        }
    }

    constructor(date: Date) {
        value = date
    }

    override fun asDate(): Date = value

    @Throws(IOException::class)
    override fun toBinary(output: DataOutputStream) {
        output.writeByte(100)
        output.writeDouble(value.time / 1000.0)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "date")
        serializer.text(value.toGMTString())
        serializer.endTag("", "date")
    }
}
