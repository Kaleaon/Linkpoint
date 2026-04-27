// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class FetchInventoryDescendents extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class InventoryData
    {

        public boolean FetchFolders;
        public boolean FetchItems;
        public UUID FolderID;
        public UUID OwnerID;
        public int SortOrder;

        public InventoryData()
        {
        }
    }


    public AgentData AgentData_Field;
    public InventoryData InventoryData_Field;

    public FetchInventoryDescendents()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        InventoryData_Field = new InventoryData();
    }

    public int CalcPayloadSize()
    {
        return 74;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleFetchInventoryDescendents(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)21);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, InventoryData_Field.FolderID);
        packUUID(bytebuffer, InventoryData_Field.OwnerID);
        packInt(bytebuffer, InventoryData_Field.SortOrder);
        packBoolean(bytebuffer, InventoryData_Field.FetchFolders);
        packBoolean(bytebuffer, InventoryData_Field.FetchItems);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        InventoryData_Field.FolderID = unpackUUID(bytebuffer);
        InventoryData_Field.OwnerID = unpackUUID(bytebuffer);
        InventoryData_Field.SortOrder = unpackInt(bytebuffer);
        InventoryData_Field.FetchFolders = unpackBoolean(bytebuffer);
        InventoryData_Field.FetchItems = unpackBoolean(bytebuffer);
    }
}
