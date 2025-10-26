package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DeclineFriendship : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public TransactionBlock TransactionBlock_Field = TransactionBlock()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class TransactionBlock {
        public UUID TransactionID
    }

    public DeclineFriendship() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return 52
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleDeclineFriendship(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 42)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.TransactionBlock_Field.TransactionID)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.TransactionBlock_Field.TransactionID = unpackUUID(byteBuffer)
    }
}
