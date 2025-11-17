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

    Int CalcPayloadSize() {
        return 68
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRequestInventoryAsset(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put(Ascii.SUB)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packUUID(byteBuffer, this.QueryData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.OwnerID)
        packUUID(byteBuffer, this.QueryData_Field.ItemID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.QueryData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.OwnerID = unpackUUID(byteBuffer)
        this.QueryData_Field.ItemID = unpackUUID(byteBuffer)
    }
}
