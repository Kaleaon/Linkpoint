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

    Int CalcPayloadSize() {
        return 24
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEventLocationRequest(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 51)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packInt(byteBuffer, this.EventData_Field.EventID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.EventData_Field.EventID = unpackInt(byteBuffer)
    }
}
