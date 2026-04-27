// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.xmlpull.v1.XmlSerializer;

public class LLSDString extends LLSDNode
{

    private String value;

    public LLSDString(String s)
    {
        value = s;
    }

    public boolean asBoolean()
    {
        return "true".equalsIgnoreCase(value);
    }

    public String asString()
    {
        return value;
    }

    public UUID asUUID()
    {
        return UUID.fromString(value);
    }

    public void toBinary(DataOutputStream dataoutputstream)
        throws IOException
    {
        dataoutputstream.writeByte(115);
        if (value.isEmpty())
        {
            dataoutputstream.writeInt(0);
            return;
        } else
        {
            byte abyte0[] = SLMessage.stringToVariableUTF(value);
            dataoutputstream.writeInt(abyte0.length);
            dataoutputstream.write(abyte0);
            return;
        }
    }

    public void toXML(XmlSerializer xmlserializer)
        throws IOException
    {
        xmlserializer.startTag("", "string");
        xmlserializer.text(value);
        xmlserializer.endTag("", "string");
    }
}
