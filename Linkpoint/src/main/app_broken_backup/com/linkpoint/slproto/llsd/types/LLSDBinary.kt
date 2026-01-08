package com.linkpoint.slproto.llsd.types

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.base64.Base64
import com.linkpoint.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDBinary : LLSDNode {
    private ByteArray value

    LLSDBinary(String str) {
        this.value = Base64.decode(str)
    }

    LLSDBinary(ByteArray bArr) {
        this.value = bArr
    }

    fun asBinary(): ByteArray {
        return this.value
    }

    fun asInt(): Int {
        Int i = 0
        byte b = 0
        while (i < 4 && i < this.value.size) {
            b = (b << 8) | (this.value[i] & UnsignedBytes.MAX_VALUE)
            i++
        }
        return b
    }

    fun asLong(): Long {
        Long j = 0
        Int i = 0
        while (i < 8 && i < this.value.size) {
            j = (j << 8) | ((Long) (this.value[i] & UnsignedBytes.MAX_VALUE))
            i++
        }
        return j
    }

    @Throws(IOException::class)

    fun toBinary(DataOutputStream dataOutputStream) {
        dataOutputStream.writeByte(98)
        dataOutputStream.writeInt(this.value.size)
        dataOutputStream.write(this.value)
    }

    @Throws(IOException::class)

    fun toXML(XmlSerializer xmlSerializer) {
        xmlSerializer.startTag("", "binary")
        xmlSerializer.text(android.util.Base64.encodeToString(this.value, android.util.Base64.DEFAULT))
        xmlSerializer.endTag("", "binary")
    }
}
