package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EventLocationRequest : SLMessage {
    EventData EventData_Field = EventData()
    QueryData QueryData_Field = QueryData()

    class EventData {
        Int EventID
    }

    class QueryData {
        UUID QueryID
    }

    EventLocationRequest() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 24
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleEventLocationRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 51)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packInt(byteBuffer, this.EventData_Field.EventID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.EventData_Field.EventID = unpackInt(byteBuffer)
    }
}
