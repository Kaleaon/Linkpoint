package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDInvalidKeyException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.DataOutputStream
import java.io.IOException
import java.util.ArrayList
import org.xmlpull.v1.XmlSerializer

class LLSDArray : LLSDNode {
    val items = ArrayList<LLSDNode>()

    val value: List<LLSDNode>
        get() = items

    constructor()

    constructor(vararg nodes: LLSDNode) {
        items.addAll(nodes)
    }

    fun add(node: LLSDNode) {
        items.add(node)
    }

    @Throws(LLSDInvalidKeyException::class)
    override fun byIndex(i: Int): LLSDNode {
        if (i >= 0 && i < items.size) {
            return items[i]
        }
        throw LLSDInvalidKeyException("Array index out of range: req $i, size ${items.size}")
    }

    override fun getCount(): Int {
        return items.size
    }

    @Throws(IOException::class)
    override fun toBinary(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeByte('['.toInt())
        dataOutputStream.writeInt(items.size)
        for (item in items) {
            item.toBinary(dataOutputStream)
        }
        dataOutputStream.writeByte(']'.toInt())
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
