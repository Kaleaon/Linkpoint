// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RemoveInventoryObjects extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class FolderData
    {

        public UUID FolderID;

        public FolderData()
        {
        }
    }

    public static class ItemData
    {

        public UUID ItemID;

        public ItemData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList FolderData_Fields;
    public ArrayList ItemData_Fields;

    public RemoveInventoryObjects()
    {
        FolderData_Fields = new ArrayList();
        ItemData_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return FolderData_Fields.size() * 16 + 37 + 1 + ItemData_Fields.size() * 16;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRemoveInventoryObjects(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)28);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        bytebuffer.put((byte)FolderData_Fields.size());
        for (Iterator iterator = FolderData_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, ((FolderData)iterator.next()).FolderID)) { }
        bytebuffer.put((byte)ItemData_Fields.size());
        for (Iterator iterator1 = ItemData_Fields.iterator(); iterator1.hasNext(); packUUID(bytebuffer, ((ItemData)iterator1.next()).ItemID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            FolderData folderdata = new FolderData();
            folderdata.FolderID = unpackUUID(bytebuffer);
            FolderData_Fields.add(folderdata);
        }

        byte0 = bytebuffer.get();
        for (int j = ((flag) ? 1 : 0); j < (byte0 & 0xff); j++)
        {
            ItemData itemdata = new ItemData();
            itemdata.ItemID = unpackUUID(bytebuffer);
            ItemData_Fields.add(itemdata);
        }

    }
}
