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

public class FindAgent extends SLMessage
{
    public static class AgentBlock
    {

        public UUID Hunter;
        public UUID Prey;
        public Inet4Address SpaceIP;

        public AgentBlock()
        {
        }
    }

    public static class LocationBlock
    {

        public double GlobalX;
        public double GlobalY;

        public LocationBlock()
        {
        }
    }


    public AgentBlock AgentBlock_Field;
    public ArrayList LocationBlock_Fields;

    public FindAgent()
    {
        LocationBlock_Fields = new ArrayList();
        zeroCoded = false;
        AgentBlock_Field = new AgentBlock();
    }

    public int CalcPayloadSize()
    {
        return LocationBlock_Fields.size() * 16 + 41;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleFindAgent(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)0);
        packUUID(bytebuffer, AgentBlock_Field.Hunter);
        packUUID(bytebuffer, AgentBlock_Field.Prey);
        packIPAddress(bytebuffer, AgentBlock_Field.SpaceIP);
        bytebuffer.put((byte)LocationBlock_Fields.size());
        LocationBlock locationblock;
        for (Iterator iterator = LocationBlock_Fields.iterator(); iterator.hasNext(); packDouble(bytebuffer, locationblock.GlobalY))
        {
            locationblock = (LocationBlock)iterator.next();
            packDouble(bytebuffer, locationblock.GlobalX);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentBlock_Field.Hunter = unpackUUID(bytebuffer);
        AgentBlock_Field.Prey = unpackUUID(bytebuffer);
        AgentBlock_Field.SpaceIP = unpackIPAddress(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            LocationBlock locationblock = new LocationBlock();
            locationblock.GlobalX = unpackDouble(bytebuffer);
            locationblock.GlobalY = unpackDouble(bytebuffer);
            LocationBlock_Fields.add(locationblock);
        }

    }
}
