package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDNode
import org.xmlpull.v1.XmlSerializer
import java.io.DataOutputStream
import java.io.IOException

class LLSDBoolean : LLSDNode {
    private val value: Boolean

    constructor(str: String) {
        value = when {
            str.equals("true", ignoreCase = true) -> true
            str.equals("false", ignoreCase = true) -> false
            else -> str.toInt() != 0
        }
    }

    constructor(value: Boolean) {
        this.value = value
    }

    override fun asBoolean(): Boolean {
        return value
    }

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(if (value) 49 else 48)
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "boolean")
        xmlSerializer.text(if (value) "1" else "0")
        xmlSerializer.endTag("", "boolean")
    }
}