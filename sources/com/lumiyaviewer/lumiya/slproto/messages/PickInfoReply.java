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

public class PickInfoReply extends SLMessage
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

        public UUID CreatorID;
        public byte Desc[];
        public boolean Enabled;
        public byte Name[];
        public byte OriginalName[];
        public UUID ParcelID;
        public UUID PickID;
        public LLVector3d PosGlobal;
        public byte SimName[];
        public UUID SnapshotID;
        public int SortOrder;
        public boolean TopPick;
        public byte User[];

        public Data()
        {
        }
    }


    public AgentData AgentData_Field;
    public Data Data_Field;

    public PickInfoReply()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return Data_Field.Name.length + 50 + 2 + Data_Field.Desc.length + 16 + 1 + Data_Field.User.length + 1 + Data_Field.OriginalName.length + 1 + Data_Field.SimName.length + 24 + 4 + 1 + 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandlePickInfoReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-72);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, Data_Field.PickID);
        packUUID(bytebuffer, Data_Field.CreatorID);
        packBoolean(bytebuffer, Data_Field.TopPick);
        packUUID(bytebuffer, Data_Field.ParcelID);
        packVariable(bytebuffer, Data_Field.Name, 1);
        packVariable(bytebuffer, Data_Field.Desc, 2);
        packUUID(bytebuffer, Data_Field.SnapshotID);
        packVariable(bytebuffer, Data_Field.User, 1);
        packVariable(bytebuffer, Data_Field.OriginalName, 1);
        packVariable(bytebuffer, Data_Field.SimName, 1);
        packLLVector3d(bytebuffer, Data_Field.PosGlobal);
        packInt(bytebuffer, Data_Field.SortOrder);
        packBoolean(bytebuffer, Data_Field.Enabled);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        Data_Field.PickID = unpackUUID(bytebuffer);
        Data_Field.CreatorID = unpackUUID(bytebuffer);
        Data_Field.TopPick = unpackBoolean(bytebuffer);
        Data_Field.ParcelID = unpackUUID(bytebuffer);
        Data_Field.Name = unpackVariable(bytebuffer, 1);
        Data_Field.Desc = unpackVariable(bytebuffer, 2);
        Data_Field.SnapshotID = unpackUUID(bytebuffer);
        Data_Field.User = unpackVariable(bytebuffer, 1);
        Data_Field.OriginalName = unpackVariable(bytebuffer, 1);
        Data_Field.SimName = unpackVariable(bytebuffer, 1);
        Data_Field.PosGlobal = unpackLLVector3d(bytebuffer);
        Data_Field.SortOrder = unpackInt(bytebuffer);
        Data_Field.Enabled = unpackBoolean(bytebuffer);
    }
}
