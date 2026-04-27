// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class NearestLandingRegionReply extends SLMessage
{
    public static class LandingRegionData
    {

        public long RegionHandle;

        public LandingRegionData()
        {
        }
    }


    public LandingRegionData LandingRegionData_Field;

    public NearestLandingRegionReply()
    {
        zeroCoded = false;
        LandingRegionData_Field = new LandingRegionData();
    }

    public int CalcPayloadSize()
    {
        return 12;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleNearestLandingRegionReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-111);
        packLong(bytebuffer, LandingRegionData_Field.RegionHandle);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        LandingRegionData_Field.RegionHandle = unpackLong(bytebuffer);
    }
}
