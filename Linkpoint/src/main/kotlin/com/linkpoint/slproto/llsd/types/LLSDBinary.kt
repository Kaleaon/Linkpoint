package com.linkpoint.slproto.llsd.types

import android.util.Base64
import com.linkpoint.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlSerializer

class LLSDBinary(private val value: ByteArray) : LLSDNode() {

    constructor(base64: String) : this(decode(base64))

    override fun asBinary(): ByteArray = value

    @Throws(IOException::class)
    override fun toBinary(stream: DataOutputStream) {
        stream.writeByte('b'.code)
        stream.writeInt(value.size)
        stream.write(value)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "binary")
        serializer.text(Base64.encodeToString(value, Base64.NO_WRAP))
        serializer.endTag("", "binary")
    }

    companion object {
        private fun decode(text: String): ByteArray {
            return try {
                Base64.decode(text, Base64.DEFAULT)
            } catch (_: IllegalArgumentException) {
                text.toByteArray()
            }
        }
    }
}
