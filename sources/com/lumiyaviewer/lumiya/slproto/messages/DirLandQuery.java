// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class DirLandQuery extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class QueryData
    {

        public int Area;
        public int Price;
        public int QueryFlags;
        public UUID QueryID;
        public int QueryStart;
        public int SearchType;

        public QueryData()
        {
        }
    }


    public AgentData AgentData_Field;
    public QueryData QueryData_Field;

    public DirLandQuery()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        QueryData_Field = new QueryData();
    }

    public int CalcPayloadSize()
    {
        return 72;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleDirLandQuery(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)48);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, QueryData_Field.QueryID);
        packInt(bytebuffer, QueryData_Field.QueryFlags);
        packInt(bytebuffer, QueryData_Field.SearchType);
        packInt(bytebuffer, QueryData_Field.Price);
        packInt(bytebuffer, QueryData_Field.Area);
        packInt(bytebuffer, QueryData_Field.QueryStart);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        QueryData_Field.QueryID = unpackUUID(bytebuffer);
        QueryData_Field.QueryFlags = unpackInt(bytebuffer);
        QueryData_Field.SearchType = unpackInt(bytebuffer);
        QueryData_Field.Price = unpackInt(bytebuffer);
        QueryData_Field.Area = unpackInt(bytebuffer);
        QueryData_Field.QueryStart = unpackInt(bytebuffer);
    }
}
