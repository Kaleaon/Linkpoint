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

public class PreloadSound extends SLMessage
{
    public static class DataBlock
    {

        public UUID ObjectID;
        public UUID OwnerID;
        public UUID SoundID;

        public DataBlock()
        {
        }
    }


    public ArrayList DataBlock_Fields;

    public PreloadSound()
    {
        DataBlock_Fields = new ArrayList();
        zeroCoded = false;
    }

    public int CalcPayloadSize()
    {
        return DataBlock_Fields.size() * 48 + 3;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandlePreloadSound(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)-1);
        bytebuffer.put((byte)15);
        bytebuffer.put((byte)DataBlock_Fields.size());
        DataBlock datablock;
        for (Iterator iterator = DataBlock_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, datablock.SoundID))
        {
            datablock = (DataBlock)iterator.next();
            packUUID(bytebuffer, datablock.ObjectID);
            packUUID(bytebuffer, datablock.OwnerID);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            DataBlock datablock = new DataBlock();
            datablock.ObjectID = unpackUUID(bytebuffer);
            datablock.OwnerID = unpackUUID(bytebuffer);
            datablock.SoundID = unpackUUID(bytebuffer);
            DataBlock_Fields.add(datablock);
        }

    }
}
