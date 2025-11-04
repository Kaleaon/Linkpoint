package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDInvalidKeyException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: classes.dex */
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
        for (LLSDNode lLSDNode : lLSDNodeArr) {
            this.items.add(lLSDNode);
        }
    }

    public void add(LLSDNode lLSDNode) {
        this.items.add(lLSDNode);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public LLSDNode byIndex(int i) throws LLSDInvalidKeyException {
        if (i < 0 || i >= this.items.size()) {
            throw new LLSDInvalidKeyException(String.format("Array index out of range: req %d, size %d", Integer.valueOf(i), Integer.valueOf(this.items.size())));
        }
        return this.items.get(i);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public int getCount() {
        return this.items.size();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public void toBinary(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(91);
        dataOutputStream.writeInt(this.items.size());
        Iterator<T> it = this.items.iterator();
        while (it.hasNext()) {
            ((LLSDNode) it.next()).toBinary(dataOutputStream);
        }
        dataOutputStream.writeByte(93);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
    public void toXML(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "array");
        Iterator<T> it = this.items.iterator();
        while (it.hasNext()) {
            ((LLSDNode) it.next()).toXML(xmlSerializer);
        }
        xmlSerializer.endTag("", "array");
    }
}
