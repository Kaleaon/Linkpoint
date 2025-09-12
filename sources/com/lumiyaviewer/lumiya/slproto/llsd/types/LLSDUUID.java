// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd.types;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.xmlpull.v1.XmlSerializer;

public class LLSDUUID extends LLSDNode
{

    private UUID value;

    public LLSDUUID()
    {
        value = null;
    }

    public LLSDUUID(String s)
    {
        int j;
        int k;
        int i1;
        int j1;
        long l1;
        long l2;
        long l3;
        j1 = s.length();
        l1 = 0L;
        k = 0;
        j = 0;
        l2 = 0L;
        l3 = 0L;
        i1 = 0;
_L2:
        int i;
        long l4;
        if (i1 >= j1)
        {
            break; /* Loop/switch isn't completed */
        }
        char c = s.charAt(i1);
        l4 = l1;
        i = k;
        if (c == '-')
        {
            break MISSING_BLOCK_LABEL_238;
        }
        if (c >= '0' && c <= '9')
        {
            i = c - 48;
        } else
        if (c >= 'a' && c <= 'f')
        {
            i = (c - 97) + 10;
        } else
        if (c >= 'A' && c <= 'F')
        {
            i = (c - 65) + 10;
        } else
        {
            i = 0;
        }
        l1 = l1 << 4 | (long)i;
        k++;
        l4 = l1;
        i = k;
        if (k < 16)
        {
            break MISSING_BLOCK_LABEL_238;
        }
        if (j == 0)
        {
            l3 = l1;
        } else
        {
            l2 = l1;
        }
        l4 = l2;
        i = j + 1;
        j = k;
        l2 = l3;
        l3 = l4;
_L3:
        i1++;
        l4 = l3;
        l3 = l2;
        k = j;
        l2 = l4;
        j = i;
        if (true) goto _L2; else goto _L1
_L1:
        value = new UUID(l3, l2);
        return;
        long l5 = l3;
        int l = i;
        i = j;
        j = l;
        l1 = l4;
        l3 = l2;
        l2 = l5;
          goto _L3
    }

    public LLSDUUID(UUID uuid)
    {
        value = uuid;
    }

    public String asString()
    {
        return value.toString();
    }

    public UUID asUUID()
    {
        return value;
    }

    public void toBinary(DataOutputStream dataoutputstream)
        throws IOException
    {
        dataoutputstream.writeByte(117);
        dataoutputstream.writeLong(value.getMostSignificantBits());
        dataoutputstream.writeLong(value.getLeastSignificantBits());
    }

    public void toXML(XmlSerializer xmlserializer)
        throws IOException
    {
        xmlserializer.startTag("", "uuid");
        if (value != null)
        {
            xmlserializer.text(value.toString());
        }
        xmlserializer.endTag("", "uuid");
    }
}
