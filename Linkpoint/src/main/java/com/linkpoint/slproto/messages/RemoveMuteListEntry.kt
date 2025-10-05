package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RemoveMuteListEntry : SLMessage() {
    val AgentData_Field = AgentData()
    val MuteData_Field = MuteData()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class MuteData {
        var MuteID: UUID? = null
        lateinit var MuteName: ByteArray
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return MuteData_Field.MuteName.size + 17 + 36
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleRemoveMuteListEntry(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(8.toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packUUID(buffer, MuteData_Field.MuteID)
        packVariable(buffer, MuteData_Field.MuteName, 1)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        MuteData_Field.MuteID = unpackUUID(buffer)
        MuteData_Field.MuteName = unpackVariable(buffer, 1)
    }
}