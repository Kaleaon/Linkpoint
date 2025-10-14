package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RemoveMuteListEntry : SLMessage {
    AgentData AgentData_Field = AgentData()
    MuteData MuteData_Field = MuteData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class MuteData {
        UUID MuteID
        Byte[] MuteName
    }

    RemoveMuteListEntry() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.MuteData_Field.MuteName.length + 17 + 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRemoveMuteListEntry(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 8)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.MuteData_Field.MuteID)
        packVariable(byteBuffer, this.MuteData_Field.MuteName, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.MuteData_Field.MuteID = unpackUUID(byteBuffer)
        this.MuteData_Field.MuteName = unpackVariable(byteBuffer, 1)
    }
}
