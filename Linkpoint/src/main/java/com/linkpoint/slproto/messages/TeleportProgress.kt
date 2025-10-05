package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TeleportProgress : SLMessage() {
    val AgentData_Field = AgentData()
    val Info_Field = Info()

    class AgentData {
        var AgentID: UUID? = null
    }

    class Info {
        var TeleportFlags: Int = 0
        lateinit var Message: ByteArray
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return Info_Field.Message.size + 5 + 20
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleTeleportProgress(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(66.toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packInt(buffer, Info_Field.TeleportFlags)
        packVariable(buffer, Info_Field.Message, 1)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        Info_Field.TeleportFlags = unpackInt(buffer)
        Info_Field.Message = unpackVariable(buffer, 1)
    }
}