package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class EventLocationReply : SLMessage() {
    public EventData EventData_Field = EventData()
    public QueryData QueryData_Field = QueryData()

    @JvmStatic
    class EventData {
        public UUID RegionID
        public LLVector3 RegionPos
        public Boolean Success
    }

    @JvmStatic
    class QueryData {
        public UUID QueryID
    }

    public EventLocationReply() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        return 49
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleEventLocationReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 52)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packBoolean(byteBuffer, this.EventData_Field.Success)
        packUUID(byteBuffer, this.EventData_Field.RegionID)
        packLLVector3(byteBuffer, this.EventData_Field.RegionPos)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.EventData_Field.Success = unpackBoolean(byteBuffer)
        this.EventData_Field.RegionID = unpackUUID(byteBuffer)
        this.EventData_Field.RegionPos = unpackLLVector3(byteBuffer)
    }
}
