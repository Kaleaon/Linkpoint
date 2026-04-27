// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class CheckParcelAuctions extends SLMessage
{
    public static class RegionData
    {

        public long RegionHandle;

        public RegionData()
        {
        }
    }


    public ArrayList RegionData_Fields;

    public CheckParcelAuctions()
    {
        RegionData_Fields = new ArrayList();
        zeroCoded = false;
    }

    public int CalcPayloadSize()
    {
        return RegionData_Fields.size() * 8 + 5;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleCheckParcelAuctions(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-23);
        bytebuffer.put((byte)RegionData_Fields.size());
        for (Iterator iterator = RegionData_Fields.iterator(); iterator.hasNext(); packLong(bytebuffer, ((RegionData)iterator.next()).RegionHandle)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            RegionData regiondata = new RegionData();
            regiondata.RegionHandle = unpackLong(bytebuffer);
            RegionData_Fields.add(regiondata);
        }

    }
}
