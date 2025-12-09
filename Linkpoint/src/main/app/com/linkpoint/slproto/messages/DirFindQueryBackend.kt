package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DirFindQueryBackend : SLMessage {
    AgentData AgentData_Field = AgentData()
    QueryData QueryData_Field = QueryData()

    class AgentData {
        UUID AgentID
    }

    class QueryData {
        Int EstateID
        Boolean Godlike
        Int QueryFlags
        UUID QueryID
        Int QueryStart
        ByteArray QueryText
    }

    DirFindQueryBackend() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.QueryData_Field.QueryText.size + 17 + 4 + 4 + 4 + 1 + 20
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleDirFindQueryBackend(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 32)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packVariable(byteBuffer, this.QueryData_Field.QueryText, 1)
        packInt(byteBuffer, this.QueryData_Field.QueryFlags)
        packInt(byteBuffer, this.QueryData_Field.QueryStart)
        packInt(byteBuffer, this.QueryData_Field.EstateID)
        packBoolean(byteBuffer, this.QueryData_Field.Godlike)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryText = unpackVariable(byteBuffer, 1)
        this.QueryData_Field.QueryFlags = unpackInt(byteBuffer)
        this.QueryData_Field.QueryStart = unpackInt(byteBuffer)
        this.QueryData_Field.EstateID = unpackInt(byteBuffer)
        this.QueryData_Field.Godlike = unpackBoolean(byteBuffer)
    }
}
