// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class NearestLandingRegionRequest extends SLMessage
{
    public static class RequestingRegionData
    {

        public long RegionHandle;

        public RequestingRegionData()
        {
        }
    }


    public RequestingRegionData RequestingRegionData_Field;

    public NearestLandingRegionRequest()
    {
        zeroCoded = false;
        RequestingRegionData_Field = new RequestingRegionData();
    }

    public int CalcPayloadSize()
    {
        return 12;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleNearestLandingRegionRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-112);
        packLong(bytebuffer, RequestingRegionData_Field.RegionHandle);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        RequestingRegionData_Field.RegionHandle = unpackLong(bytebuffer);
    }
}
