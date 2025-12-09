package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DirClassifiedQueryBackend : SLMessage {
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
        ByteArray QueryText
    }

    DirClassifiedQueryBackend() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.QueryData_Field.QueryText.size + 17 + 4 + 4 + 4 + 1 + 4 + 20
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleDirClassifiedQueryBackend(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 40)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packVariable(byteBuffer, this.QueryData_Field.QueryText, 1)
        packInt(byteBuffer, this.QueryData_Field.QueryFlags)
        packInt(byteBuffer, this.QueryData_Field.Category)
        packInt(byteBuffer, this.QueryData_Field.EstateID)
        packBoolean(byteBuffer, this.QueryData_Field.Godlike)
        packInt(byteBuffer, this.QueryData_Field.QueryStart)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryText = unpackVariable(byteBuffer, 1)
        this.QueryData_Field.QueryFlags = unpackInt(byteBuffer)
        this.QueryData_Field.Category = unpackInt(byteBuffer)
        this.QueryData_Field.EstateID = unpackInt(byteBuffer)
        this.QueryData_Field.Godlike = unpackBoolean(byteBuffer)
        this.QueryData_Field.QueryStart = unpackInt(byteBuffer)
    }
}
