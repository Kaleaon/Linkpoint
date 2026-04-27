// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class EventLocationReply extends SLMessage
{
    public static class EventData
    {

        public UUID RegionID;
        public LLVector3 RegionPos;
        public boolean Success;

        public EventData()
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


    public EventData EventData_Field;
    public QueryData QueryData_Field;

    public EventLocationReply()
    {
        zeroCoded = true;
        QueryData_Field = new QueryData();
        EventData_Field = new EventData();
    }

    public int CalcPayloadSize()
    {
        return 49;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleEventLocationReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)52);
        packUUID(bytebuffer, QueryData_Field.QueryID);
        packBoolean(bytebuffer, EventData_Field.Success);
        packUUID(bytebuffer, EventData_Field.RegionID);
        packLLVector3(bytebuffer, EventData_Field.RegionPos);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        QueryData_Field.QueryID = unpackUUID(bytebuffer);
        EventData_Field.Success = unpackBoolean(bytebuffer);
        EventData_Field.RegionID = unpackUUID(bytebuffer);
        EventData_Field.RegionPos = unpackLLVector3(bytebuffer);
    }
}
