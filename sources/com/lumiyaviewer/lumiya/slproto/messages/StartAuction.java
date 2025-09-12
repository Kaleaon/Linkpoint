// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class StartAuction extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;

        public AgentData()
        {
        }
    }

    public static class ParcelData
    {

        public byte Name[];
        public UUID ParcelID;
        public UUID SnapshotID;

        public ParcelData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ParcelData ParcelData_Field;

    public StartAuction()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        ParcelData_Field = new ParcelData();
    }

    public int CalcPayloadSize()
    {
        return ParcelData_Field.Name.length + 33 + 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleStartAuction(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-27);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, ParcelData_Field.ParcelID);
        packUUID(bytebuffer, ParcelData_Field.SnapshotID);
        packVariable(bytebuffer, ParcelData_Field.Name, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        ParcelData_Field.ParcelID = unpackUUID(bytebuffer);
        ParcelData_Field.SnapshotID = unpackUUID(bytebuffer);
        ParcelData_Field.Name = unpackVariable(bytebuffer, 1);
    }
}
