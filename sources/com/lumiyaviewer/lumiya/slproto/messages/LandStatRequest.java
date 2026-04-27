// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class LandStatRequest extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class RequestData
    {

        public byte Filter[];
        public int ParcelLocalID;
        public int ReportType;
        public int RequestFlags;

        public RequestData()
        {
        }
    }


    public AgentData AgentData_Field;
    public RequestData RequestData_Field;

    public LandStatRequest()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        RequestData_Field = new RequestData();
    }

    public int CalcPayloadSize()
    {
        return RequestData_Field.Filter.length + 9 + 4 + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleLandStatRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-91);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, RequestData_Field.ReportType);
        packInt(bytebuffer, RequestData_Field.RequestFlags);
        packVariable(bytebuffer, RequestData_Field.Filter, 1);
        packInt(bytebuffer, RequestData_Field.ParcelLocalID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        RequestData_Field.ReportType = unpackInt(bytebuffer);
        RequestData_Field.RequestFlags = unpackInt(bytebuffer);
        RequestData_Field.Filter = unpackVariable(bytebuffer, 1);
        RequestData_Field.ParcelLocalID = unpackInt(bytebuffer);
    }
}
