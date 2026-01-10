package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TerminateFriendship : SLMessage {
    AgentData AgentData_Field = AgentData()
    ExBlock ExBlock_Field = ExBlock()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ExBlock {
        UUID OtherID
    }

    TerminateFriendship() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 52
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleTerminateFriendship(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 44)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.ExBlock_Field.OtherID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ExBlock_Field.OtherID = unpackUUID(byteBuffer)
    }
}
