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

public class DirEventsReply extends SLMessage
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

        public byte Date[];
        public int EventFlags;
        public int EventID;
        public byte Name[];
        public UUID OwnerID;
        public int UnixTime;

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
    public QueryData QueryData_Field;
    public ArrayList QueryReplies_Fields;
    public ArrayList StatusData_Fields;

    public DirEventsReply()
    {
        QueryReplies_Fields = new ArrayList();
        StatusData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        QueryData_Field = new QueryData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = QueryReplies_Fields.iterator();
        QueryReplies queryreplies;
        int i;
        int j;
        for (i = 37; iterator.hasNext(); i = queryreplies.Date.length + (j + 17 + 4 + 1) + 4 + 4 + i)
        {
            queryreplies = (QueryReplies)iterator.next();
            j = queryreplies.Name.length;
        }

        return i + 1 + StatusData_Fields.size() * 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleDirEventsReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)37);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, QueryData_Field.QueryID);
        bytebuffer.put((byte)QueryReplies_Fields.size());
        QueryReplies queryreplies;
        for (Iterator iterator = QueryReplies_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, queryreplies.EventFlags))
        {
            queryreplies = (QueryReplies)iterator.next();
            packUUID(bytebuffer, queryreplies.OwnerID);
            packVariable(bytebuffer, queryreplies.Name, 1);
            packInt(bytebuffer, queryreplies.EventID);
            packVariable(bytebuffer, queryreplies.Date, 1);
            packInt(bytebuffer, queryreplies.UnixTime);
        }

        bytebuffer.put((byte)StatusData_Fields.size());
        for (Iterator iterator1 = StatusData_Fields.iterator(); iterator1.hasNext(); packInt(bytebuffer, ((StatusData)iterator1.next()).Status)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        QueryData_Field.QueryID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            QueryReplies queryreplies = new QueryReplies();
            queryreplies.OwnerID = unpackUUID(bytebuffer);
            queryreplies.Name = unpackVariable(bytebuffer, 1);
            queryreplies.EventID = unpackInt(bytebuffer);
            queryreplies.Date = unpackVariable(bytebuffer, 1);
            queryreplies.UnixTime = unpackInt(bytebuffer);
            queryreplies.EventFlags = unpackInt(bytebuffer);
            QueryReplies_Fields.add(queryreplies);
        }

        byte0 = bytebuffer.get();
        for (int j = ((flag) ? 1 : 0); j < (byte0 & 0xff); j++)
        {
            StatusData statusdata = new StatusData();
            statusdata.Status = unpackInt(bytebuffer);
            StatusData_Fields.add(statusdata);
        }

    }
}
