// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class EventLocationRequest extends SLMessage
{
    public static class EventData
    {

        public int EventID;

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

    public EventLocationRequest()
    {
        zeroCoded = true;
        QueryData_Field = new QueryData();
        EventData_Field = new EventData();
    }

    public int CalcPayloadSize()
    {
        return 24;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleEventLocationRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)51);
        packUUID(bytebuffer, QueryData_Field.QueryID);
        packInt(bytebuffer, EventData_Field.EventID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        QueryData_Field.QueryID = unpackUUID(bytebuffer);
        EventData_Field.EventID = unpackInt(bytebuffer);
    }
}
