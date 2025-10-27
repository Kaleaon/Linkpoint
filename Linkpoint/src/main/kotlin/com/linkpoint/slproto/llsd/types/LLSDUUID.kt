package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import java.util.UUID
import org.xmlpull.v1.XmlSerializer

class LLSDUUID : LLSDNode() {
    private UUID value

    public LLSDUUID() {
        this.value = null
    }

    public LLSDUUID(String str) {
        val length: Int = str.length()
        val j: Long = 0
        val i: Int = 0
        val i2: Int = 0
        val j2: Long = 0
        val j3: Long = 0
        val i3: Int = 0
        while (i3 < length) {
            val charAt: Char = str.charAt(i3)
            if (charAt != '-') {
                j = (j << 4) | ((Long) ((charAt < '0' || charAt > '9') ? (charAt < 'a' || charAt > 'f') ? (charAt < 'A' || charAt > 'F') ? 0 : (charAt - 'A') + 10 : (charAt - 'a') + 10 : charAt - '0'))
                i++
                if (i >= 16) {
                    if (i2 == 0) {
                        j3 = j
                    } else {
                        j2 = j
                    }
                    i2++
                }
            }
            i3++
            j2 = j2
            j3 = j3
            i = i
            i2 = i2
        }
        this.value = UUID(j3, j2)
    }

    public LLSDUUID(UUID uuid) {
        this.value = uuid
    }

     public fun asString(): String {
        return this.value.toString()
    }

     public fun asUUID(): UUID {
        return this.value
    }

    fun toBinary(dataOutputStream: DataOutputStream) throws IOException {
        dataOutputStream.writeByte(117)
        dataOutputStream.writeLong(this.value.getMostSignificantBits())
        dataOutputStream.writeLong(this.value.getLeastSignificantBits())
    }

    fun toXML(xmlSerializer: XmlSerializer) throws IOException {
        xmlSerializer.startTag("", "uuid")
        if (this.value != null) {
            xmlSerializer.text(this.value.toString())
        }
        xmlSerializer.endTag("", "uuid")
    }
}
