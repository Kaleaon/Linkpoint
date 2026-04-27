// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class NeighborList extends SLMessage
{
    public static class NeighborBlock
    {

        public Inet4Address IP;
        public byte Name[];
        public int Port;
        public Inet4Address PublicIP;
        public int PublicPort;
        public UUID RegionID;
        public int SimAccess;

        public NeighborBlock()
        {
        }
    }


    public NeighborBlock NeighborBlock_Fields[];

    public NeighborList()
    {
        int i = 0;
        super();
        NeighborBlock_Fields = new NeighborBlock[4];
        zeroCoded = false;
        for (; i < 4; i++)
        {
            NeighborBlock_Fields[i] = new NeighborBlock();
        }

    }

    public int CalcPayloadSize()
    {
        int j = 1;
        for (int i = 0; i < 4; i++)
        {
            j += NeighborBlock_Fields[i].Name.length + 29 + 1;
        }

        return j;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleNeighborList(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)3);
        for (int i = 0; i < 4; i++)
        {
            packIPAddress(bytebuffer, NeighborBlock_Fields[i].IP);
            packShort(bytebuffer, (short)NeighborBlock_Fields[i].Port);
            packIPAddress(bytebuffer, NeighborBlock_Fields[i].PublicIP);
            packShort(bytebuffer, (short)NeighborBlock_Fields[i].PublicPort);
            packUUID(bytebuffer, NeighborBlock_Fields[i].RegionID);
            packVariable(bytebuffer, NeighborBlock_Fields[i].Name, 1);
            packByte(bytebuffer, (byte)NeighborBlock_Fields[i].SimAccess);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        for (int i = 0; i < 4; i++)
        {
            NeighborBlock_Fields[i].IP = unpackIPAddress(bytebuffer);
            NeighborBlock_Fields[i].Port = unpackShort(bytebuffer) & 0xffff;
            NeighborBlock_Fields[i].PublicIP = unpackIPAddress(bytebuffer);
            NeighborBlock_Fields[i].PublicPort = unpackShort(bytebuffer) & 0xffff;
            NeighborBlock_Fields[i].RegionID = unpackUUID(bytebuffer);
            NeighborBlock_Fields[i].Name = unpackVariable(bytebuffer, 1);
            NeighborBlock_Fields[i].SimAccess = unpackByte(bytebuffer) & 0xff;
        }

    }
}
