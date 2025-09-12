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

public class ViewerStats extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public int AgentsInView;
        public float FPS;
        public Inet4Address IP;
        public double MetersTraveled;
        public float Ping;
        public int RegionsVisited;
        public float RunTime;
        public UUID SessionID;
        public float SimFPS;
        public int StartTime;
        public byte SysCPU[];
        public byte SysGPU[];
        public byte SysOS[];
        public int SysRAM;

        public AgentData()
        {
        }
    }

    public static class DownloadTotals
    {

        public int Objects;
        public int Textures;
        public int World;

        public DownloadTotals()
        {
        }
    }

    public static class FailStats
    {

        public int Dropped;
        public int FailedResends;
        public int Invalid;
        public int OffCircuit;
        public int Resent;
        public int SendPacket;

        public FailStats()
        {
        }
    }

    public static class MiscStats
    {

        public int Type;
        public double Value;

        public MiscStats()
        {
        }
    }

    public static class NetStats
    {

        public int Bytes;
        public int Compressed;
        public int Packets;
        public int Savings;

        public NetStats()
        {
        }
    }


    public AgentData AgentData_Field;
    public DownloadTotals DownloadTotals_Field;
    public FailStats FailStats_Field;
    public ArrayList MiscStats_Fields;
    public NetStats NetStats_Fields[];

    public ViewerStats()
    {
        NetStats_Fields = new NetStats[2];
        MiscStats_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        DownloadTotals_Field = new DownloadTotals();
        for (int i = 0; i < 2; i++)
        {
            NetStats_Fields[i] = new NetStats();
        }

        FailStats_Field = new FailStats();
    }

    public int CalcPayloadSize()
    {
        return AgentData_Field.SysOS.length + 74 + 1 + AgentData_Field.SysCPU.length + 1 + AgentData_Field.SysGPU.length + 4 + 12 + 32 + 24 + 1 + MiscStats_Fields.size() * 12;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleViewerStats(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        int i = 0;
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-125);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packIPAddress(bytebuffer, AgentData_Field.IP);
        packInt(bytebuffer, AgentData_Field.StartTime);
        packFloat(bytebuffer, AgentData_Field.RunTime);
        packFloat(bytebuffer, AgentData_Field.SimFPS);
        packFloat(bytebuffer, AgentData_Field.FPS);
        packByte(bytebuffer, (byte)AgentData_Field.AgentsInView);
        packFloat(bytebuffer, AgentData_Field.Ping);
        packDouble(bytebuffer, AgentData_Field.MetersTraveled);
        packInt(bytebuffer, AgentData_Field.RegionsVisited);
        packInt(bytebuffer, AgentData_Field.SysRAM);
        packVariable(bytebuffer, AgentData_Field.SysOS, 1);
        packVariable(bytebuffer, AgentData_Field.SysCPU, 1);
        packVariable(bytebuffer, AgentData_Field.SysGPU, 1);
        packInt(bytebuffer, DownloadTotals_Field.World);
        packInt(bytebuffer, DownloadTotals_Field.Objects);
        packInt(bytebuffer, DownloadTotals_Field.Textures);
        for (; i < 2; i++)
        {
            packInt(bytebuffer, NetStats_Fields[i].Bytes);
            packInt(bytebuffer, NetStats_Fields[i].Packets);
            packInt(bytebuffer, NetStats_Fields[i].Compressed);
            packInt(bytebuffer, NetStats_Fields[i].Savings);
        }

        packInt(bytebuffer, FailStats_Field.SendPacket);
        packInt(bytebuffer, FailStats_Field.Dropped);
        packInt(bytebuffer, FailStats_Field.Resent);
        packInt(bytebuffer, FailStats_Field.FailedResends);
        packInt(bytebuffer, FailStats_Field.OffCircuit);
        packInt(bytebuffer, FailStats_Field.Invalid);
        bytebuffer.put((byte)MiscStats_Fields.size());
        MiscStats miscstats;
        for (Iterator iterator = MiscStats_Fields.iterator(); iterator.hasNext(); packDouble(bytebuffer, miscstats.Value))
        {
            miscstats = (MiscStats)iterator.next();
            packInt(bytebuffer, miscstats.Type);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.IP = unpackIPAddress(bytebuffer);
        AgentData_Field.StartTime = unpackInt(bytebuffer);
        AgentData_Field.RunTime = unpackFloat(bytebuffer);
        AgentData_Field.SimFPS = unpackFloat(bytebuffer);
        AgentData_Field.FPS = unpackFloat(bytebuffer);
        AgentData_Field.AgentsInView = unpackByte(bytebuffer) & 0xff;
        AgentData_Field.Ping = unpackFloat(bytebuffer);
        AgentData_Field.MetersTraveled = unpackDouble(bytebuffer);
        AgentData_Field.RegionsVisited = unpackInt(bytebuffer);
        AgentData_Field.SysRAM = unpackInt(bytebuffer);
        AgentData_Field.SysOS = unpackVariable(bytebuffer, 1);
        AgentData_Field.SysCPU = unpackVariable(bytebuffer, 1);
        AgentData_Field.SysGPU = unpackVariable(bytebuffer, 1);
        DownloadTotals_Field.World = unpackInt(bytebuffer);
        DownloadTotals_Field.Objects = unpackInt(bytebuffer);
        DownloadTotals_Field.Textures = unpackInt(bytebuffer);
        for (int i = 0; i < 2; i++)
        {
            NetStats_Fields[i].Bytes = unpackInt(bytebuffer);
            NetStats_Fields[i].Packets = unpackInt(bytebuffer);
            NetStats_Fields[i].Compressed = unpackInt(bytebuffer);
            NetStats_Fields[i].Savings = unpackInt(bytebuffer);
        }

        FailStats_Field.SendPacket = unpackInt(bytebuffer);
        FailStats_Field.Dropped = unpackInt(bytebuffer);
        FailStats_Field.Resent = unpackInt(bytebuffer);
        FailStats_Field.FailedResends = unpackInt(bytebuffer);
        FailStats_Field.OffCircuit = unpackInt(bytebuffer);
        FailStats_Field.Invalid = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int j = ((flag) ? 1 : 0); j < (byte0 & 0xff); j++)
        {
            MiscStats miscstats = new MiscStats();
            miscstats.Type = unpackInt(bytebuffer);
            miscstats.Value = unpackDouble(bytebuffer);
            MiscStats_Fields.add(miscstats);
        }

    }
}
