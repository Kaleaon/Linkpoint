package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import org.xmlpull.v1.XmlSerializer

class LLSDDate(private val value: Date) : LLSDNode() {

    constructor(text: String) : this(parseDate(text))

    override fun asDate(): Date = value

    @Throws(IOException::class)
    override fun toBinary(stream: DataOutputStream) {
        stream.writeByte('d'.code)
        stream.writeDouble(value.time / 1000.0)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "date")
        serializer.text(DATE_FORMAT.format(value))
        serializer.endTag("", "date")
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        private fun parseDate(text: String): Date {
            return try {
                DATE_FORMAT.parse(text)
            } catch (_: ParseException) {
                Date()
            } ?: Date()
        }
    }
}
