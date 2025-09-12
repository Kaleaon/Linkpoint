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

public class UUIDGroupNameReply extends SLMessage
{
    public static class UUIDNameBlock
    {

        public byte GroupName[];
        public UUID ID;

        public UUIDNameBlock()
        {
        }
    }


    public ArrayList UUIDNameBlock_Fields;

    public UUIDGroupNameReply()
    {
        UUIDNameBlock_Fields = new ArrayList();
        zeroCoded = false;
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = UUIDNameBlock_Fields.iterator();
        int i;
        for (i = 5; iterator.hasNext(); i = ((UUIDNameBlock)iterator.next()).GroupName.length + 17 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleUUIDGroupNameReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-18);
        bytebuffer.put((byte)UUIDNameBlock_Fields.size());
        UUIDNameBlock uuidnameblock;
        for (Iterator iterator = UUIDNameBlock_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, uuidnameblock.GroupName, 1))
        {
            uuidnameblock = (UUIDNameBlock)iterator.next();
            packUUID(bytebuffer, uuidnameblock.ID);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            UUIDNameBlock uuidnameblock = new UUIDNameBlock();
            uuidnameblock.ID = unpackUUID(bytebuffer);
            uuidnameblock.GroupName = unpackVariable(bytebuffer, 1);
            UUIDNameBlock_Fields.add(uuidnameblock);
        }

    }
}
