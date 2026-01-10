package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DeclineFriendship : SLMessage {
    AgentData AgentData_Field = AgentData()
    TransactionBlock TransactionBlock_Field = TransactionBlock()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class TransactionBlock {
        UUID TransactionID
    }

    DeclineFriendship() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 52
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleDeclineFriendship(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 42)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.TransactionBlock_Field.TransactionID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.TransactionBlock_Field.TransactionID = unpackUUID(byteBuffer)
    }
}
