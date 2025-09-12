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

public class AcceptCallingCard extends SLMessage
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

    public static class TransactionBlock
    {

        public UUID TransactionID;

        public TransactionBlock()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList FolderData_Fields;
    public TransactionBlock TransactionBlock_Field;

    public AcceptCallingCard()
    {
        FolderData_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
        TransactionBlock_Field = new TransactionBlock();
    }

    public int CalcPayloadSize()
    {
        return FolderData_Fields.size() * 16 + 53;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAcceptCallingCard(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)46);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, TransactionBlock_Field.TransactionID);
        bytebuffer.put((byte)FolderData_Fields.size());
        for (Iterator iterator = FolderData_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, ((FolderData)iterator.next()).FolderID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        TransactionBlock_Field.TransactionID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            FolderData folderdata = new FolderData();
            folderdata.FolderID = unpackUUID(bytebuffer);
            FolderData_Fields.add(folderdata);
        }

    }
}
