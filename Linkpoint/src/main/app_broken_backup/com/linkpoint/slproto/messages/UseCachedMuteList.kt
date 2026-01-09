package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UseCachedMuteList : SLMessage {
    AgentData AgentData_Field = AgentData()

    class AgentData {
        UUID AgentID
    }

    UseCachedMuteList() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 20
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleUseCachedMuteList(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 63)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
    }
}
