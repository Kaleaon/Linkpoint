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
    private val items = ArrayList<LLSDNode>()

    constructor()

    @Throws(XmlPullParserException::class, IOException::class, LLSDXMLException::class)
    constructor(xmlPullParser: XmlPullParser) {
        while (xmlPullParser.nextTag() != 3) {
            this.items.add(LLSDNodeFactory.parseNode(xmlPullParser))
        }
    }

    constructor(vararg lLSDNodeArr: LLSDNode) {
        for (node in lLSDNodeArr) {
            this.items.add(node)
        }
    }

    fun add(lLSDNode: LLSDNode) {
        this.items.add(lLSDNode)
    }

    @Throws(LLSDInvalidKeyException::class)
    override fun byIndex(i: Int): LLSDNode {
        if (i >= 0 && i < this.items.size) {
            return this.items[i]
        }
        throw LLSDInvalidKeyException(String.format("Array index out of range: req %d, size %d", i, this.items.size))
    }

    override fun getCount(): Int {
        return this.items.size
    }

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte(91)
        dataOutputStream.writeInt(this.items.size)
        for (node in this.items) {
            node.toBinary(dataOutputStream)
        }
        dataOutputStream.writeByte(93)
    }

    @Throws(IOException::class)
    override fun toXML(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "array")
        for (node in this.items) {
            node.toXML(xmlSerializer)
        }
        xmlSerializer.endTag("", "array")
    }
}
