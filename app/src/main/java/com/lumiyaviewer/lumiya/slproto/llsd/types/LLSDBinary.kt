package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.base64.Base64
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import org.xmlpull.v1.XmlSerializer

class LLSDBinary : LLSDNode {
    private val value: ByteArray

    constructor(str: String) {
        this.value = Base64.decode(str)
    }

    constructor(bArr: ByteArray) {
        this.value = bArr
    }

    override fun asBinary(): ByteArray {
        return this.value
    }

    override fun asInt(): Int {
        var result = 0
        var i = 0
        while (i < 4 && i < this.value.size) {
            result = (result shl 8) or (this.value[i].toInt() and 0xFF)
            i++
        }
        return result
    }

    override fun asLong(): Long {
        var result: Long = 0
        var i = 0
        while (i < 8 && i < this.value.size) {
            result = (result shl 8) or (this.value[i].toLong() and 0xFF)
            i++
        }
        return result
    }

    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(98)
        dataOutputStream.writeInt(this.value.size)
        dataOutputStream.write(this.value)
    }

    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "binary")
        xmlSerializer.text(android.util.Base64.encodeToString(this.value, android.util.Base64.DEFAULT))
        xmlSerializer.endTag("", "binary")
    }
}
