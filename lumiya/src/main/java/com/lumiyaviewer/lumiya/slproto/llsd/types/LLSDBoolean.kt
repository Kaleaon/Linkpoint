package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDBoolean : LLSDNode {
    private val value: Boolean

    constructor(str: String) {
        this.value = when {
            str.equals("true", ignoreCase = true) -> true
            str.equals("false", ignoreCase = true) -> false
            else -> try {
                str.toInt() != 0
            } catch (e: Exception) {
                true // fallback as per original logic (defaulted to true if not false?)
                // Original: value = Integer.parseInt(str) == 0 ? false : z (z=true)
                // So if not 0, it is true.
            }
        }
    }

    constructor(z: Boolean) {
        this.value = z
    }

    override fun asBoolean(): Boolean {
        return this.value
    }

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(if (this.value) 49 else 48) // '1' or '0'
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "Boolean")
        xmlSerializer.text(if (this.value) "1" else "0")
        xmlSerializer.endTag("", "Boolean")
    }
}
