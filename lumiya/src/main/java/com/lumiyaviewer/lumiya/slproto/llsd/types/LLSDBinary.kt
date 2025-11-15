package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.base64.Base64
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDBinary : LLSDNode {
    private byte[] value

    LLSDBinary(String str) {
        this.value = Base64.decode(str)
    }

    LLSDBinary(byte[] bArr) {
        this.value = bArr
    }

    byte[] asBinary() {
        return this.value
    }

    Int asInt() {
        Int i = 0
        byte b = 0
        while (i < 4 && i < this.value.length) {
            b = (b << 8) | (this.value[i] & UnsignedBytes.MAX_VALUE)
            i++
        }
        return b
    }

    Long asLong() {
        Long j = 0
        Int i = 0
        while (i < 8 && i < this.value.length) {
            j = (j << 8) | ((Long) (this.value[i] & UnsignedBytes.MAX_VALUE))
            i++
        }
        return j
    }

    Unit toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(98)
        dataOutputStream.writeInt(this.value.length)
        dataOutputStream.write(this.value)
    }

    Unit toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "binary")
        xmlSerializer.text(android.util.Base64.encodeToString(this.value, android.util.Base64.DEFAULT))
        xmlSerializer.endTag("", "binary")
    }
}
