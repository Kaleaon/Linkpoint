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

public class CreateNewOutfitAttachments extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class HeaderData
    {

        public UUID NewFolderID;

        public HeaderData()
        {
        }
    }

    public static class ObjectData
    {

        public UUID OldFolderID;
        public UUID OldItemID;

        public ObjectData()
        {
        }
    }


    public AgentData AgentData_Field;
    public HeaderData HeaderData_Field;
    public ArrayList ObjectData_Fields;

    public CreateNewOutfitAttachments()
    {
        ObjectData_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
        HeaderData_Field = new HeaderData();
    }

    public int CalcPayloadSize()
    {
        return ObjectData_Fields.size() * 32 + 53;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleCreateNewOutfitAttachments(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-114);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, HeaderData_Field.NewFolderID);
        bytebuffer.put((byte)ObjectData_Fields.size());
        ObjectData objectdata;
        for (Iterator iterator = ObjectData_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, objectdata.OldFolderID))
        {
            objectdata = (ObjectData)iterator.next();
            packUUID(bytebuffer, objectdata.OldItemID);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        HeaderData_Field.NewFolderID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ObjectData objectdata = new ObjectData();
            objectdata.OldItemID = unpackUUID(bytebuffer);
            objectdata.OldFolderID = unpackUUID(bytebuffer);
            ObjectData_Fields.add(objectdata);
        }

    }
}
