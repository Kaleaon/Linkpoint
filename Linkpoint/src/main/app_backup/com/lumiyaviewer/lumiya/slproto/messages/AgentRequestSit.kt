package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class AgentRequestSit : SLMessage {
    var AgentData_Field: AgentData? = AgentData()
    var TargetObject_Field: TargetObject? = TargetObject()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class TargetObject {
        var Offset: LLVector3? = LLVector3()
        var TargetID: UUID? = null
    }

    constructor() {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return 61
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAgentRequestSit(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put(6.toByte())
        packUUID(byteBuffer, AgentData_Field?.AgentID)
        packUUID(byteBuffer, AgentData_Field?.SessionID)
        packUUID(byteBuffer, TargetObject_Field?.TargetID)
        packLLVector3(byteBuffer, TargetObject_Field?.Offset)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        val agentData = AgentData_Field ?: AgentData()
        agentData.AgentID = unpackUUID(byteBuffer)
        agentData.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field = agentData

        val targetObject = TargetObject_Field ?: TargetObject()
        targetObject.TargetID = unpackUUID(byteBuffer)
        targetObject.Offset = unpackLLVector3(byteBuffer)
        this.TargetObject_Field = targetObject
    }
}
