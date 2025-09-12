// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class MoveTaskInventory extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID FolderID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class InventoryData
    {

        public UUID ItemID;
        public int LocalID;

        public InventoryData()
        {
        }
    }


    public AgentData AgentData_Field;
    public InventoryData InventoryData_Field;

    public MoveTaskInventory()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        InventoryData_Field = new InventoryData();
    }

    public int CalcPayloadSize()
    {
        return 72;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleMoveTaskInventory(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)32);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, AgentData_Field.FolderID);
        packInt(bytebuffer, InventoryData_Field.LocalID);
        packUUID(bytebuffer, InventoryData_Field.ItemID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.FolderID = unpackUUID(bytebuffer);
        InventoryData_Field.LocalID = unpackInt(bytebuffer);
        InventoryData_Field.ItemID = unpackUUID(bytebuffer);
    }
}
