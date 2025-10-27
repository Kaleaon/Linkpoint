package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MapNameRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    NameData NameData_Field = NameData()

    class AgentData {
        UUID AgentID
        Int EstateID
        Int Flags
        Boolean Godlike
        UUID SessionID
    }

    class NameData {
        ByteArray Name
    }

    MapNameRequest() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.NameData_Field.Name.length + 1 + 45
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMapNameRequest(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -104)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.Flags)
        packInt(byteBuffer, this.AgentData_Field.EstateID)
        packBoolean(byteBuffer, this.AgentData_Field.Godlike)
        packVariable(byteBuffer, this.NameData_Field.Name, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.Flags = unpackInt(byteBuffer)
        this.AgentData_Field.EstateID = unpackInt(byteBuffer)
        this.AgentData_Field.Godlike = unpackBoolean(byteBuffer)
        this.NameData_Field.Name = unpackVariable(byteBuffer, 1)
    }
}
