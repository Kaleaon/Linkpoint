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

public class PlacesReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID QueryID;

        public AgentData()
        {
        }
    }

    public static class QueryData
    {

        public int ActualArea;
        public int BillableArea;
        public byte Desc[];
        public float Dwell;
        public int Flags;
        public float GlobalX;
        public float GlobalY;
        public float GlobalZ;
        public byte Name[];
        public UUID OwnerID;
        public int Price;
        public byte SimName[];
        public UUID SnapshotID;

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
    public ArrayList QueryData_Fields;
    public TransactionData TransactionData_Field;

    public PlacesReply()
    {
        QueryData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        TransactionData_Field = new TransactionData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = QueryData_Fields.iterator();
        QueryData querydata;
        int i;
        int j;
        int k;
        for (i = 53; iterator.hasNext(); i = querydata.SimName.length + (j + 17 + 1 + k + 4 + 4 + 1 + 4 + 4 + 4 + 1) + 16 + 4 + 4 + i)
        {
            querydata = (QueryData)iterator.next();
            j = querydata.Name.length;
            k = querydata.Desc.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandlePlacesReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)30);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.QueryID);
        packUUID(bytebuffer, TransactionData_Field.TransactionID);
        bytebuffer.put((byte)QueryData_Fields.size());
        QueryData querydata;
        for (Iterator iterator = QueryData_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, querydata.Price))
        {
            querydata = (QueryData)iterator.next();
            packUUID(bytebuffer, querydata.OwnerID);
            packVariable(bytebuffer, querydata.Name, 1);
            packVariable(bytebuffer, querydata.Desc, 1);
            packInt(bytebuffer, querydata.ActualArea);
            packInt(bytebuffer, querydata.BillableArea);
            packByte(bytebuffer, (byte)querydata.Flags);
            packFloat(bytebuffer, querydata.GlobalX);
            packFloat(bytebuffer, querydata.GlobalY);
            packFloat(bytebuffer, querydata.GlobalZ);
            packVariable(bytebuffer, querydata.SimName, 1);
            packUUID(bytebuffer, querydata.SnapshotID);
            packFloat(bytebuffer, querydata.Dwell);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.QueryID = unpackUUID(bytebuffer);
        TransactionData_Field.TransactionID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            QueryData querydata = new QueryData();
            querydata.OwnerID = unpackUUID(bytebuffer);
            querydata.Name = unpackVariable(bytebuffer, 1);
            querydata.Desc = unpackVariable(bytebuffer, 1);
            querydata.ActualArea = unpackInt(bytebuffer);
            querydata.BillableArea = unpackInt(bytebuffer);
            querydata.Flags = unpackByte(bytebuffer) & 0xff;
            querydata.GlobalX = unpackFloat(bytebuffer);
            querydata.GlobalY = unpackFloat(bytebuffer);
            querydata.GlobalZ = unpackFloat(bytebuffer);
            querydata.SimName = unpackVariable(bytebuffer, 1);
            querydata.SnapshotID = unpackUUID(bytebuffer);
            querydata.Dwell = unpackFloat(bytebuffer);
            querydata.Price = unpackInt(bytebuffer);
            QueryData_Fields.add(querydata);
        }

    }
}
