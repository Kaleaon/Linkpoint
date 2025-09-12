// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ParcelPropertiesRequestByID extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class ParcelData
    {

        public int LocalID;
        public int SequenceID;

        public ParcelData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ParcelData ParcelData_Field;

    public ParcelPropertiesRequestByID()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        ParcelData_Field = new ParcelData();
    }

    public int CalcPayloadSize()
    {
        return 44;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleParcelPropertiesRequestByID(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-59);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, ParcelData_Field.SequenceID);
        packInt(bytebuffer, ParcelData_Field.LocalID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        ParcelData_Field.SequenceID = unpackInt(bytebuffer);
        ParcelData_Field.LocalID = unpackInt(bytebuffer);
    }
}
