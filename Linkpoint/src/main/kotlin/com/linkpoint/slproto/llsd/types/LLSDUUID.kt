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
        Int length = str.length()
        Long j = 0
        Int i = 0
        Int i2 = 0
        Long j2 = 0
        Long j3 = 0
        Int i3 = 0
        while (i3 < length) {
            Char charAt = str.charAt(i3)
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

    public String asString() {
        return this.value.toString()
    }

    public UUID asUUID() {
        return this.value
    }

    fun toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(117)
        dataOutputStream.writeLong(this.value.getMostSignificantBits())
        dataOutputStream.writeLong(this.value.getLeastSignificantBits())
    }

    fun toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "uuid")
        if (this.value != null) {
            xmlSerializer.text(this.value.toString())
        }
        xmlSerializer.endTag("", "uuid")
    }
}
