package com.linkpoint.slproto.llsd.types

import com.linkpoint.slproto.llsd.LLSDInvalidKeyException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.LLSDNodeFactory
import com.linkpoint.slproto.llsd.LLSDXMLException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlSerializer
import java.io.DataOutputStream
import java.io.IOException

class LLSDArray : LLSDNode {
    private val items = ArrayList<LLSDNode>()

    constructor()

    @Throws(XmlPullParserException::class, IOException::class, LLSDXMLException::class)
    constructor(xmlPullParser: XmlPullParser) {
        while (xmlPullParser.nextTag() != XmlPullParser.END_TAG) {
            items.add(LLSDNodeFactory.parseNode(xmlPullParser))
        }
    }

    constructor(vararg nodes: LLSDNode) {
        nodes.forEach { items.add(it) }
    }

    fun add(node: LLSDNode) {
        items.add(node)
    }

    @Throws(LLSDInvalidKeyException::class)
    override fun byIndex(index: Int): LLSDNode {
        if (index >= 0 && index < items.size) {
            return items[index]
        }
        throw LLSDInvalidKeyException(
            String.format("Array index out of range: req %d, size %d", index, items.size)
        )
    }

    override fun getCount(): Int {
        return items.size
    }

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(91)
        dataOutputStream.writeInt(items.size)
        for (item in items) {
            item.toBinary(dataOutputStream)
        }
        dataOutputStream.writeByte(93)
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "array")
        for (item in items) {
            item.toXML(xmlSerializer)
        }
        xmlSerializer.endTag("", "array")
    }
}