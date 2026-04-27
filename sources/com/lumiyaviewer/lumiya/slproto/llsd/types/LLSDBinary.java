// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.base64.Base64;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

public class LLSDBinary extends LLSDNode
{

    private byte value[];

    public LLSDBinary(String s)
    {
        value = Base64.decode(s);
    }

    public LLSDBinary(byte abyte0[])
    {
        value = abyte0;
    }

    public byte[] asBinary()
    {
        return value;
    }

    public int asInt()
    {
        int i = 0;
        int j = 0;
        for (; i < 4 && i < value.length; i++)
        {
            j = j << 8 | value[i] & 0xff;
        }

        return j;
    }

    public long asLong()
    {
        long l = 0L;
        for (int i = 0; i < 8 && i < value.length; i++)
        {
            l = l << 8 | (long)(value[i] & 0xff);
        }

        return l;
    }

    public void toBinary(DataOutputStream dataoutputstream)
        throws IOException
    {
        dataoutputstream.writeByte(98);
        dataoutputstream.writeInt(value.length);
        dataoutputstream.write(value);
    }

    public void toXML(XmlSerializer xmlserializer)
        throws IOException
    {
        xmlserializer.startTag("", "binary");
        xmlserializer.text(Base64.encodeToString(value, false));
        xmlserializer.endTag("", "binary");
    }
}
