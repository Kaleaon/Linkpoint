package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DirPlacesQueryBackend : SLMessage {
    AgentData AgentData_Field = AgentData()
    QueryData QueryData_Field = QueryData()

    class AgentData {
        UUID AgentID
    }

    class QueryData {
        Int Category
        Int EstateID
        Boolean Godlike
        Int QueryFlags
        UUID QueryID
        Int QueryStart
        byte[] QueryText
        byte[] SimName
    }

    DirPlacesQueryBackend() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.QueryData_Field.QueryText.length + 17 + 4 + 1 + 1 + this.QueryData_Field.SimName.length + 4 + 1 + 4 + 20
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDirPlacesQueryBackend(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 34)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packVariable(byteBuffer, this.QueryData_Field.QueryText, 1)
        packInt(byteBuffer, this.QueryData_Field.QueryFlags)
        packByte(byteBuffer, (byte) this.QueryData_Field.Category)
        packVariable(byteBuffer, this.QueryData_Field.SimName, 1)
        packInt(byteBuffer, this.QueryData_Field.EstateID)
        packBoolean(byteBuffer, this.QueryData_Field.Godlike)
        packInt(byteBuffer, this.QueryData_Field.QueryStart)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryText = unpackVariable(byteBuffer, 1)
        this.QueryData_Field.QueryFlags = unpackInt(byteBuffer)
        this.QueryData_Field.Category = unpackByte(byteBuffer)
        this.QueryData_Field.SimName = unpackVariable(byteBuffer, 1)
        this.QueryData_Field.EstateID = unpackInt(byteBuffer)
        this.QueryData_Field.Godlike = unpackBoolean(byteBuffer)
        this.QueryData_Field.QueryStart = unpackInt(byteBuffer)
    }
}
