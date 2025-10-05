package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelBuyPass : SLMessage() {
    val AgentData_Field = AgentData()
    val ParcelData_Field = ParcelData()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class ParcelData {
        var LocalID: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 40

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleParcelBuyPass(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-50).toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packInt(buffer, ParcelData_Field.LocalID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        ParcelData_Field.LocalID = unpackInt(buffer)
    }
}