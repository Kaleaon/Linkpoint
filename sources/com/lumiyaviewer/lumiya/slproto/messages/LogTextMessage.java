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

public class LogTextMessage extends SLMessage
{
    public static class DataBlock
    {

        public UUID FromAgentId;
        public double GlobalX;
        public double GlobalY;
        public byte Message[];
        public int Time;
        public UUID ToAgentId;

        public DataBlock()
        {
        }
    }


    public ArrayList DataBlock_Fields;

    public LogTextMessage()
    {
        DataBlock_Fields = new ArrayList();
        zeroCoded = true;
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = DataBlock_Fields.iterator();
        int i;
        for (i = 5; iterator.hasNext(); i = ((DataBlock)iterator.next()).Message.length + 54 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleLogTextMessage(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-121);
        bytebuffer.put((byte)DataBlock_Fields.size());
        DataBlock datablock;
        for (Iterator iterator = DataBlock_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, datablock.Message, 2))
        {
            datablock = (DataBlock)iterator.next();
            packUUID(bytebuffer, datablock.FromAgentId);
            packUUID(bytebuffer, datablock.ToAgentId);
            packDouble(bytebuffer, datablock.GlobalX);
            packDouble(bytebuffer, datablock.GlobalY);
            packInt(bytebuffer, datablock.Time);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            DataBlock datablock = new DataBlock();
            datablock.FromAgentId = unpackUUID(bytebuffer);
            datablock.ToAgentId = unpackUUID(bytebuffer);
            datablock.GlobalX = unpackDouble(bytebuffer);
            datablock.GlobalY = unpackDouble(bytebuffer);
            datablock.Time = unpackInt(bytebuffer);
            datablock.Message = unpackVariable(bytebuffer, 2);
            DataBlock_Fields.add(datablock);
        }

    }
}
