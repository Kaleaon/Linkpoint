package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EventLocationRequest : SLMessage() {
    public EventData EventData_Field = EventData()
    public QueryData QueryData_Field = QueryData()

    @JvmStatic
    class EventData {
        public Int EventID
    }

    @JvmStatic
    class QueryData {
        public UUID QueryID
    }

    public EventLocationRequest() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return 24
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEventLocationRequest(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 51)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packInt(byteBuffer, this.EventData_Field.EventID)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.EventData_Field.EventID = unpackInt(byteBuffer)
    }
}
