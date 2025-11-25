package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class AgentMovementComplete : SLMessage {
    var AgentData_Field: AgentData? = AgentData()
    var Data_Field: Data? = Data()
    var SimData_Field: SimData? = SimData()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class Data {
        var LookAt: LLVector3? = LLVector3()
        var Position: LLVector3? = LLVector3()
        var RegionHandle: Long = 0
        var Timestamp: Int = 0
    }

    class SimData {
        var ChannelVersion: ByteArray? = null
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return (SimData_Field?.ChannelVersion?.size ?: 0) + 2 + 72
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAgentMovementComplete(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-6).toByte())
        packUUID(byteBuffer, AgentData_Field?.AgentID)
        packUUID(byteBuffer, AgentData_Field?.SessionID)
        packLLVector3(byteBuffer, Data_Field?.Position)
        packLLVector3(byteBuffer, Data_Field?.LookAt)
        packLong(byteBuffer, Data_Field?.RegionHandle ?: 0)
        packInt(byteBuffer, Data_Field?.Timestamp ?: 0)
        packVariable(byteBuffer, SimData_Field?.ChannelVersion, 2)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        val agentData = AgentData_Field ?: AgentData()
        agentData.AgentID = unpackUUID(byteBuffer)
        agentData.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field = agentData

        val data = Data_Field ?: Data()
        data.Position = unpackLLVector3(byteBuffer)
        data.LookAt = unpackLLVector3(byteBuffer)
        data.RegionHandle = unpackLong(byteBuffer)
        data.Timestamp = unpackInt(byteBuffer)
        this.Data_Field = data

        val simData = SimData_Field ?: SimData()
        simData.ChannelVersion = unpackVariable(byteBuffer, 2)
        this.SimData_Field = simData
    }
}
