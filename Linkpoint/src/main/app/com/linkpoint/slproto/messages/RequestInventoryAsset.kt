package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RequestInventoryAsset : SLMessage {
    QueryData QueryData_Field = QueryData()

    class QueryData {
        UUID AgentID
        UUID ItemID
        UUID OwnerID
        UUID QueryID
    }

    RequestInventoryAsset() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 68
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleRequestInventoryAsset(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put(Ascii.SUB)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packUUID(byteBuffer, this.QueryData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.OwnerID)
        packUUID(byteBuffer, this.QueryData_Field.ItemID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.QueryData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.OwnerID = unpackUUID(byteBuffer)
        this.QueryData_Field.ItemID = unpackUUID(byteBuffer)
    }
}
