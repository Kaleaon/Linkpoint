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

public class DirPlacesReply extends SLMessage
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

        public boolean Auction;
        public float Dwell;
        public boolean ForSale;
        public byte Name[];
        public UUID ParcelID;

        public QueryReplies()
        {
        }
    }

    public static class StatusData
    {

        public int Status;

        public StatusData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList QueryData_Fields;
    public ArrayList QueryReplies_Fields;
    public ArrayList StatusData_Fields;

    public DirPlacesReply()
    {
        QueryData_Fields = new ArrayList();
        QueryReplies_Fields = new ArrayList();
        StatusData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        int i = QueryData_Fields.size();
        Iterator iterator = QueryReplies_Fields.iterator();
        for (i = i * 16 + 21 + 1; iterator.hasNext(); i = ((QueryReplies)iterator.next()).Name.length + 17 + 1 + 1 + 4 + i) { }
        return i + 1 + StatusData_Fields.size() * 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleDirPlacesReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)35);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        bytebuffer.put((byte)QueryData_Fields.size());
        for (Iterator iterator = QueryData_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, ((QueryData)iterator.next()).QueryID)) { }
        bytebuffer.put((byte)QueryReplies_Fields.size());
        QueryReplies queryreplies;
        for (Iterator iterator1 = QueryReplies_Fields.iterator(); iterator1.hasNext(); packFloat(bytebuffer, queryreplies.Dwell))
        {
            queryreplies = (QueryReplies)iterator1.next();
            packUUID(bytebuffer, queryreplies.ParcelID);
            packVariable(bytebuffer, queryreplies.Name, 1);
            packBoolean(bytebuffer, queryreplies.ForSale);
            packBoolean(bytebuffer, queryreplies.Auction);
        }

        bytebuffer.put((byte)StatusData_Fields.size());
        for (Iterator iterator2 = StatusData_Fields.iterator(); iterator2.hasNext(); packInt(bytebuffer, ((StatusData)iterator2.next()).Status)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            QueryData querydata = new QueryData();
            querydata.QueryID = unpackUUID(bytebuffer);
            QueryData_Fields.add(querydata);
        }

        byte0 = bytebuffer.get();
        for (int j = 0; j < (byte0 & 0xff); j++)
        {
            QueryReplies queryreplies = new QueryReplies();
            queryreplies.ParcelID = unpackUUID(bytebuffer);
            queryreplies.Name = unpackVariable(bytebuffer, 1);
            queryreplies.ForSale = unpackBoolean(bytebuffer);
            queryreplies.Auction = unpackBoolean(bytebuffer);
            queryreplies.Dwell = unpackFloat(bytebuffer);
            QueryReplies_Fields.add(queryreplies);
        }

        byte0 = bytebuffer.get();
        for (int k = ((flag) ? 1 : 0); k < (byte0 & 0xff); k++)
        {
            StatusData statusdata = new StatusData();
            statusdata.Status = unpackInt(bytebuffer);
            StatusData_Fields.add(statusdata);
        }

    }
}
