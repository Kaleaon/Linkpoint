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

public class DirGroupsReply extends SLMessage
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

        public UUID GroupID;
        public byte GroupName[];
        public int Members;
        public float SearchOrder;

        public QueryReplies()
        {
        }
    }


    public AgentData AgentData_Field;
    public QueryData QueryData_Field;
    public ArrayList QueryReplies_Fields;

    public DirGroupsReply()
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
        for (i = 37; iterator.hasNext(); i = ((QueryReplies)iterator.next()).GroupName.length + 17 + 4 + 4 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleDirGroupsReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)38);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, QueryData_Field.QueryID);
        bytebuffer.put((byte)QueryReplies_Fields.size());
        QueryReplies queryreplies;
        for (Iterator iterator = QueryReplies_Fields.iterator(); iterator.hasNext(); packFloat(bytebuffer, queryreplies.SearchOrder))
        {
            queryreplies = (QueryReplies)iterator.next();
            packUUID(bytebuffer, queryreplies.GroupID);
            packVariable(bytebuffer, queryreplies.GroupName, 1);
            packInt(bytebuffer, queryreplies.Members);
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
            queryreplies.GroupID = unpackUUID(bytebuffer);
            queryreplies.GroupName = unpackVariable(bytebuffer, 1);
            queryreplies.Members = unpackInt(bytebuffer);
            queryreplies.SearchOrder = unpackFloat(bytebuffer);
            QueryReplies_Fields.add(queryreplies);
        }

    }
}
