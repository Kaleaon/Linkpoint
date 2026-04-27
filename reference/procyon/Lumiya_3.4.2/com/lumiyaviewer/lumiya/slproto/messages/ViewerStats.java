// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.net.Inet4Address;
import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ViewerStats extends SLMessage
{
    public AgentData AgentData_Field;
    public DownloadTotals DownloadTotals_Field;
    public FailStats FailStats_Field;
    public ArrayList<MiscStats> MiscStats_Fields;
    public NetStats[] NetStats_Fields;
    
    public ViewerStats() {
        this.NetStats_Fields = new NetStats[2];
        this.MiscStats_Fields = new ArrayList<MiscStats>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.DownloadTotals_Field = new DownloadTotals();
        for (int i = 0; i < 2; ++i) {
            this.NetStats_Fields[i] = new NetStats();
        }
        this.FailStats_Field = new FailStats();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.AgentData_Field.SysOS.length + 74 + 1 + this.AgentData_Field.SysCPU.length + 1 + this.AgentData_Field.SysGPU.length + 4 + 12 + 32 + 24 + 1 + this.MiscStats_Fields.size() * 12;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleViewerStats(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        int i = 0;
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-125));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packIPAddress(byteBuffer, this.AgentData_Field.IP);
        this.packInt(byteBuffer, this.AgentData_Field.StartTime);
        this.packFloat(byteBuffer, this.AgentData_Field.RunTime);
        this.packFloat(byteBuffer, this.AgentData_Field.SimFPS);
        this.packFloat(byteBuffer, this.AgentData_Field.FPS);
        this.packByte(byteBuffer, (byte)this.AgentData_Field.AgentsInView);
        this.packFloat(byteBuffer, this.AgentData_Field.Ping);
        this.packDouble(byteBuffer, this.AgentData_Field.MetersTraveled);
        this.packInt(byteBuffer, this.AgentData_Field.RegionsVisited);
        this.packInt(byteBuffer, this.AgentData_Field.SysRAM);
        this.packVariable(byteBuffer, this.AgentData_Field.SysOS, 1);
        this.packVariable(byteBuffer, this.AgentData_Field.SysCPU, 1);
        this.packVariable(byteBuffer, this.AgentData_Field.SysGPU, 1);
        this.packInt(byteBuffer, this.DownloadTotals_Field.World);
        this.packInt(byteBuffer, this.DownloadTotals_Field.Objects);
        this.packInt(byteBuffer, this.DownloadTotals_Field.Textures);
        while (i < 2) {
            this.packInt(byteBuffer, this.NetStats_Fields[i].Bytes);
            this.packInt(byteBuffer, this.NetStats_Fields[i].Packets);
            this.packInt(byteBuffer, this.NetStats_Fields[i].Compressed);
            this.packInt(byteBuffer, this.NetStats_Fields[i].Savings);
            ++i;
        }
        this.packInt(byteBuffer, this.FailStats_Field.SendPacket);
        this.packInt(byteBuffer, this.FailStats_Field.Dropped);
        this.packInt(byteBuffer, this.FailStats_Field.Resent);
        this.packInt(byteBuffer, this.FailStats_Field.FailedResends);
        this.packInt(byteBuffer, this.FailStats_Field.OffCircuit);
        this.packInt(byteBuffer, this.FailStats_Field.Invalid);
        byteBuffer.put((byte)this.MiscStats_Fields.size());
        for (final MiscStats miscStats : this.MiscStats_Fields) {
            this.packInt(byteBuffer, miscStats.Type);
            this.packDouble(byteBuffer, miscStats.Value);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.IP = this.unpackIPAddress(byteBuffer);
        this.AgentData_Field.StartTime = this.unpackInt(byteBuffer);
        this.AgentData_Field.RunTime = this.unpackFloat(byteBuffer);
        this.AgentData_Field.SimFPS = this.unpackFloat(byteBuffer);
        this.AgentData_Field.FPS = this.unpackFloat(byteBuffer);
        this.AgentData_Field.AgentsInView = (this.unpackByte(byteBuffer) & 0xFF);
        this.AgentData_Field.Ping = this.unpackFloat(byteBuffer);
        this.AgentData_Field.MetersTraveled = this.unpackDouble(byteBuffer);
        this.AgentData_Field.RegionsVisited = this.unpackInt(byteBuffer);
        this.AgentData_Field.SysRAM = this.unpackInt(byteBuffer);
        this.AgentData_Field.SysOS = this.unpackVariable(byteBuffer, 1);
        this.AgentData_Field.SysCPU = this.unpackVariable(byteBuffer, 1);
        this.AgentData_Field.SysGPU = this.unpackVariable(byteBuffer, 1);
        this.DownloadTotals_Field.World = this.unpackInt(byteBuffer);
        this.DownloadTotals_Field.Objects = this.unpackInt(byteBuffer);
        this.DownloadTotals_Field.Textures = this.unpackInt(byteBuffer);
        for (int i = 0; i < 2; ++i) {
            this.NetStats_Fields[i].Bytes = this.unpackInt(byteBuffer);
            this.NetStats_Fields[i].Packets = this.unpackInt(byteBuffer);
            this.NetStats_Fields[i].Compressed = this.unpackInt(byteBuffer);
            this.NetStats_Fields[i].Savings = this.unpackInt(byteBuffer);
        }
        this.FailStats_Field.SendPacket = this.unpackInt(byteBuffer);
        this.FailStats_Field.Dropped = this.unpackInt(byteBuffer);
        this.FailStats_Field.Resent = this.unpackInt(byteBuffer);
        this.FailStats_Field.FailedResends = this.unpackInt(byteBuffer);
        this.FailStats_Field.OffCircuit = this.unpackInt(byteBuffer);
        this.FailStats_Field.Invalid = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int j = n; j < (value & 0xFF); ++j) {
            final MiscStats e = new MiscStats();
            e.Type = this.unpackInt(byteBuffer);
            e.Value = this.unpackDouble(byteBuffer);
            this.MiscStats_Fields.add(e);
        }
    }
    
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
        public byte[] SysCPU;
        public byte[] SysGPU;
        public byte[] SysOS;
        public int SysRAM;
    }
    
    public static class DownloadTotals
    {
        public int Objects;
        public int Textures;
        public int World;
    }
    
    public static class FailStats
    {
        public int Dropped;
        public int FailedResends;
        public int Invalid;
        public int OffCircuit;
        public int Resent;
        public int SendPacket;
    }
    
    public static class MiscStats
    {
        public int Type;
        public double Value;
    }
    
    public static class NetStats
    {
        public int Bytes;
        public int Compressed;
        public int Packets;
        public int Savings;
    }
}
