// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

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

public class LLSDArray extends LLSDNode
{

    private ArrayList items;

    public LLSDArray()
    {
        items = new ArrayList();
    }

    public LLSDArray(XmlPullParser xmlpullparser)
        throws XmlPullParserException, IOException, LLSDXMLException
    {
        items = new ArrayList();
        for (; xmlpullparser.nextTag() != 3; items.add(LLSDNodeFactory.parseNode(xmlpullparser))) { }
    }

    public transient LLSDArray(LLSDNode allsdnode[])
    {
        items = new ArrayList();
        int i = 0;
        for (int j = allsdnode.length; i < j; i++)
        {
            LLSDNode llsdnode = allsdnode[i];
            items.add(llsdnode);
        }

    }

    public void add(LLSDNode llsdnode)
    {
        items.add(llsdnode);
    }

    public LLSDNode byIndex(int i)
        throws LLSDInvalidKeyException
    {
        if (i >= 0 && i < items.size())
        {
            return (LLSDNode)items.get(i);
        } else
        {
            throw new LLSDInvalidKeyException(String.format("Array index out of range: req %d, size %d", new Object[] {
                Integer.valueOf(i), Integer.valueOf(items.size())
            }));
        }
    }

    public int getCount()
    {
        return items.size();
    }

    public void toBinary(DataOutputStream dataoutputstream)
        throws IOException
    {
        dataoutputstream.writeByte(91);
        dataoutputstream.writeInt(items.size());
        for (Iterator iterator = items.iterator(); iterator.hasNext(); ((LLSDNode)iterator.next()).toBinary(dataoutputstream)) { }
        dataoutputstream.writeByte(93);
    }

    public void toXML(XmlSerializer xmlserializer)
        throws IOException
    {
        xmlserializer.startTag("", "array");
        for (Iterator iterator = items.iterator(); iterator.hasNext(); ((LLSDNode)iterator.next()).toXML(xmlserializer)) { }
        xmlserializer.endTag("", "array");
    }
}
