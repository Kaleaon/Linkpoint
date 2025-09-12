// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class CreateInventoryFolder extends SLMessage
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
    public FolderData FolderData_Field;

    public CreateInventoryFolder()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        FolderData_Field = new FolderData();
    }

    public int CalcPayloadSize()
    {
        return FolderData_Field.Name.length + 34 + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleCreateInventoryFolder(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)17);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, FolderData_Field.FolderID);
        packUUID(bytebuffer, FolderData_Field.ParentID);
        packByte(bytebuffer, (byte)FolderData_Field.Type);
        packVariable(bytebuffer, FolderData_Field.Name, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        FolderData_Field.FolderID = unpackUUID(bytebuffer);
        FolderData_Field.ParentID = unpackUUID(bytebuffer);
        FolderData_Field.Type = unpackByte(bytebuffer);
        FolderData_Field.Name = unpackVariable(bytebuffer, 1);
    }
}
