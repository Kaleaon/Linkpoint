package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DeclineCallingCard : SLMessage() {
    val AgentData_Field = AgentData()
    val TransactionBlock_Field = TransactionBlock()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class TransactionBlock {
        var TransactionID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 52

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleDeclineCallingCard(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(47.toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packUUID(buffer, TransactionBlock_Field.TransactionID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        TransactionBlock_Field.TransactionID = unpackUUID(buffer)
    }
}