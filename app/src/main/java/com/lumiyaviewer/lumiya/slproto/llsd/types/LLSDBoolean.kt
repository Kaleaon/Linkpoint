package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import org.xmlpull.v1.XmlSerializer

class LLSDBoolean : LLSDNode {
    private val value: Boolean

    constructor(str: String) {
        this.value = when {
            str.equals("true", ignoreCase = true) -> true
            str.equals("false", ignoreCase = true) -> false
            else -> Integer.parseInt(str) != 0
        }
    }

    constructor(z: Boolean) {
        this.value = z
    }

    override fun asBoolean(): Boolean {
        return this.value
    }

    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(if (this.value) 49 else 48)
    }

    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "Boolean")
        xmlSerializer.text(if (this.value) "1" else "0")
        xmlSerializer.endTag("", "Boolean")
    }
}
