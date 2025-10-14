package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarNotesReply : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
    }

    class Data {
        byte[] Notes
        UUID TargetID
    }

    AvatarNotesReply() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.Data_Field.Notes.length + 18 + 20
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarNotesReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -80)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.Data_Field.TargetID)
        packVariable(byteBuffer, this.Data_Field.Notes, 2)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.Data_Field.TargetID = unpackUUID(byteBuffer)
        this.Data_Field.Notes = unpackVariable(byteBuffer, 2)
    }
}
