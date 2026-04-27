// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class LiveHelpGroupRequest extends SLMessage
{
    public static class RequestData
    {

        public UUID AgentID;
        public UUID RequestID;

        public RequestData()
        {
        }
    }


    public RequestData RequestData_Field;

    public LiveHelpGroupRequest()
    {
        zeroCoded = false;
        RequestData_Field = new RequestData();
    }

    public int CalcPayloadSize()
    {
        return 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleLiveHelpGroupRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)123);
        packUUID(bytebuffer, RequestData_Field.RequestID);
        packUUID(bytebuffer, RequestData_Field.AgentID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        RequestData_Field.RequestID = unpackUUID(bytebuffer);
        RequestData_Field.AgentID = unpackUUID(bytebuffer);
    }
}
