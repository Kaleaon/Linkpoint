// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import org.xmlpull.v1.XmlSerializer;

public class LLSDURI extends LLSDNode
{

    private URI value;

    public LLSDURI(String s)
    {
        value = URI.create("");
    }

    public LLSDURI(URI uri)
    {
        value = uri;
    }

    public URI asURI()
    {
        return value;
    }

    public void toBinary(DataOutputStream dataoutputstream)
        throws IOException
    {
        String s = value.toString();
        dataoutputstream.writeByte(108);
        if (s.isEmpty())
        {
            dataoutputstream.writeInt(0);
            return;
        } else
        {
            byte abyte0[] = SLMessage.stringToVariableUTF(s);
            dataoutputstream.writeInt(abyte0.length);
            dataoutputstream.write(abyte0);
            return;
        }
    }

    public void toXML(XmlSerializer xmlserializer)
        throws IOException
    {
        xmlserializer.startTag("", "uri");
        xmlserializer.text(value.toString());
        xmlserializer.endTag("", "uri");
    }
}
