// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class CrossedRegion extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class Info
    {

        public LLVector3 LookAt;
        public LLVector3 Position;

        public Info()
        {
        }
    }

    public static class RegionData
    {

        public long RegionHandle;
        public byte SeedCapability[];
        public Inet4Address SimIP;
        public int SimPort;

        public RegionData()
        {
        }
    }


    public AgentData AgentData_Field;
    public Info Info_Field;
    public RegionData RegionData_Field;

    public CrossedRegion()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        RegionData_Field = new RegionData();
        Info_Field = new Info();
    }

    public int CalcPayloadSize()
    {
        return RegionData_Field.SeedCapability.length + 16 + 34 + 24;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleCrossedRegion(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)-1);
        bytebuffer.put((byte)7);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packIPAddress(bytebuffer, RegionData_Field.SimIP);
        packShort(bytebuffer, (short)RegionData_Field.SimPort);
        packLong(bytebuffer, RegionData_Field.RegionHandle);
        packVariable(bytebuffer, RegionData_Field.SeedCapability, 2);
        packLLVector3(bytebuffer, Info_Field.Position);
        packLLVector3(bytebuffer, Info_Field.LookAt);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        RegionData_Field.SimIP = unpackIPAddress(bytebuffer);
        RegionData_Field.SimPort = unpackShort(bytebuffer) & 0xffff;
        RegionData_Field.RegionHandle = unpackLong(bytebuffer);
        RegionData_Field.SeedCapability = unpackVariable(bytebuffer, 2);
        Info_Field.Position = unpackLLVector3(bytebuffer);
        Info_Field.LookAt = unpackLLVector3(bytebuffer);
    }
}
