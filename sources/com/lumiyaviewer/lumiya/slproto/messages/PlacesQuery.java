// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class PlacesQuery extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID QueryID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class QueryData
    {

        public int Category;
        public int QueryFlags;
        public byte QueryText[];
        public byte SimName[];

        public QueryData()
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
    public QueryData QueryData_Field;
    public TransactionData TransactionData_Field;

    public PlacesQuery()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        TransactionData_Field = new TransactionData();
        QueryData_Field = new QueryData();
    }

    public int CalcPayloadSize()
    {
        return QueryData_Field.QueryText.length + 1 + 4 + 1 + 1 + QueryData_Field.SimName.length + 68;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandlePlacesQuery(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)29);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, AgentData_Field.QueryID);
        packUUID(bytebuffer, TransactionData_Field.TransactionID);
        packVariable(bytebuffer, QueryData_Field.QueryText, 1);
        packInt(bytebuffer, QueryData_Field.QueryFlags);
        packByte(bytebuffer, (byte)QueryData_Field.Category);
        packVariable(bytebuffer, QueryData_Field.SimName, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.QueryID = unpackUUID(bytebuffer);
        TransactionData_Field.TransactionID = unpackUUID(bytebuffer);
        QueryData_Field.QueryText = unpackVariable(bytebuffer, 1);
        QueryData_Field.QueryFlags = unpackInt(bytebuffer);
        QueryData_Field.Category = unpackByte(bytebuffer);
        QueryData_Field.SimName = unpackVariable(bytebuffer, 1);
    }
}
