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

public class TeleportFinish extends SLMessage
{
    public static class Info
    {

        public UUID AgentID;
        public int LocationID;
        public long RegionHandle;
        public byte SeedCapability[];
        public int SimAccess;
        public Inet4Address SimIP;
        public int SimPort;
        public int TeleportFlags;

        public Info()
        {
        }
    }


    public Info Info_Field;

    public TeleportFinish()
    {
        zeroCoded = false;
        Info_Field = new Info();
    }

    public int CalcPayloadSize()
    {
        return Info_Field.SeedCapability.length + 36 + 1 + 4 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleTeleportFinish(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)69);
        packUUID(bytebuffer, Info_Field.AgentID);
        packInt(bytebuffer, Info_Field.LocationID);
        packIPAddress(bytebuffer, Info_Field.SimIP);
        packShort(bytebuffer, (short)Info_Field.SimPort);
        packLong(bytebuffer, Info_Field.RegionHandle);
        packVariable(bytebuffer, Info_Field.SeedCapability, 2);
        packByte(bytebuffer, (byte)Info_Field.SimAccess);
        packInt(bytebuffer, Info_Field.TeleportFlags);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Info_Field.AgentID = unpackUUID(bytebuffer);
        Info_Field.LocationID = unpackInt(bytebuffer);
        Info_Field.SimIP = unpackIPAddress(bytebuffer);
        Info_Field.SimPort = unpackShort(bytebuffer) & 0xffff;
        Info_Field.RegionHandle = unpackLong(bytebuffer);
        Info_Field.SeedCapability = unpackVariable(bytebuffer, 2);
        Info_Field.SimAccess = unpackByte(bytebuffer) & 0xff;
        Info_Field.TeleportFlags = unpackInt(bytebuffer);
    }
}
