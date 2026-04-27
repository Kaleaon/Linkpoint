// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class EventGodDelete extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class EventData
    {

        public int EventID;

        public EventData()
        {
        }
    }

    public static class QueryData
    {

        public int QueryFlags;
        public UUID QueryID;
        public int QueryStart;
        public byte QueryText[];

        public QueryData()
        {
        }
    }


    public AgentData AgentData_Field;
    public EventData EventData_Field;
    public QueryData QueryData_Field;

    public EventGodDelete()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        EventData_Field = new EventData();
        QueryData_Field = new QueryData();
    }

    public int CalcPayloadSize()
    {
        return QueryData_Field.QueryText.length + 17 + 4 + 4 + 40;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleEventGodDelete(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-73);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, EventData_Field.EventID);
        packUUID(bytebuffer, QueryData_Field.QueryID);
        packVariable(bytebuffer, QueryData_Field.QueryText, 1);
        packInt(bytebuffer, QueryData_Field.QueryFlags);
        packInt(bytebuffer, QueryData_Field.QueryStart);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        EventData_Field.EventID = unpackInt(bytebuffer);
        QueryData_Field.QueryID = unpackUUID(bytebuffer);
        QueryData_Field.QueryText = unpackVariable(bytebuffer, 1);
        QueryData_Field.QueryFlags = unpackInt(bytebuffer);
        QueryData_Field.QueryStart = unpackInt(bytebuffer);
    }
}
