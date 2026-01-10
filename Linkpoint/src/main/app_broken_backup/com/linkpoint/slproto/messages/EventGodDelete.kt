package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EventGodDelete : SLMessage {
    AgentData AgentData_Field = AgentData()
    EventData EventData_Field = EventData()
    QueryData QueryData_Field = QueryData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class EventData {
        Int EventID
    }

    class QueryData {
        Int QueryFlags
        UUID QueryID
        Int QueryStart
        ByteArray QueryText
    }

    EventGodDelete() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.QueryData_Field.QueryText.size + 17 + 4 + 4 + 40
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleEventGodDelete(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -73)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.EventData_Field.EventID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packVariable(byteBuffer, this.QueryData_Field.QueryText, 1)
        packInt(byteBuffer, this.QueryData_Field.QueryFlags)
        packInt(byteBuffer, this.QueryData_Field.QueryStart)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.EventData_Field.EventID = unpackInt(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryText = unpackVariable(byteBuffer, 1)
        this.QueryData_Field.QueryFlags = unpackInt(byteBuffer)
        this.QueryData_Field.QueryStart = unpackInt(byteBuffer)
    }
}
