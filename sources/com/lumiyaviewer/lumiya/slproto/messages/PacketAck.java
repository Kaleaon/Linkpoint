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

public class PacketAck extends SLMessage
{
    public static class Packets
    {

        public int ID;

        public Packets()
        {
        }
    }


    public ArrayList Packets_Fields;

    public PacketAck()
    {
        Packets_Fields = new ArrayList();
        zeroCoded = false;
    }

    public int CalcPayloadSize()
    {
        return Packets_Fields.size() * 4 + 5;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandlePacketAck(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)-1);
        bytebuffer.put((byte)-5);
        bytebuffer.put((byte)Packets_Fields.size());
        for (Iterator iterator = Packets_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, ((Packets)iterator.next()).ID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            Packets packets = new Packets();
            packets.ID = unpackInt(bytebuffer);
            Packets_Fields.add(packets);
        }

    }
}
