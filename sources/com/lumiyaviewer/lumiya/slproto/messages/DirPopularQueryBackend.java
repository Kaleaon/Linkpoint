// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class DirPopularQueryBackend extends SLMessage
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

        public int EstateID;
        public boolean Godlike;
        public int QueryFlags;
        public UUID QueryID;

        public QueryData()
        {
        }
    }


    public AgentData AgentData_Field;
    public QueryData QueryData_Field;

    public DirPopularQueryBackend()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        QueryData_Field = new QueryData();
    }

    public int CalcPayloadSize()
    {
        return 45;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleDirPopularQueryBackend(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)52);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, QueryData_Field.QueryID);
        packInt(bytebuffer, QueryData_Field.QueryFlags);
        packInt(bytebuffer, QueryData_Field.EstateID);
        packBoolean(bytebuffer, QueryData_Field.Godlike);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        QueryData_Field.QueryID = unpackUUID(bytebuffer);
        QueryData_Field.QueryFlags = unpackInt(bytebuffer);
        QueryData_Field.EstateID = unpackInt(bytebuffer);
        QueryData_Field.Godlike = unpackBoolean(bytebuffer);
    }
}
