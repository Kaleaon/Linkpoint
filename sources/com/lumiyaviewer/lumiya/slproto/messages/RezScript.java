// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RezScript extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class InventoryBlock
    {

        public int BaseMask;
        public int CRC;
        public int CreationDate;
        public UUID CreatorID;
        public byte Description[];
        public int EveryoneMask;
        public int Flags;
        public UUID FolderID;
        public UUID GroupID;
        public int GroupMask;
        public boolean GroupOwned;
        public int InvType;
        public UUID ItemID;
        public byte Name[];
        public int NextOwnerMask;
        public UUID OwnerID;
        public int OwnerMask;
        public int SalePrice;
        public int SaleType;
        public UUID TransactionID;
        public int Type;

        public InventoryBlock()
        {
        }
    }

    public static class UpdateBlock
    {

        public boolean Enabled;
        public int ObjectLocalID;

        public UpdateBlock()
        {
        }
    }


    public AgentData AgentData_Field;
    public InventoryBlock InventoryBlock_Field;
    public UpdateBlock UpdateBlock_Field;

    public RezScript()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        UpdateBlock_Field = new UpdateBlock();
        InventoryBlock_Field = new InventoryBlock();
    }

    public int CalcPayloadSize()
    {
        return InventoryBlock_Field.Name.length + 129 + 1 + InventoryBlock_Field.Description.length + 4 + 4 + 57;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRezScript(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)48);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, AgentData_Field.GroupID);
        packInt(bytebuffer, UpdateBlock_Field.ObjectLocalID);
        packBoolean(bytebuffer, UpdateBlock_Field.Enabled);
        packUUID(bytebuffer, InventoryBlock_Field.ItemID);
        packUUID(bytebuffer, InventoryBlock_Field.FolderID);
        packUUID(bytebuffer, InventoryBlock_Field.CreatorID);
        packUUID(bytebuffer, InventoryBlock_Field.OwnerID);
        packUUID(bytebuffer, InventoryBlock_Field.GroupID);
        packInt(bytebuffer, InventoryBlock_Field.BaseMask);
        packInt(bytebuffer, InventoryBlock_Field.OwnerMask);
        packInt(bytebuffer, InventoryBlock_Field.GroupMask);
        packInt(bytebuffer, InventoryBlock_Field.EveryoneMask);
        packInt(bytebuffer, InventoryBlock_Field.NextOwnerMask);
        packBoolean(bytebuffer, InventoryBlock_Field.GroupOwned);
        packUUID(bytebuffer, InventoryBlock_Field.TransactionID);
        packByte(bytebuffer, (byte)InventoryBlock_Field.Type);
        packByte(bytebuffer, (byte)InventoryBlock_Field.InvType);
        packInt(bytebuffer, InventoryBlock_Field.Flags);
        packByte(bytebuffer, (byte)InventoryBlock_Field.SaleType);
        packInt(bytebuffer, InventoryBlock_Field.SalePrice);
        packVariable(bytebuffer, InventoryBlock_Field.Name, 1);
        packVariable(bytebuffer, InventoryBlock_Field.Description, 1);
        packInt(bytebuffer, InventoryBlock_Field.CreationDate);
        packInt(bytebuffer, InventoryBlock_Field.CRC);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.GroupID = unpackUUID(bytebuffer);
        UpdateBlock_Field.ObjectLocalID = unpackInt(bytebuffer);
        UpdateBlock_Field.Enabled = unpackBoolean(bytebuffer);
        InventoryBlock_Field.ItemID = unpackUUID(bytebuffer);
        InventoryBlock_Field.FolderID = unpackUUID(bytebuffer);
        InventoryBlock_Field.CreatorID = unpackUUID(bytebuffer);
        InventoryBlock_Field.OwnerID = unpackUUID(bytebuffer);
        InventoryBlock_Field.GroupID = unpackUUID(bytebuffer);
        InventoryBlock_Field.BaseMask = unpackInt(bytebuffer);
        InventoryBlock_Field.OwnerMask = unpackInt(bytebuffer);
        InventoryBlock_Field.GroupMask = unpackInt(bytebuffer);
        InventoryBlock_Field.EveryoneMask = unpackInt(bytebuffer);
        InventoryBlock_Field.NextOwnerMask = unpackInt(bytebuffer);
        InventoryBlock_Field.GroupOwned = unpackBoolean(bytebuffer);
        InventoryBlock_Field.TransactionID = unpackUUID(bytebuffer);
        InventoryBlock_Field.Type = unpackByte(bytebuffer);
        InventoryBlock_Field.InvType = unpackByte(bytebuffer);
        InventoryBlock_Field.Flags = unpackInt(bytebuffer);
        InventoryBlock_Field.SaleType = unpackByte(bytebuffer) & 0xff;
        InventoryBlock_Field.SalePrice = unpackInt(bytebuffer);
        InventoryBlock_Field.Name = unpackVariable(bytebuffer, 1);
        InventoryBlock_Field.Description = unpackVariable(bytebuffer, 1);
        InventoryBlock_Field.CreationDate = unpackInt(bytebuffer);
        InventoryBlock_Field.CRC = unpackInt(bytebuffer);
    }
}
