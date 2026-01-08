package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupActiveProposalsRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    GroupData GroupData_Field = GroupData()
    TransactionData TransactionData_Field = TransactionData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class GroupData {
        UUID GroupID
    }

    class TransactionData {
        UUID TransactionID
    }

    GroupActiveProposalsRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 68
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleGroupActiveProposalsRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 103)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer)
    }
}
