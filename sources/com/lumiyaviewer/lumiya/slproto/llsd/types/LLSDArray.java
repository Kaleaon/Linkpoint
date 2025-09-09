package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDInvalidKeyException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class LLSDArray extends LLSDNode {
    private ArrayList<LLSDNode> items = new ArrayList<>();

    public LLSDArray() {
    }

    public LLSDArray(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, LLSDXMLException {
        while (xmlPullParser.nextTag() != 3) {
            this.items.add(LLSDNodeFactory.parseNode(xmlPullParser));
        }
    }

    public LLSDArray(LLSDNode... lLSDNodeArr) {
        for (LLSDNode add : lLSDNodeArr) {
            this.items.add(add);
        }
    }

    public void add(LLSDNode lLSDNode) {
        this.items.add(lLSDNode);
    }

    public LLSDNode byIndex(int i) throws LLSDInvalidKeyException {
        if (i >= 0 && i < this.items.size()) {
            return this.items.get(i);
        }
        throw new LLSDInvalidKeyException(String.format("Array index out of range: req %d, size %d", new Object[]{Integer.valueOf(i), Integer.valueOf(this.items.size())}));
    }

    public int getCount() {
        return this.items.size();
    }

    public void toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(91);
        dataOutputStream.writeInt(this.items.size());
        for (LLSDNode binary : this.items) {
            binary.toBinary(dataOutputStream);
        }
        dataOutputStream.writeByte(93);
    }

    public void toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "array");
        for (LLSDNode xml : this.items) {
            xml.toXML(xmlSerializer);
        }
        xmlSerializer.endTag("", "array");
    }
}
