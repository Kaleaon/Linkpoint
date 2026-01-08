package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarNotesUpdate : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Data {
        ByteArray Notes
        UUID TargetID
    }

    AvatarNotesUpdate() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.Data_Field.Notes.size + 18 + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleAvatarNotesUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -79)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.TargetID)
        packVariable(byteBuffer, this.Data_Field.Notes, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.TargetID = unpackUUID(byteBuffer)
        this.Data_Field.Notes = unpackVariable(byteBuffer, 2)
    }
}
