package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDInvalidKeyException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
import java.io.DataOutputStream
import java.io.IOException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlSerializer

class LLSDArray : LLSDNode {
    private val items: ArrayList<LLSDNode> = ArrayList()

    constructor()

    @Throws(XmlPullParserException::class, IOException::class, LLSDXMLException::class)
    constructor(parser: XmlPullParser) {
        while (parser.nextTag() != 3) {
            items.add(LLSDNodeFactory.parseNode(parser))
        }
    }

    constructor(vararg nodes: LLSDNode) {
        for (node in nodes) {
            items.add(node)
        }
    }

    fun add(node: LLSDNode) {
        items.add(node)
    }

    @Throws(LLSDInvalidKeyException::class)
    override fun byIndex(index: Int): LLSDNode {
        if (index < 0 || index >= items.size) {
            throw LLSDInvalidKeyException(
                String.format("Array index out of range: req %d, size %d", index, items.size)
            )
        }
        return items[index]
    }

    override fun getCount(): Int = items.size

    @Throws(IOException::class)
    override fun toBinary(output: DataOutputStream) {
        output.writeByte(91)
        output.writeInt(items.size)
        for (node in items) {
            node.toBinary(output)
        }
        output.writeByte(93)
    }

    @Throws(IOException::class)
    override fun toXML(serializer: XmlSerializer) {
        serializer.startTag("", "array")
        for (node in items) {
            node.toXML(serializer)
        }
        serializer.endTag("", "array")
    }
}
