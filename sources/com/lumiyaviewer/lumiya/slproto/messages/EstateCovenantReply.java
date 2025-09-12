// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class EstateCovenantReply extends SLMessage
{
    public static class Data
    {

        public UUID CovenantID;
        public int CovenantTimestamp;
        public byte EstateName[];
        public UUID EstateOwnerID;

        public Data()
        {
        }
    }


    public Data Data_Field;

    public EstateCovenantReply()
    {
        zeroCoded = false;
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return Data_Field.EstateName.length + 21 + 16 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleEstateCovenantReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-52);
        packUUID(bytebuffer, Data_Field.CovenantID);
        packInt(bytebuffer, Data_Field.CovenantTimestamp);
        packVariable(bytebuffer, Data_Field.EstateName, 1);
        packUUID(bytebuffer, Data_Field.EstateOwnerID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Data_Field.CovenantID = unpackUUID(bytebuffer);
        Data_Field.CovenantTimestamp = unpackInt(bytebuffer);
        Data_Field.EstateName = unpackVariable(bytebuffer, 1);
        Data_Field.EstateOwnerID = unpackUUID(bytebuffer);
    }
}
