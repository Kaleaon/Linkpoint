// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class UpdateAttachment extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class AttachmentBlock
    {

        public int AttachmentPoint;

        public AttachmentBlock()
        {
        }
    }

    public static class InventoryData
    {

        public UUID AssetID;
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
        public int Type;

        public InventoryData()
        {
        }
    }

    public static class OperationData
    {

        public boolean AddItem;
        public boolean UseExistingAsset;

        public OperationData()
        {
        }
    }


    public AgentData AgentData_Field;
    public AttachmentBlock AttachmentBlock_Field;
    public InventoryData InventoryData_Field;
    public OperationData OperationData_Field;

    public UpdateAttachment()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        AttachmentBlock_Field = new AttachmentBlock();
        OperationData_Field = new OperationData();
        InventoryData_Field = new InventoryData();
    }

    public int CalcPayloadSize()
    {
        return InventoryData_Field.Name.length + 129 + 1 + InventoryData_Field.Description.length + 4 + 4 + 39;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleUpdateAttachment(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)75);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packByte(bytebuffer, (byte)AttachmentBlock_Field.AttachmentPoint);
        packBoolean(bytebuffer, OperationData_Field.AddItem);
        packBoolean(bytebuffer, OperationData_Field.UseExistingAsset);
        packUUID(bytebuffer, InventoryData_Field.ItemID);
        packUUID(bytebuffer, InventoryData_Field.FolderID);
        packUUID(bytebuffer, InventoryData_Field.CreatorID);
        packUUID(bytebuffer, InventoryData_Field.OwnerID);
        packUUID(bytebuffer, InventoryData_Field.GroupID);
        packInt(bytebuffer, InventoryData_Field.BaseMask);
        packInt(bytebuffer, InventoryData_Field.OwnerMask);
        packInt(bytebuffer, InventoryData_Field.GroupMask);
        packInt(bytebuffer, InventoryData_Field.EveryoneMask);
        packInt(bytebuffer, InventoryData_Field.NextOwnerMask);
        packBoolean(bytebuffer, InventoryData_Field.GroupOwned);
        packUUID(bytebuffer, InventoryData_Field.AssetID);
        packByte(bytebuffer, (byte)InventoryData_Field.Type);
        packByte(bytebuffer, (byte)InventoryData_Field.InvType);
        packInt(bytebuffer, InventoryData_Field.Flags);
        packByte(bytebuffer, (byte)InventoryData_Field.SaleType);
        packInt(bytebuffer, InventoryData_Field.SalePrice);
        packVariable(bytebuffer, InventoryData_Field.Name, 1);
        packVariable(bytebuffer, InventoryData_Field.Description, 1);
        packInt(bytebuffer, InventoryData_Field.CreationDate);
        packInt(bytebuffer, InventoryData_Field.CRC);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AttachmentBlock_Field.AttachmentPoint = unpackByte(bytebuffer) & 0xff;
        OperationData_Field.AddItem = unpackBoolean(bytebuffer);
        OperationData_Field.UseExistingAsset = unpackBoolean(bytebuffer);
        InventoryData_Field.ItemID = unpackUUID(bytebuffer);
        InventoryData_Field.FolderID = unpackUUID(bytebuffer);
        InventoryData_Field.CreatorID = unpackUUID(bytebuffer);
        InventoryData_Field.OwnerID = unpackUUID(bytebuffer);
        InventoryData_Field.GroupID = unpackUUID(bytebuffer);
        InventoryData_Field.BaseMask = unpackInt(bytebuffer);
        InventoryData_Field.OwnerMask = unpackInt(bytebuffer);
        InventoryData_Field.GroupMask = unpackInt(bytebuffer);
        InventoryData_Field.EveryoneMask = unpackInt(bytebuffer);
        InventoryData_Field.NextOwnerMask = unpackInt(bytebuffer);
        InventoryData_Field.GroupOwned = unpackBoolean(bytebuffer);
        InventoryData_Field.AssetID = unpackUUID(bytebuffer);
        InventoryData_Field.Type = unpackByte(bytebuffer);
        InventoryData_Field.InvType = unpackByte(bytebuffer);
        InventoryData_Field.Flags = unpackInt(bytebuffer);
        InventoryData_Field.SaleType = unpackByte(bytebuffer) & 0xff;
        InventoryData_Field.SalePrice = unpackInt(bytebuffer);
        InventoryData_Field.Name = unpackVariable(bytebuffer, 1);
        InventoryData_Field.Description = unpackVariable(bytebuffer, 1);
        InventoryData_Field.CreationDate = unpackInt(bytebuffer);
        InventoryData_Field.CRC = unpackInt(bytebuffer);
    }
}
