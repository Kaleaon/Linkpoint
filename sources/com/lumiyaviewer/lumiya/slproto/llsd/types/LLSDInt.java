// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

public class LLSDInt extends LLSDNode
{

    private int value;

    public LLSDInt(int i)
    {
        value = i;
    }

    public LLSDInt(String s)
    {
        try
        {
            value = Integer.parseInt(s);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (String s)
        {
            value = 0;
        }
    }

    public boolean asBoolean()
    {
        boolean flag = false;
        if (value != 0)
        {
            flag = true;
        }
        return flag;
    }

    public int asInt()
    {
        return value;
    }

    public void toBinary(DataOutputStream dataoutputstream)
        throws IOException
    {
        dataoutputstream.writeByte(105);
        dataoutputstream.writeInt(value);
    }

    public void toXML(XmlSerializer xmlserializer)
        throws IOException
    {
        xmlserializer.startTag("", "integer");
        xmlserializer.text(Integer.toString(value));
        xmlserializer.endTag("", "integer");
    }
}
