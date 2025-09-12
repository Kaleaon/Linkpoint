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

public class UUIDGroupNameRequest extends SLMessage
{
    public static class UUIDNameBlock
    {

        public UUID ID;

        public UUIDNameBlock()
        {
        }
    }


    public ArrayList UUIDNameBlock_Fields;

    public UUIDGroupNameRequest()
    {
        UUIDNameBlock_Fields = new ArrayList();
        zeroCoded = false;
    }

    public int CalcPayloadSize()
    {
        return UUIDNameBlock_Fields.size() * 16 + 5;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleUUIDGroupNameRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-19);
        bytebuffer.put((byte)UUIDNameBlock_Fields.size());
        for (Iterator iterator = UUIDNameBlock_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, ((UUIDNameBlock)iterator.next()).ID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            UUIDNameBlock uuidnameblock = new UUIDNameBlock();
            uuidnameblock.ID = unpackUUID(bytebuffer);
            UUIDNameBlock_Fields.add(uuidnameblock);
        }

    }
}
