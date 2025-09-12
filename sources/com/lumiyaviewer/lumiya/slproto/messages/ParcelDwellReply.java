// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ParcelDwellReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;

        public AgentData()
        {
        }
    }

    public static class Data
    {

        public float Dwell;
        public int LocalID;
        public UUID ParcelID;

        public Data()
        {
        }
    }


    public AgentData AgentData_Field;
    public Data Data_Field;

    public ParcelDwellReply()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return 44;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleParcelDwellReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-37);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packInt(bytebuffer, Data_Field.LocalID);
        packUUID(bytebuffer, Data_Field.ParcelID);
        packFloat(bytebuffer, Data_Field.Dwell);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        Data_Field.LocalID = unpackInt(bytebuffer);
        Data_Field.ParcelID = unpackUUID(bytebuffer);
        Data_Field.Dwell = unpackFloat(bytebuffer);
    }
}
