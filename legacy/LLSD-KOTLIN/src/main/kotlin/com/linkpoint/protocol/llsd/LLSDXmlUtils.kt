package com.linkpoint.protocol.llsd

import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object LLSDXmlUtils {
    private const val XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><llsd>"
    private const val XML_FOOTER = "</llsd>"

    fun wrap(value: LLSDValue): String = "$XML_HEADER${value.toXML()}$XML_FOOTER"

    fun wrapToBytes(value: LLSDValue): ByteArray = wrap(value).toByteArray(StandardCharsets.UTF_8)

    fun writeToStream(value: LLSDValue, outputStream: OutputStream) {
        val writer = OutputStreamWriter(outputStream, StandardCharsets.UTF_8)
        writer.write(XML_HEADER)
        writer.write(value.toXML())
        writer.write(XML_FOOTER)
        writer.flush()
    }

    fun parse(stream: InputStream): LLSDValue = LLSDParser.parseXML(stream)

    fun parse(data: ByteArray): LLSDValue = LLSDParser.parseXML(data)
}
