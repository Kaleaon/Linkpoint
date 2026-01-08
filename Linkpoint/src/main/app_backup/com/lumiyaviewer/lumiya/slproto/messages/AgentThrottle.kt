package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AgentThrottle : SLMessage {
    var AgentData_Field: AgentData? = AgentData()
    var Throttle_Field: Throttle? = Throttle()

    class AgentData {
        var AgentID: UUID? = null
        var CircuitCode: Int = 0
        var SessionID: UUID? = null
    }

    class Throttle {
        var GenCounter: Int = 0
        var Throttles: ByteArray? = null
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return (Throttle_Field?.Throttles?.size ?: 0) + 2 + 48
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAgentThrottle(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(81.toByte())
        packUUID(byteBuffer, AgentData_Field?.AgentID!!)
        packUUID(byteBuffer, AgentData_Field?.SessionID!!)
        packInt(byteBuffer, AgentData_Field?.CircuitCode ?: 0)
        packInt(byteBuffer, Throttle_Field?.GenCounter ?: 0)
        packVariable(byteBuffer, Throttle_Field?.Throttles!!, 2)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        val agentData = AgentData_Field ?: AgentData()
        agentData.AgentID = unpackUUID(byteBuffer)
        agentData.SessionID = unpackUUID(byteBuffer)
        agentData.CircuitCode = unpackInt(byteBuffer)
        this.AgentData_Field = agentData

        val throttle = Throttle_Field ?: Throttle()
        throttle.GenCounter = unpackInt(byteBuffer)
        throttle.Throttles = unpackVariable(byteBuffer, 2)
        this.Throttle_Field = throttle
    }
}
