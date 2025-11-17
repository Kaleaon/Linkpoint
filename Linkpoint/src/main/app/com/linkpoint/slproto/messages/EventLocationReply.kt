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

    Int CalcPayloadSize() {
        return 49
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEventLocationReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 52)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packBoolean(byteBuffer, this.EventData_Field.Success)
        packUUID(byteBuffer, this.EventData_Field.RegionID)
        packLLVector3(byteBuffer, this.EventData_Field.RegionPos)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.EventData_Field.Success = unpackBoolean(byteBuffer)
        this.EventData_Field.RegionID = unpackUUID(byteBuffer)
        this.EventData_Field.RegionPos = unpackLLVector3(byteBuffer)
    }
}
