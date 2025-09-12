// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class DirPlacesQueryBackend extends SLMessage
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

        public int Category;
        public int EstateID;
        public boolean Godlike;
        public int QueryFlags;
        public UUID QueryID;
        public int QueryStart;
        public byte QueryText[];
        public byte SimName[];

        public QueryData()
        {
        }
    }


    public AgentData AgentData_Field;
    public QueryData QueryData_Field;

    public DirPlacesQueryBackend()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        QueryData_Field = new QueryData();
    }

    public int CalcPayloadSize()
    {
        return QueryData_Field.QueryText.length + 17 + 4 + 1 + 1 + QueryData_Field.SimName.length + 4 + 1 + 4 + 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleDirPlacesQueryBackend(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)34);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, QueryData_Field.QueryID);
        packVariable(bytebuffer, QueryData_Field.QueryText, 1);
        packInt(bytebuffer, QueryData_Field.QueryFlags);
        packByte(bytebuffer, (byte)QueryData_Field.Category);
        packVariable(bytebuffer, QueryData_Field.SimName, 1);
        packInt(bytebuffer, QueryData_Field.EstateID);
        packBoolean(bytebuffer, QueryData_Field.Godlike);
        packInt(bytebuffer, QueryData_Field.QueryStart);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        QueryData_Field.QueryID = unpackUUID(bytebuffer);
        QueryData_Field.QueryText = unpackVariable(bytebuffer, 1);
        QueryData_Field.QueryFlags = unpackInt(bytebuffer);
        QueryData_Field.Category = unpackByte(bytebuffer);
        QueryData_Field.SimName = unpackVariable(bytebuffer, 1);
        QueryData_Field.EstateID = unpackInt(bytebuffer);
        QueryData_Field.Godlike = unpackBoolean(bytebuffer);
        QueryData_Field.QueryStart = unpackInt(bytebuffer);
    }
}
