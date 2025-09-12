// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ParcelInfoReply extends SLMessage
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

        public int ActualArea;
        public int AuctionID;
        public int BillableArea;
        public byte Desc[];
        public float Dwell;
        public int Flags;
        public float GlobalX;
        public float GlobalY;
        public float GlobalZ;
        public byte Name[];
        public UUID OwnerID;
        public UUID ParcelID;
        public int SalePrice;
        public byte SimName[];
        public UUID SnapshotID;

        public Data()
        {
        }
    }


    public AgentData AgentData_Field;
    public Data Data_Field;

    public ParcelInfoReply()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return Data_Field.Name.length + 33 + 1 + Data_Field.Desc.length + 4 + 4 + 1 + 4 + 4 + 4 + 1 + Data_Field.SimName.length + 16 + 4 + 4 + 4 + 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleParcelInfoReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)55);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, Data_Field.ParcelID);
        packUUID(bytebuffer, Data_Field.OwnerID);
        packVariable(bytebuffer, Data_Field.Name, 1);
        packVariable(bytebuffer, Data_Field.Desc, 1);
        packInt(bytebuffer, Data_Field.ActualArea);
        packInt(bytebuffer, Data_Field.BillableArea);
        packByte(bytebuffer, (byte)Data_Field.Flags);
        packFloat(bytebuffer, Data_Field.GlobalX);
        packFloat(bytebuffer, Data_Field.GlobalY);
        packFloat(bytebuffer, Data_Field.GlobalZ);
        packVariable(bytebuffer, Data_Field.SimName, 1);
        packUUID(bytebuffer, Data_Field.SnapshotID);
        packFloat(bytebuffer, Data_Field.Dwell);
        packInt(bytebuffer, Data_Field.SalePrice);
        packInt(bytebuffer, Data_Field.AuctionID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        Data_Field.ParcelID = unpackUUID(bytebuffer);
        Data_Field.OwnerID = unpackUUID(bytebuffer);
        Data_Field.Name = unpackVariable(bytebuffer, 1);
        Data_Field.Desc = unpackVariable(bytebuffer, 1);
        Data_Field.ActualArea = unpackInt(bytebuffer);
        Data_Field.BillableArea = unpackInt(bytebuffer);
        Data_Field.Flags = unpackByte(bytebuffer) & 0xff;
        Data_Field.GlobalX = unpackFloat(bytebuffer);
        Data_Field.GlobalY = unpackFloat(bytebuffer);
        Data_Field.GlobalZ = unpackFloat(bytebuffer);
        Data_Field.SimName = unpackVariable(bytebuffer, 1);
        Data_Field.SnapshotID = unpackUUID(bytebuffer);
        Data_Field.Dwell = unpackFloat(bytebuffer);
        Data_Field.SalePrice = unpackInt(bytebuffer);
        Data_Field.AuctionID = unpackInt(bytebuffer);
    }
}
