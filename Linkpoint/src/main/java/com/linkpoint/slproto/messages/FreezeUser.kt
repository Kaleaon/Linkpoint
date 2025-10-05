package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class FreezeUser : SLMessage() {
    val AgentData_Field = AgentData()
    val Data_Field = Data()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class Data {
        var TargetID: UUID? = null
        var Flags: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 56

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleFreezeUser(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-88).toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packUUID(buffer, Data_Field.TargetID)
        packInt(buffer, Data_Field.Flags)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        Data_Field.TargetID = unpackUUID(buffer)
        Data_Field.Flags = unpackInt(buffer)
    }
}