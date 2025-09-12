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

public class DirPopularReply extends SLMessage
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

        public float Dwell;
        public byte Name[];
        public UUID ParcelID;

        public QueryReplies()
        {
        }
    }


    public AgentData AgentData_Field;
    public QueryData QueryData_Field;
    public ArrayList QueryReplies_Fields;

    public DirPopularReply()
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
        for (i = 37; iterator.hasNext(); i = ((QueryReplies)iterator.next()).Name.length + 17 + 4 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleDirPopularReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)53);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, QueryData_Field.QueryID);
        bytebuffer.put((byte)QueryReplies_Fields.size());
        QueryReplies queryreplies;
        for (Iterator iterator = QueryReplies_Fields.iterator(); iterator.hasNext(); packFloat(bytebuffer, queryreplies.Dwell))
        {
            queryreplies = (QueryReplies)iterator.next();
            packUUID(bytebuffer, queryreplies.ParcelID);
            packVariable(bytebuffer, queryreplies.Name, 1);
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
            queryreplies.Dwell = unpackFloat(bytebuffer);
            QueryReplies_Fields.add(queryreplies);
        }

    }
}
