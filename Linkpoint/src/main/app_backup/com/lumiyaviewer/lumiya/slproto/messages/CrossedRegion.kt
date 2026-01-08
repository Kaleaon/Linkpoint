package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.net.Inet4Address
import java.nio.ByteBuffer

class CrossedRegion : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var Info_Field: Info = Info()
    var RegionData_Field: RegionData = RegionData()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
        var SessionID: UUID = UUIDPool.ZeroUUID
    }

    class Info {
        var LookAt: LLVector3 = LLVector3()
        var Position: LLVector3 = LLVector3()
    }

    class RegionData {
        var RegionHandle: Long = 0
        var SeedCapability: ByteArray = ByteArray(0)
        var SimIP: Inet4Address? = null
        var SimPort: Int = 0
    }

    init {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return this.RegionData_Field.SeedCapability.size + 16 + 34 + 24
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleCrossedRegion(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put((-1).toByte())
        byteBuffer.put(7.toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        this.RegionData_Field.SimIP?.let { packIPAddress(byteBuffer, it) }
        packShort(byteBuffer, this.RegionData_Field.SimPort.toShort())
        packLong(byteBuffer, this.RegionData_Field.RegionHandle)
        packVariable(byteBuffer, this.RegionData_Field.SeedCapability, 2)
        packLLVector3(byteBuffer, this.Info_Field.Position)
        packLLVector3(byteBuffer, this.Info_Field.LookAt)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.RegionData_Field.SimIP = unpackIPAddress(byteBuffer)
        this.RegionData_Field.SimPort = unpackShort(byteBuffer).toInt() and 0xFFFF
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer)
        this.RegionData_Field.SeedCapability = unpackVariable(byteBuffer, 2)
        this.Info_Field.Position = unpackLLVector3(byteBuffer)
        this.Info_Field.LookAt = unpackLLVector3(byteBuffer)
    }
}
