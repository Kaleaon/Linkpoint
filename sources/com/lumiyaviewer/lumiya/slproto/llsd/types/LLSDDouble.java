// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

public class LLSDDouble extends LLSDNode
{

    private double value;

    public LLSDDouble(double d)
    {
        value = d;
    }

    public LLSDDouble(String s)
    {
        value = Double.parseDouble(s);
    }

    public double asDouble()
    {
        return value;
    }

    public void toBinary(DataOutputStream dataoutputstream)
        throws IOException
    {
        dataoutputstream.writeByte(114);
        dataoutputstream.writeDouble(value);
    }

    public void toXML(XmlSerializer xmlserializer)
        throws IOException
    {
        xmlserializer.startTag("", "real");
        xmlserializer.text(Double.toString(value));
        xmlserializer.endTag("", "real");
    }
}
