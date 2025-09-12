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

public class DirLandReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;

        public AgentData()
        {
        }
    }

    public static class QueryData
    {

        public UUID QueryID;

        public QueryData()
        {
        }
    }

    public static class QueryReplies
    {

        public int ActualArea;
        public boolean Auction;
        public boolean ForSale;
        public byte Name[];
        public UUID ParcelID;
        public int SalePrice;

        public QueryReplies()
        {
        }
    }


    public AgentData AgentData_Field;
    public QueryData QueryData_Field;
    public ArrayList QueryReplies_Fields;

    public DirLandReply()
    {
        QueryReplies_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        QueryData_Field = new QueryData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = QueryReplies_Fields.iterator();
        int i;
        for (i = 37; iterator.hasNext(); i = ((QueryReplies)iterator.next()).Name.length + 17 + 1 + 1 + 4 + 4 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleDirLandReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)50);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, QueryData_Field.QueryID);
        bytebuffer.put((byte)QueryReplies_Fields.size());
        QueryReplies queryreplies;
        for (Iterator iterator = QueryReplies_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, queryreplies.ActualArea))
        {
            queryreplies = (QueryReplies)iterator.next();
            packUUID(bytebuffer, queryreplies.ParcelID);
            packVariable(bytebuffer, queryreplies.Name, 1);
            packBoolean(bytebuffer, queryreplies.Auction);
            packBoolean(bytebuffer, queryreplies.ForSale);
            packInt(bytebuffer, queryreplies.SalePrice);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        QueryData_Field.QueryID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            QueryReplies queryreplies = new QueryReplies();
            queryreplies.ParcelID = unpackUUID(bytebuffer);
            queryreplies.Name = unpackVariable(bytebuffer, 1);
            queryreplies.Auction = unpackBoolean(bytebuffer);
            queryreplies.ForSale = unpackBoolean(bytebuffer);
            queryreplies.SalePrice = unpackInt(bytebuffer);
            queryreplies.ActualArea = unpackInt(bytebuffer);
            QueryReplies_Fields.add(queryreplies);
        }

    }
}
