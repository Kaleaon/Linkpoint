package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.util.Date
import org.xmlpull.v1.XmlSerializer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class LLSDDate : LLSDNode {
    private val value: Date

    constructor(str: String) {
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

    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(100)
        dataOutputStream.writeDouble(this.value.time.toDouble() / 1000.0)
    }

    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "date")
        // Use thread-local formatter for thread-safety and efficiency
        xmlSerializer.text(ISO8601_FORMATTER.get().format(this.value))
        xmlSerializer.endTag("", "date")
    }

    companion object {
        // ThreadLocal SimpleDateFormat for thread-safe reuse
        private val ISO8601_FORMATTER = ThreadLocal.withInitial {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        }
    }
}
