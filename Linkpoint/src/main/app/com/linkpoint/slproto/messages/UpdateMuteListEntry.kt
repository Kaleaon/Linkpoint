package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UpdateMuteListEntry : SLMessage {
    AgentData AgentData_Field = AgentData()
    MuteData MuteData_Field = MuteData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class MuteData {
        Int MuteFlags
        UUID MuteID
        ByteArray MuteName
        Int MuteType
    }

    UpdateMuteListEntry() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.MuteData_Field.MuteName.length + 17 + 4 + 4 + 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUpdateMuteListEntry(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 7)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.MuteData_Field.MuteID)
        packVariable(byteBuffer, this.MuteData_Field.MuteName, 1)
        packInt(byteBuffer, this.MuteData_Field.MuteType)
        packInt(byteBuffer, this.MuteData_Field.MuteFlags)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.MuteData_Field.MuteID = unpackUUID(byteBuffer)
        this.MuteData_Field.MuteName = unpackVariable(byteBuffer, 1)
        this.MuteData_Field.MuteType = unpackInt(byteBuffer)
        this.MuteData_Field.MuteFlags = unpackInt(byteBuffer)
    }
}
