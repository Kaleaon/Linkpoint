// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class MapItemRequest extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public int EstateID;
        public int Flags;
        public boolean Godlike;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class RequestData
    {

        public int ItemType;
        public long RegionHandle;

        public RequestData()
        {
        }
    }


    public AgentData AgentData_Field;
    public RequestData RequestData_Field;

    public MapItemRequest()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        RequestData_Field = new RequestData();
    }

    public int CalcPayloadSize()
    {
        return 57;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleMapItemRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-102);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, AgentData_Field.Flags);
        packInt(bytebuffer, AgentData_Field.EstateID);
        packBoolean(bytebuffer, AgentData_Field.Godlike);
        packInt(bytebuffer, RequestData_Field.ItemType);
        packLong(bytebuffer, RequestData_Field.RegionHandle);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.Flags = unpackInt(bytebuffer);
        AgentData_Field.EstateID = unpackInt(bytebuffer);
        AgentData_Field.Godlike = unpackBoolean(bytebuffer);
        RequestData_Field.ItemType = unpackInt(bytebuffer);
        RequestData_Field.RegionHandle = unpackLong(bytebuffer);
    }
}
