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

public class DirClassifiedReply extends SLMessage
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

        public int ClassifiedFlags;
        public UUID ClassifiedID;
        public int CreationDate;
        public int ExpirationDate;
        public byte Name[];
        public int PriceForListing;

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

    public DirClassifiedReply()
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
        int i;
        for (i = 37; iterator.hasNext(); i = ((QueryReplies)iterator.next()).Name.length + 17 + 1 + 4 + 4 + 4 + i) { }
        return i + 1 + StatusData_Fields.size() * 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleDirClassifiedReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)41);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, QueryData_Field.QueryID);
        bytebuffer.put((byte)QueryReplies_Fields.size());
        QueryReplies queryreplies;
        for (Iterator iterator = QueryReplies_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, queryreplies.PriceForListing))
        {
            queryreplies = (QueryReplies)iterator.next();
            packUUID(bytebuffer, queryreplies.ClassifiedID);
            packVariable(bytebuffer, queryreplies.Name, 1);
            packByte(bytebuffer, (byte)queryreplies.ClassifiedFlags);
            packInt(bytebuffer, queryreplies.CreationDate);
            packInt(bytebuffer, queryreplies.ExpirationDate);
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
            queryreplies.ClassifiedID = unpackUUID(bytebuffer);
            queryreplies.Name = unpackVariable(bytebuffer, 1);
            queryreplies.ClassifiedFlags = unpackByte(bytebuffer) & 0xff;
            queryreplies.CreationDate = unpackInt(bytebuffer);
            queryreplies.ExpirationDate = unpackInt(bytebuffer);
            queryreplies.PriceForListing = unpackInt(bytebuffer);
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
