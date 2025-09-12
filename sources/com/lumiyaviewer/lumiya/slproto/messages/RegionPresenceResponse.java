// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RegionPresenceResponse extends SLMessage
{
    public static class RegionData
    {

        public Inet4Address ExternalRegionIP;
        public Inet4Address InternalRegionIP;
        public byte Message[];
        public long RegionHandle;
        public UUID RegionID;
        public int RegionPort;
        public double ValidUntil;

        public RegionData()
        {
        }
    }


    public ArrayList RegionData_Fields;

    public RegionPresenceResponse()
    {
        RegionData_Fields = new ArrayList();
        zeroCoded = true;
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = RegionData_Fields.iterator();
        int i;
        for (i = 5; iterator.hasNext(); i = ((RegionData)iterator.next()).Message.length + 43 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRegionPresenceResponse(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)16);
        bytebuffer.put((byte)RegionData_Fields.size());
        RegionData regiondata;
        for (Iterator iterator = RegionData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, regiondata.Message, 1))
        {
            regiondata = (RegionData)iterator.next();
            packUUID(bytebuffer, regiondata.RegionID);
            packLong(bytebuffer, regiondata.RegionHandle);
            packIPAddress(bytebuffer, regiondata.InternalRegionIP);
            packIPAddress(bytebuffer, regiondata.ExternalRegionIP);
            packShort(bytebuffer, (short)regiondata.RegionPort);
            packDouble(bytebuffer, regiondata.ValidUntil);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            RegionData regiondata = new RegionData();
            regiondata.RegionID = unpackUUID(bytebuffer);
            regiondata.RegionHandle = unpackLong(bytebuffer);
            regiondata.InternalRegionIP = unpackIPAddress(bytebuffer);
            regiondata.ExternalRegionIP = unpackIPAddress(bytebuffer);
            regiondata.RegionPort = unpackShort(bytebuffer) & 0xffff;
            regiondata.ValidUntil = unpackDouble(bytebuffer);
            regiondata.Message = unpackVariable(bytebuffer, 1);
            RegionData_Fields.add(regiondata);
        }

    }
}
