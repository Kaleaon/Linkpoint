package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MuteListUpdate : SLMessage {
    MuteData MuteData_Field = MuteData()

    class MuteData {
        UUID AgentID
        Byte[] Filename
    }

    MuteListUpdate() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.MuteData_Field.Filename.length + 17 + 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMuteListUpdate(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 62)
        packUUID(byteBuffer, this.MuteData_Field.AgentID)
        packVariable(byteBuffer, this.MuteData_Field.Filename, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.MuteData_Field.AgentID = unpackUUID(byteBuffer)
        this.MuteData_Field.Filename = unpackVariable(byteBuffer, 1)
    }
}
