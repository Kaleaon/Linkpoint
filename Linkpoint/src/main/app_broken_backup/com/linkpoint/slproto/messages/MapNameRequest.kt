package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
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

    fun CalcPayloadSize(): Int {
        return this.NameData_Field.Name.size + 1 + 45
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleMapNameRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
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

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.Flags = unpackInt(byteBuffer)
        this.AgentData_Field.EstateID = unpackInt(byteBuffer)
        this.AgentData_Field.Godlike = unpackBoolean(byteBuffer)
        this.NameData_Field.Name = unpackVariable(byteBuffer, 1)
    }
}
