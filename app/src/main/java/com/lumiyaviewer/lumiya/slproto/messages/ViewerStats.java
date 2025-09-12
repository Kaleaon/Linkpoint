package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class ViewerStats extends SLMessage {
    public AgentData AgentData_Field;
    public DownloadTotals DownloadTotals_Field;
    public FailStats FailStats_Field;
    public ArrayList<MiscStats> MiscStats_Fields = new ArrayList<>();
    public NetStats[] NetStats_Fields = new NetStats[2];

    public static class AgentData {
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

    public static class DownloadTotals {
        public int Objects;
        public int Textures;
        public int World;
    }

    public static class FailStats {
        public int Dropped;
        public int FailedResends;
        public int Invalid;
        public int OffCircuit;
        public int Resent;
        public int SendPacket;
    }

    public static class MiscStats {
        public int Type;
        public double Value;
    }

    public static class NetStats {
        public int Bytes;
        public int Compressed;
        public int Packets;
        public int Savings;
    }

    public ViewerStats() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.DownloadTotals_Field = new DownloadTotals();
        for (int i = 0; i < 2; i++) {
            this.NetStats_Fields[i] = new NetStats();
        }
        this.FailStats_Field = new FailStats();
    }

    public int CalcPayloadSize() {
        return this.AgentData_Field.SysOS.length + 74 + 1 + this.AgentData_Field.SysCPU.length + 1 + this.AgentData_Field.SysGPU.length + 4 + 12 + 32 + 24 + 1 + (this.MiscStats_Fields.size() * 12);
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleViewerStats(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -125);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packIPAddress(byteBuffer, this.AgentData_Field.IP);
        packInt(byteBuffer, this.AgentData_Field.StartTime);
        packFloat(byteBuffer, this.AgentData_Field.RunTime);
        packFloat(byteBuffer, this.AgentData_Field.SimFPS);
        packFloat(byteBuffer, this.AgentData_Field.FPS);
        packByte(byteBuffer, (byte) this.AgentData_Field.AgentsInView);
        packFloat(byteBuffer, this.AgentData_Field.Ping);
        packDouble(byteBuffer, this.AgentData_Field.MetersTraveled);
        packInt(byteBuffer, this.AgentData_Field.RegionsVisited);
        packInt(byteBuffer, this.AgentData_Field.SysRAM);
        packVariable(byteBuffer, this.AgentData_Field.SysOS, 1);
        packVariable(byteBuffer, this.AgentData_Field.SysCPU, 1);
        packVariable(byteBuffer, this.AgentData_Field.SysGPU, 1);
        packInt(byteBuffer, this.DownloadTotals_Field.World);
        packInt(byteBuffer, this.DownloadTotals_Field.Objects);
        packInt(byteBuffer, this.DownloadTotals_Field.Textures);
        for (int i = 0; i < 2; i++) {
            packInt(byteBuffer, this.NetStats_Fields[i].Bytes);
            packInt(byteBuffer, this.NetStats_Fields[i].Packets);
            packInt(byteBuffer, this.NetStats_Fields[i].Compressed);
            packInt(byteBuffer, this.NetStats_Fields[i].Savings);
        }
        packInt(byteBuffer, this.FailStats_Field.SendPacket);
        packInt(byteBuffer, this.FailStats_Field.Dropped);
        packInt(byteBuffer, this.FailStats_Field.Resent);
        packInt(byteBuffer, this.FailStats_Field.FailedResends);
        packInt(byteBuffer, this.FailStats_Field.OffCircuit);
        packInt(byteBuffer, this.FailStats_Field.Invalid);
        byteBuffer.put((byte) this.MiscStats_Fields.size());
        for (MiscStats miscStats : this.MiscStats_Fields) {
            packInt(byteBuffer, miscStats.Type);
            packDouble(byteBuffer, miscStats.Value);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.IP = unpackIPAddress(byteBuffer);
        this.AgentData_Field.StartTime = unpackInt(byteBuffer);
        this.AgentData_Field.RunTime = unpackFloat(byteBuffer);
        this.AgentData_Field.SimFPS = unpackFloat(byteBuffer);
        this.AgentData_Field.FPS = unpackFloat(byteBuffer);
        this.AgentData_Field.AgentsInView = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.AgentData_Field.Ping = unpackFloat(byteBuffer);
        this.AgentData_Field.MetersTraveled = unpackDouble(byteBuffer);
        this.AgentData_Field.RegionsVisited = unpackInt(byteBuffer);
        this.AgentData_Field.SysRAM = unpackInt(byteBuffer);
        this.AgentData_Field.SysOS = unpackVariable(byteBuffer, 1);
        this.AgentData_Field.SysCPU = unpackVariable(byteBuffer, 1);
        this.AgentData_Field.SysGPU = unpackVariable(byteBuffer, 1);
        this.DownloadTotals_Field.World = unpackInt(byteBuffer);
        this.DownloadTotals_Field.Objects = unpackInt(byteBuffer);
        this.DownloadTotals_Field.Textures = unpackInt(byteBuffer);
        for (int i = 0; i < 2; i++) {
            this.NetStats_Fields[i].Bytes = unpackInt(byteBuffer);
            this.NetStats_Fields[i].Packets = unpackInt(byteBuffer);
            this.NetStats_Fields[i].Compressed = unpackInt(byteBuffer);
            this.NetStats_Fields[i].Savings = unpackInt(byteBuffer);
        }
        this.FailStats_Field.SendPacket = unpackInt(byteBuffer);
        this.FailStats_Field.Dropped = unpackInt(byteBuffer);
        this.FailStats_Field.Resent = unpackInt(byteBuffer);
        this.FailStats_Field.FailedResends = unpackInt(byteBuffer);
        this.FailStats_Field.OffCircuit = unpackInt(byteBuffer);
        this.FailStats_Field.Invalid = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < b; i2++) {
            MiscStats miscStats = new MiscStats();
            miscStats.Type = unpackInt(byteBuffer);
            miscStats.Value = unpackDouble(byteBuffer);
            this.MiscStats_Fields.add(miscStats);
        }
    }
}
