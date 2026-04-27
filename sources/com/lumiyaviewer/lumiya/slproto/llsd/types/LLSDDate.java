// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import org.xmlpull.v1.XmlSerializer;

public class LLSDDate extends LLSDNode
{

    private Date value;

    public LLSDDate(String s)
    {
        try
        {
            value = new Date(s);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (String s)
        {
            value = new Date();
        }
    }

    public LLSDDate(Date date)
    {
        value = date;
    }

    public Date asDate()
    {
        return value;
    }

    public void toBinary(DataOutputStream dataoutputstream)
        throws IOException
    {
        dataoutputstream.writeByte(100);
        dataoutputstream.writeDouble(value.getTime() / 1000L);
    }

    public void toXML(XmlSerializer xmlserializer)
        throws IOException
    {
        xmlserializer.startTag("", "date");
        xmlserializer.text(value.toGMTString());
        xmlserializer.endTag("", "date");
    }
}
