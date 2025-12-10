package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DeclineCallingCard : SLMessage {
    AgentData AgentData_Field = AgentData()
    TransactionBlock TransactionBlock_Field = TransactionBlock()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class TransactionBlock {
        UUID TransactionID
    }

    DeclineCallingCard() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 52
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleDeclineCallingCard(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 47)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.TransactionBlock_Field.TransactionID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.TransactionBlock_Field.TransactionID = unpackUUID(byteBuffer)
    }
}
