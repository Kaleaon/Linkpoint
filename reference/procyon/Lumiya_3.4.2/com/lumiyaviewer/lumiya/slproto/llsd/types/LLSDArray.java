// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import org.xmlpull.v1.XmlSerializer;
import java.util.Iterator;
import java.io.DataOutputStream;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDInvalidKeyException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNodeFactory;
import org.xmlpull.v1.XmlPullParser;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;

public class LLSDArray extends LLSDNode
{
    private ArrayList<LLSDNode> items;
    
    public LLSDArray() {
        this.items = new ArrayList<LLSDNode>();
    }
    
    public LLSDArray(final XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, LLSDXMLException {
        this.items = new ArrayList<LLSDNode>();
        while (xmlPullParser.nextTag() != 3) {
            this.items.add(LLSDNodeFactory.parseNode(xmlPullParser));
        }
    }
    
    public LLSDArray(final LLSDNode... array) {
        this.items = new ArrayList<LLSDNode>();
        for (int i = 0; i < array.length; ++i) {
            this.items.add(array[i]);
        }
    }
    
    public void add(final LLSDNode e) {
        this.items.add(e);
    }
    
    @Override
    public LLSDNode byIndex(final int n) throws LLSDInvalidKeyException {
        if (n >= 0 && n < this.items.size()) {
            return this.items.get(n);
        }
        throw new LLSDInvalidKeyException(String.format("Array index out of range: req %d, size %d", n, this.items.size()));
    }
    
    @Override
    public int getCount() {
        return this.items.size();
    }
    
    @Override
    public void toBinary(final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(91);
        dataOutputStream.writeInt(this.items.size());
        final Iterator<Object> iterator = this.items.iterator();
        while (iterator.hasNext()) {
            iterator.next().toBinary(dataOutputStream);
        }
        dataOutputStream.writeByte(93);
    }
    
    @Override
    public void toXML(final XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "array");
        final Iterator<Object> iterator = this.items.iterator();
        while (iterator.hasNext()) {
            iterator.next().toXML(xmlSerializer);
        }
        xmlSerializer.endTag("", "array");
    }
}
