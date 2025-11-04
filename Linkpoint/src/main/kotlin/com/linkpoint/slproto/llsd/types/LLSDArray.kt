package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDInvalidKeyException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.LLSDNodeFactory
import com.linkpoint.slproto.llsd.LLSDXMLException
import java.io.DataOutputStream
import java.io.IOException
import java.util.ArrayList
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlSerializer

class LLSDArray() : LLSDNode() {

    private val items = ArrayList<LLSDNode>()

    @Throws(XmlPullParserException::class, IOException::class, LLSDXMLException::class)
    constructor(parser: XmlPullParser) : this() {
        while (parser.nextTag() != XmlPullParser.END_TAG) {
            items.add(LLSDNodeFactory.parseNode(parser))
        }
    }

    constructor(vararg nodes: LLSDNode) : this() {
        items.addAll(nodes)
    }

    fun add(node: LLSDNode) {
        items.add(node)
    }

    override fun byIndex(index: Int): LLSDNode {
        if (index < 0 || index >= items.size) {
            throw LLSDInvalidKeyException("Array index out of range: req $index, size ${items.size}")
        }
        return items[index]
    }

    override fun getCount(): Int = items.size

    @Throws(IOException::class)
    override fun toBinary(stream: DataOutputStream) {
        stream.writeByte('['.code)
        stream.writeInt(items.size)
        for (item in items) {
            item.toBinary(stream)
        }
        stream.writeByte(']'.code)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "array")
        for (item in items) {
            item.toXML(serializer)
        }
        serializer.endTag("", "array")
    }
}
