package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DirLandQuery : SLMessage {
    AgentData AgentData_Field = AgentData()
    QueryData QueryData_Field = QueryData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class QueryData {
        Int Area
        Int Price
        Int QueryFlags
        UUID QueryID
        Int QueryStart
        Int SearchType
    }

    DirLandQuery() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 72
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleDirLandQuery(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 48)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packInt(byteBuffer, this.QueryData_Field.QueryFlags)
        packInt(byteBuffer, this.QueryData_Field.SearchType)
        packInt(byteBuffer, this.QueryData_Field.Price)
        packInt(byteBuffer, this.QueryData_Field.Area)
        packInt(byteBuffer, this.QueryData_Field.QueryStart)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryFlags = unpackInt(byteBuffer)
        this.QueryData_Field.SearchType = unpackInt(byteBuffer)
        this.QueryData_Field.Price = unpackInt(byteBuffer)
        this.QueryData_Field.Area = unpackInt(byteBuffer)
        this.QueryData_Field.QueryStart = unpackInt(byteBuffer)
    }
}
