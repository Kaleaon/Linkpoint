// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

public class LLSDBoolean extends LLSDNode
{

    private boolean value;

    public LLSDBoolean(String s)
    {
        boolean flag = true;
        super();
        if (s.equalsIgnoreCase("true"))
        {
            value = true;
            return;
        }
        if (s.equalsIgnoreCase("false"))
        {
            value = false;
            return;
        }
        if (Integer.parseInt(s) == 0)
        {
            flag = false;
        }
        value = flag;
    }

    public LLSDBoolean(boolean flag)
    {
        value = flag;
    }

    public boolean asBoolean()
    {
        return value;
    }

    public void toBinary(DataOutputStream dataoutputstream)
        throws IOException
    {
        byte byte0;
        if (value)
        {
            byte0 = 49;
        } else
        {
            byte0 = 48;
        }
        dataoutputstream.writeByte(byte0);
    }

    public void toXML(XmlSerializer xmlserializer)
        throws IOException
    {
        xmlserializer.startTag("", "boolean");
        String s;
        if (value)
        {
            s = "1";
        } else
        {
            s = "0";
        }
        xmlserializer.text(s);
        xmlserializer.endTag("", "boolean");
    }
}
