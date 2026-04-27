// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

public class LLSDUndefined extends LLSDNode
{

    public LLSDUndefined()
    {
    }

    public void toBinary(DataOutputStream dataoutputstream)
        throws IOException
    {
        dataoutputstream.writeByte(33);
    }

    public void toXML(XmlSerializer xmlserializer)
        throws IOException
    {
        xmlserializer.startTag("", "undef");
        xmlserializer.endTag("", "undef");
    }
}
