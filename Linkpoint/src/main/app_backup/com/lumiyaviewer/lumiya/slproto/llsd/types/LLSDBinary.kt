package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer
import android.util.Base64

class LLSDBinary : LLSDNode {
    private val value: ByteArray

    constructor(str: String) {
        this.value = Base64.decode(str, Base64.DEFAULT)
    }

    constructor(bArr: ByteArray) {
        this.value = bArr
    }

    override fun asBinary(): ByteArray {
        return this.value
    }

    override fun asInt(): Int {
        var i = 0
        var b = 0
        while (i < 4 && i < this.value.size) {
            b = (b shl 8) or (this.value[i].toInt() and 0xFF)
            i++
        }
        return b
    }

    override fun asLong(): Long {
        var j = 0L
        var i = 0
        while (i < 8 && i < this.value.size) {
            j = (j shl 8) or (this.value[i].toLong() and 0xFF)
            i++
        }
        return j
    }

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(98) // 'b'
        dataOutputStream.writeInt(this.value.size)
        dataOutputStream.write(this.value)
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "binary")
        xmlSerializer.text(Base64.encodeToString(this.value, Base64.DEFAULT))
        xmlSerializer.endTag("", "binary")
    }
    
    override fun toString(): String {
        return Base64.encodeToString(this.value, Base64.DEFAULT)
    }
}
