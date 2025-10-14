package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return 20
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUseCachedMuteList(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 63)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
    }
}
