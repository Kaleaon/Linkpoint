package com.linkpoint.slproto.llsd.types

import android.util.Base64
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.llsd.LLSDNode
import org.xmlpull.v1.XmlSerializer
import java.io.DataOutputStream
import java.io.IOException

class LLSDBinary : LLSDNode {
    private val value: ByteArray

    constructor(str: String) {
        this.value = com.linkpoint.base64.Base64.decode(str)
    }

    constructor(data: ByteArray) {
        this.value = data
    }

    override fun asBinary(): ByteArray {
        return value
    }

    override fun asInt(): Int {
        var result = 0
        var i = 0
        while (i < 4 && i < value.size) {
            result = (result shl 8) or (value[i].toInt() and UnsignedBytes.MAX_VALUE.toInt())
            i++
        }
        return result
    }

    override fun asLong(): Long {
        var result = 0L
        var i = 0
        while (i < 8 && i < value.size) {
            result = (result shl 8) or ((value[i].toInt() and UnsignedBytes.MAX_VALUE.toInt()).toLong())
            i++
        }
        return result
    }

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(98)
        dataOutputStream.writeInt(value.size)
        dataOutputStream.write(value)
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "binary")
        xmlSerializer.text(Base64.encodeToString(value, Base64.DEFAULT))
        xmlSerializer.endTag("", "binary")
    }
}