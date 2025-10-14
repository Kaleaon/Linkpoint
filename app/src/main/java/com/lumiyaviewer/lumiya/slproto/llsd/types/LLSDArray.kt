package com.lumiyaviewer.lumiya.slproto.llsd.types

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDInvalidKeyException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
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

    Unit add(LLSDNode lLSDNode) {
        this.items.add(lLSDNode)
    }

    LLSDNode byIndex(Int i) throws LLSDInvalidKeyException {
        if (i >= 0 && i < this.items.size()) {
            return this.items.get(i)
        }
        throw LLSDInvalidKeyException(String.format("Array index out of range: req %d, size %d", Object[]{Integer.valueOf(i), Integer.valueOf(this.items.size())}))
    }

    Int getCount() {
        return this.items.size()
    }

    Unit toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(91)
        dataOutputStream.writeInt(this.items.size())
        for (LLSDNode binary : this.items) {
            binary.toBinary(dataOutputStream)
        }
        dataOutputStream.writeByte(93)
    }

    Unit toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "array")
        for (LLSDNode xml : this.items) {
            xml.toXML(xmlSerializer)
        }
        xmlSerializer.endTag("", "array")
    }
}
