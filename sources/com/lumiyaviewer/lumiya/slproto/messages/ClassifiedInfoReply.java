// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ClassifiedInfoReply extends SLMessage
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

        public int Category;
        public int ClassifiedFlags;
        public UUID ClassifiedID;
        public int CreationDate;
        public UUID CreatorID;
        public byte Desc[];
        public int ExpirationDate;
        public byte Name[];
        public UUID ParcelID;
        public byte ParcelName[];
        public int ParentEstate;
        public LLVector3d PosGlobal;
        public int PriceForListing;
        public byte SimName[];
        public UUID SnapshotID;

        public Data()
        {
        }
    }


    public AgentData AgentData_Field;
    public Data Data_Field;

    public ClassifiedInfoReply()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return Data_Field.Name.length + 45 + 2 + Data_Field.Desc.length + 16 + 4 + 16 + 1 + Data_Field.SimName.length + 24 + 1 + Data_Field.ParcelName.length + 1 + 4 + 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleClassifiedInfoReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)44);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, Data_Field.ClassifiedID);
        packUUID(bytebuffer, Data_Field.CreatorID);
        packInt(bytebuffer, Data_Field.CreationDate);
        packInt(bytebuffer, Data_Field.ExpirationDate);
        packInt(bytebuffer, Data_Field.Category);
        packVariable(bytebuffer, Data_Field.Name, 1);
        packVariable(bytebuffer, Data_Field.Desc, 2);
        packUUID(bytebuffer, Data_Field.ParcelID);
        packInt(bytebuffer, Data_Field.ParentEstate);
        packUUID(bytebuffer, Data_Field.SnapshotID);
        packVariable(bytebuffer, Data_Field.SimName, 1);
        packLLVector3d(bytebuffer, Data_Field.PosGlobal);
        packVariable(bytebuffer, Data_Field.ParcelName, 1);
        packByte(bytebuffer, (byte)Data_Field.ClassifiedFlags);
        packInt(bytebuffer, Data_Field.PriceForListing);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        Data_Field.ClassifiedID = unpackUUID(bytebuffer);
        Data_Field.CreatorID = unpackUUID(bytebuffer);
        Data_Field.CreationDate = unpackInt(bytebuffer);
        Data_Field.ExpirationDate = unpackInt(bytebuffer);
        Data_Field.Category = unpackInt(bytebuffer);
        Data_Field.Name = unpackVariable(bytebuffer, 1);
        Data_Field.Desc = unpackVariable(bytebuffer, 2);
        Data_Field.ParcelID = unpackUUID(bytebuffer);
        Data_Field.ParentEstate = unpackInt(bytebuffer);
        Data_Field.SnapshotID = unpackUUID(bytebuffer);
        Data_Field.SimName = unpackVariable(bytebuffer, 1);
        Data_Field.PosGlobal = unpackLLVector3d(bytebuffer);
        Data_Field.ParcelName = unpackVariable(bytebuffer, 1);
        Data_Field.ClassifiedFlags = unpackByte(bytebuffer) & 0xff;
        Data_Field.PriceForListing = unpackInt(bytebuffer);
    }
}
