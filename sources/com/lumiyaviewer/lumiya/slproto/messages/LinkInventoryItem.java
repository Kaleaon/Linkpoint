// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class LinkInventoryItem extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class InventoryBlock
    {

        public int CallbackID;
        public byte Description[];
        public UUID FolderID;
        public int InvType;
        public byte Name[];
        public UUID OldItemID;
        public UUID TransactionID;
        public int Type;

        public InventoryBlock()
        {
        }
    }


    public AgentData AgentData_Field;
    public InventoryBlock InventoryBlock_Field;

    public LinkInventoryItem()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        InventoryBlock_Field = new InventoryBlock();
    }

    public int CalcPayloadSize()
    {
        return InventoryBlock_Field.Name.length + 55 + 1 + InventoryBlock_Field.Description.length + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleLinkInventoryItem(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-86);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, InventoryBlock_Field.CallbackID);
        packUUID(bytebuffer, InventoryBlock_Field.FolderID);
        packUUID(bytebuffer, InventoryBlock_Field.TransactionID);
        packUUID(bytebuffer, InventoryBlock_Field.OldItemID);
        packByte(bytebuffer, (byte)InventoryBlock_Field.Type);
        packByte(bytebuffer, (byte)InventoryBlock_Field.InvType);
        packVariable(bytebuffer, InventoryBlock_Field.Name, 1);
        packVariable(bytebuffer, InventoryBlock_Field.Description, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        InventoryBlock_Field.CallbackID = unpackInt(bytebuffer);
        InventoryBlock_Field.FolderID = unpackUUID(bytebuffer);
        InventoryBlock_Field.TransactionID = unpackUUID(bytebuffer);
        InventoryBlock_Field.OldItemID = unpackUUID(bytebuffer);
        InventoryBlock_Field.Type = unpackByte(bytebuffer);
        InventoryBlock_Field.InvType = unpackByte(bytebuffer);
        InventoryBlock_Field.Name = unpackVariable(bytebuffer, 1);
        InventoryBlock_Field.Description = unpackVariable(bytebuffer, 1);
    }
}
