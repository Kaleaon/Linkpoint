// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RegionPresenceRequestByRegionID extends SLMessage
{
    public static class RegionData
    {

        public UUID RegionID;

        public RegionData()
        {
        }
    }


    public ArrayList RegionData_Fields;

    public RegionPresenceRequestByRegionID()
    {
        RegionData_Fields = new ArrayList();
        zeroCoded = false;
    }

    public int CalcPayloadSize()
    {
        return RegionData_Fields.size() * 16 + 5;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRegionPresenceRequestByRegionID(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)14);
        bytebuffer.put((byte)RegionData_Fields.size());
        for (Iterator iterator = RegionData_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, ((RegionData)iterator.next()).RegionID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            RegionData regiondata = new RegionData();
            regiondata.RegionID = unpackUUID(bytebuffer);
            RegionData_Fields.add(regiondata);
        }

    }
}
