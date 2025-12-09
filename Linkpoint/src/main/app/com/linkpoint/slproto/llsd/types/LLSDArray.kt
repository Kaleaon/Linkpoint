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

class LLSDArray : LLSDNode {
    private ArrayList<LLSDNode> items = ArrayList<>()

    LLSDArray() {
    }

    LLSDArray(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, LLSDXMLException {
        while (xmlPullParser.nextTag() != 3) {
            this.items.add(LLSDNodeFactory.parseNode(xmlPullParser))
        }
    }

    LLSDArray(LLSDNode... lLSDNodeArr) {
        for (LLSDNode add : lLSDNodeArr) {
            this.items.add(add)
        }
    }

    fun add(LLSDNode lLSDNode): Unit {
        this.items.add(lLSDNode)
    }

    @Throws(LLSDInvalidKeyException::class)

    fun byIndex(Int i): LLSDNode {
        if (i >= 0 && i < this.items.size()) {
            return this.items.get(i)
        }
        throw LLSDInvalidKeyException(String.format("Array index out of range: req %d, size %d", Array<Any>{Integer.valueOf(i), Integer.valueOf(this.items.size())}))
    }

    fun getCount(): Int {
        return this.items.size()
    }

    @Throws(IOException::class)

    fun toBinary(DataOutputStream dataOutputStream) {
        dataOutputStream.writeByte(91)
        dataOutputStream.writeInt(this.items.size())
        for (LLSDNode binary : this.items) {
            binary.toBinary(dataOutputStream)
        }
        dataOutputStream.writeByte(93)
    }

    @Throws(IOException::class)

    fun toXML(XmlSerializer xmlSerializer) {
        xmlSerializer.startTag("", "array")
        for (LLSDNode xml : this.items) {
            xml.toXML(xmlSerializer)
        }
        xmlSerializer.endTag("", "array")
    }
}
