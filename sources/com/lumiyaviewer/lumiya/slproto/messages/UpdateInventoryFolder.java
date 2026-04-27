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

public class UpdateInventoryFolder extends SLMessage
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
        public byte Name[];
        public UUID ParentID;
        public int Type;

        public FolderData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList FolderData_Fields;

    public UpdateInventoryFolder()
    {
        FolderData_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = FolderData_Fields.iterator();
        int i;
        for (i = 37; iterator.hasNext(); i = ((FolderData)iterator.next()).Name.length + 34 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleUpdateInventoryFolder(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)18);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        bytebuffer.put((byte)FolderData_Fields.size());
        FolderData folderdata;
        for (Iterator iterator = FolderData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, folderdata.Name, 1))
        {
            folderdata = (FolderData)iterator.next();
            packUUID(bytebuffer, folderdata.FolderID);
            packUUID(bytebuffer, folderdata.ParentID);
            packByte(bytebuffer, (byte)folderdata.Type);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            FolderData folderdata = new FolderData();
            folderdata.FolderID = unpackUUID(bytebuffer);
            folderdata.ParentID = unpackUUID(bytebuffer);
            folderdata.Type = unpackByte(bytebuffer);
            folderdata.Name = unpackVariable(bytebuffer, 1);
            FolderData_Fields.add(folderdata);
        }

    }
}
