// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class UUIDNameReply extends SLMessage
{
    public static class UUIDNameBlock
    {

        public byte FirstName[];
        public UUID ID;
        public byte LastName[];

        public UUIDNameBlock()
        {
        }
    }


    public ArrayList UUIDNameBlock_Fields;

    public UUIDNameReply()
    {
        UUIDNameBlock_Fields = new ArrayList();
        zeroCoded = false;
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = UUIDNameBlock_Fields.iterator();
        UUIDNameBlock uuidnameblock;
        int i;
        int j;
        for (i = 5; iterator.hasNext(); i = uuidnameblock.LastName.length + (j + 17 + 1) + i)
        {
            uuidnameblock = (UUIDNameBlock)iterator.next();
            j = uuidnameblock.FirstName.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleUUIDNameReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-20);
        bytebuffer.put((byte)UUIDNameBlock_Fields.size());
        UUIDNameBlock uuidnameblock;
        for (Iterator iterator = UUIDNameBlock_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, uuidnameblock.LastName, 1))
        {
            uuidnameblock = (UUIDNameBlock)iterator.next();
            packUUID(bytebuffer, uuidnameblock.ID);
            packVariable(bytebuffer, uuidnameblock.FirstName, 1);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            UUIDNameBlock uuidnameblock = new UUIDNameBlock();
            uuidnameblock.ID = unpackUUID(bytebuffer);
            uuidnameblock.FirstName = unpackVariable(bytebuffer, 1);
            uuidnameblock.LastName = unpackVariable(bytebuffer, 1);
            UUIDNameBlock_Fields.add(uuidnameblock);
        }

    }
}
