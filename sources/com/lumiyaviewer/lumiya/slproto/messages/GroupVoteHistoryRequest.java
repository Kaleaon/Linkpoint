// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class GroupVoteHistoryRequest extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class GroupData
    {

        public UUID GroupID;

        public GroupData()
        {
        }
    }

    public static class TransactionData
    {

        public UUID TransactionID;

        public TransactionData()
        {
        }
    }


    public AgentData AgentData_Field;
    public GroupData GroupData_Field;
    public TransactionData TransactionData_Field;

    public GroupVoteHistoryRequest()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        GroupData_Field = new GroupData();
        TransactionData_Field = new TransactionData();
    }

    public int CalcPayloadSize()
    {
        return 68;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGroupVoteHistoryRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)105);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, GroupData_Field.GroupID);
        packUUID(bytebuffer, TransactionData_Field.TransactionID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        GroupData_Field.GroupID = unpackUUID(bytebuffer);
        TransactionData_Field.TransactionID = unpackUUID(bytebuffer);
    }
}
