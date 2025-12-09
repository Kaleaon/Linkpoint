package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class EventLocationReply : SLMessage {
    EventData EventData_Field = EventData()
    QueryData QueryData_Field = QueryData()

    class EventData {
        UUID RegionID
        LLVector3 RegionPos
        Boolean Success
    }

    class QueryData {
        UUID QueryID
    }

    EventLocationReply() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 49
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleEventLocationReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 52)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packBoolean(byteBuffer, this.EventData_Field.Success)
        packUUID(byteBuffer, this.EventData_Field.RegionID)
        packLLVector3(byteBuffer, this.EventData_Field.RegionPos)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.EventData_Field.Success = unpackBoolean(byteBuffer)
        this.EventData_Field.RegionID = unpackUUID(byteBuffer)
        this.EventData_Field.RegionPos = unpackLLVector3(byteBuffer)
    }
}
