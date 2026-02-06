package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDBoolean : LLSDNode {
    private var value: Boolean

    constructor(text: String) {
        value = when {
            text.equals("true", ignoreCase = true) -> true
            text.equals("false", ignoreCase = true) -> false
            else -> text.toInt() != 0
        }
    }

    constructor(value: Boolean) {
        this.value = value
    }

    override fun asBoolean(): Boolean = value

    @Throws(IOException::class)
    override fun toBinary(output: DataOutputStream) {
        output.writeByte(if (value) 49 else 48)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "boolean")
        serializer.text(if (value) "1" else "0")
        serializer.endTag("", "boolean")
    }
}
