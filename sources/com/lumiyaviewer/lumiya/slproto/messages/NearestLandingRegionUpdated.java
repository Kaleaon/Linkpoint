// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class NearestLandingRegionUpdated extends SLMessage
{
    public static class RegionData
    {

        public long RegionHandle;

        public RegionData()
        {
        }
    }


    public RegionData RegionData_Field;

    public NearestLandingRegionUpdated()
    {
        zeroCoded = false;
        RegionData_Field = new RegionData();
    }

    public int CalcPayloadSize()
    {
        return 12;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleNearestLandingRegionUpdated(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-110);
        packLong(bytebuffer, RegionData_Field.RegionHandle);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        RegionData_Field.RegionHandle = unpackLong(bytebuffer);
    }
}
