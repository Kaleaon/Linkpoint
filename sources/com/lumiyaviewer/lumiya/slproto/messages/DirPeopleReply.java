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

public class DirPeopleReply extends SLMessage
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

        public UUID AgentID;
        public byte FirstName[];
        public byte Group[];
        public byte LastName[];
        public boolean Online;
        public int Reputation;

        public QueryReplies()
        {
        }
    }


    public AgentData AgentData_Field;
    public QueryData QueryData_Field;
    public ArrayList QueryReplies_Fields;

    public DirPeopleReply()
    {
        QueryReplies_Fields = new ArrayList();
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
        int k;
        for (i = 37; iterator.hasNext(); i = queryreplies.Group.length + (j + 17 + 1 + k + 1) + 1 + 4 + i)
        {
            queryreplies = (QueryReplies)iterator.next();
            j = queryreplies.FirstName.length;
            k = queryreplies.LastName.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleDirPeopleReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)36);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, QueryData_Field.QueryID);
        bytebuffer.put((byte)QueryReplies_Fields.size());
        QueryReplies queryreplies;
        for (Iterator iterator = QueryReplies_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, queryreplies.Reputation))
        {
            queryreplies = (QueryReplies)iterator.next();
            packUUID(bytebuffer, queryreplies.AgentID);
            packVariable(bytebuffer, queryreplies.FirstName, 1);
            packVariable(bytebuffer, queryreplies.LastName, 1);
            packVariable(bytebuffer, queryreplies.Group, 1);
            packBoolean(bytebuffer, queryreplies.Online);
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
            queryreplies.AgentID = unpackUUID(bytebuffer);
            queryreplies.FirstName = unpackVariable(bytebuffer, 1);
            queryreplies.LastName = unpackVariable(bytebuffer, 1);
            queryreplies.Group = unpackVariable(bytebuffer, 1);
            queryreplies.Online = unpackBoolean(bytebuffer);
            queryreplies.Reputation = unpackInt(bytebuffer);
            QueryReplies_Fields.add(queryreplies);
        }

    }
}
