package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ViewerStats : SLMessage {
    AgentData AgentData_Field
    DownloadTotals DownloadTotals_Field
    FailStats FailStats_Field
    ArrayList<MiscStats> MiscStats_Fields = ArrayList<>()
    NetStats[] NetStats_Fields = NetStats[2]

    class AgentData {
        UUID AgentID
        Int AgentsInView
        Float FPS
        Inet4Address IP
        Double MetersTraveled
        Float Ping
        Int RegionsVisited
        Float RunTime
        UUID SessionID
        Float SimFPS
        Int StartTime
        ByteArray SysCPU
        ByteArray SysGPU
        ByteArray SysOS
        Int SysRAM
    }

    class DownloadTotals {
        Int Objects
        Int Textures
        Int World
    }

    class FailStats {
        Int Dropped
        Int FailedResends
        Int Invalid
        Int OffCircuit
        Int Resent
        Int SendPacket
    }

    class MiscStats {
        Int Type
        Double Value
    }

    class NetStats {
        Int Bytes
        Int Compressed
        Int Packets
        Int Savings
    }

    ViewerStats() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.DownloadTotals_Field = DownloadTotals()
        for (Int i = 0; i < 2; i++) {
            this.NetStats_Fields[i] = NetStats()
        }
        this.FailStats_Field = FailStats()
    }

    Int CalcPayloadSize() {
        return this.AgentData_Field.SysOS.length + 74 + 1 + this.AgentData_Field.SysCPU.length + 1 + this.AgentData_Field.SysGPU.length + 4 + 12 + 32 + 24 + 1 + (this.MiscStats_Fields.size() * 12)
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleViewerStats(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -125)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packIPAddress(byteBuffer, this.AgentData_Field.IP)
        packInt(byteBuffer, this.AgentData_Field.StartTime)
        packFloat(byteBuffer, this.AgentData_Field.RunTime)
        packFloat(byteBuffer, this.AgentData_Field.SimFPS)
        packFloat(byteBuffer, this.AgentData_Field.FPS)
        packByte(byteBuffer, (Byte) this.AgentData_Field.AgentsInView)
        packFloat(byteBuffer, this.AgentData_Field.Ping)
        packDouble(byteBuffer, this.AgentData_Field.MetersTraveled)
        packInt(byteBuffer, this.AgentData_Field.RegionsVisited)
        packInt(byteBuffer, this.AgentData_Field.SysRAM)
        packVariable(byteBuffer, this.AgentData_Field.SysOS, 1)
        packVariable(byteBuffer, this.AgentData_Field.SysCPU, 1)
        packVariable(byteBuffer, this.AgentData_Field.SysGPU, 1)
        packInt(byteBuffer, this.DownloadTotals_Field.World)
        packInt(byteBuffer, this.DownloadTotals_Field.Objects)
        packInt(byteBuffer, this.DownloadTotals_Field.Textures)
        for (Int i = 0; i < 2; i++) {
            packInt(byteBuffer, this.NetStats_Fields[i].Bytes)
            packInt(byteBuffer, this.NetStats_Fields[i].Packets)
            packInt(byteBuffer, this.NetStats_Fields[i].Compressed)
            packInt(byteBuffer, this.NetStats_Fields[i].Savings)
        }
        packInt(byteBuffer, this.FailStats_Field.SendPacket)
        packInt(byteBuffer, this.FailStats_Field.Dropped)
        packInt(byteBuffer, this.FailStats_Field.Resent)
        packInt(byteBuffer, this.FailStats_Field.FailedResends)
        packInt(byteBuffer, this.FailStats_Field.OffCircuit)
        packInt(byteBuffer, this.FailStats_Field.Invalid)
        byteBuffer.put((Byte) this.MiscStats_Fields.size())
        for (MiscStats miscStats : this.MiscStats_Fields) {
            packInt(byteBuffer, miscStats.Type)
            packDouble(byteBuffer, miscStats.Value)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.IP = unpackIPAddress(byteBuffer)
        this.AgentData_Field.StartTime = unpackInt(byteBuffer)
        this.AgentData_Field.RunTime = unpackFloat(byteBuffer)
        this.AgentData_Field.SimFPS = unpackFloat(byteBuffer)
        this.AgentData_Field.FPS = unpackFloat(byteBuffer)
        this.AgentData_Field.AgentsInView = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.AgentData_Field.Ping = unpackFloat(byteBuffer)
        this.AgentData_Field.MetersTraveled = unpackDouble(byteBuffer)
        this.AgentData_Field.RegionsVisited = unpackInt(byteBuffer)
        this.AgentData_Field.SysRAM = unpackInt(byteBuffer)
        this.AgentData_Field.SysOS = unpackVariable(byteBuffer, 1)
        this.AgentData_Field.SysCPU = unpackVariable(byteBuffer, 1)
        this.AgentData_Field.SysGPU = unpackVariable(byteBuffer, 1)
        this.DownloadTotals_Field.World = unpackInt(byteBuffer)
        this.DownloadTotals_Field.Objects = unpackInt(byteBuffer)
        this.DownloadTotals_Field.Textures = unpackInt(byteBuffer)
        for (Int i = 0; i < 2; i++) {
            this.NetStats_Fields[i].Bytes = unpackInt(byteBuffer)
            this.NetStats_Fields[i].Packets = unpackInt(byteBuffer)
            this.NetStats_Fields[i].Compressed = unpackInt(byteBuffer)
            this.NetStats_Fields[i].Savings = unpackInt(byteBuffer)
        }
        this.FailStats_Field.SendPacket = unpackInt(byteBuffer)
        this.FailStats_Field.Dropped = unpackInt(byteBuffer)
        this.FailStats_Field.Resent = unpackInt(byteBuffer)
        this.FailStats_Field.FailedResends = unpackInt(byteBuffer)
        this.FailStats_Field.OffCircuit = unpackInt(byteBuffer)
        this.FailStats_Field.Invalid = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b; i2++) {
            MiscStats miscStats = MiscStats()
            miscStats.Type = unpackInt(byteBuffer)
            miscStats.Value = unpackDouble(byteBuffer)
            this.MiscStats_Fields.add(miscStats)
        }
    }
}
