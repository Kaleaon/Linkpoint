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

    public fun CalcPayloadSize(): Int {
        return 24
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleEventLocationRequest(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 51)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packInt(byteBuffer, this.EventData_Field.EventID)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.EventData_Field.EventID = unpackInt(byteBuffer)
    }
}
