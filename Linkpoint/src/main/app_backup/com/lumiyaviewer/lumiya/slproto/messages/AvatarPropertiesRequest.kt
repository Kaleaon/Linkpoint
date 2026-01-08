package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AvatarPropertiesRequest : SLMessage {
    var AgentData_Field: AgentData = AgentData()

    class AgentData {
        var AgentID: UUID? = null
        var AvatarID: UUID? = null
        var SessionID: UUID? = null
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 52
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarPropertiesRequest(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-87).toByte())
        packUUID(byteBuffer, AgentData_Field.AgentID!!)
        packUUID(byteBuffer, AgentData_Field.SessionID!!)
        packUUID(byteBuffer, AgentData_Field.AvatarID!!)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(byteBuffer)
        AgentData_Field.SessionID = unpackUUID(byteBuffer)
        AgentData_Field.AvatarID = unpackUUID(byteBuffer)
    }
}
