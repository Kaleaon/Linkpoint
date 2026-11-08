package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.base64.Base64
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDBinary : LLSDNode {
    private var value: ByteArray

    constructor(bytes: ByteArray) {
        value = bytes
    }

    constructor(base64: String) {
        value = Base64.decode(base64)
    }

    override fun asBinary(): ByteArray = value

    override fun asInt(): Int {
        var result = 0
        for (i in 0 until minOf(4, value.size)) {
            result = (result shl 8) or (value[i].toInt() and UnsignedBytes.MAX_VALUE.toInt())
        }
        return result
    }

    override fun asLong(): Long {
        var result = 0L
        for (i in 0 until minOf(8, value.size)) {
            result = (result shl 8) or (value[i].toLong() and UnsignedBytes.MAX_VALUE.toLong())
        }
        return result
    }

    @Throws(IOException::class)
    override fun toBinary(output: DataOutputStream) {
        output.writeByte(98)
        output.writeInt(value.size)
        output.write(value)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "binary")
        serializer.text(Base64.encodeToString(value, false))
        serializer.endTag("", "binary")
    }
}
