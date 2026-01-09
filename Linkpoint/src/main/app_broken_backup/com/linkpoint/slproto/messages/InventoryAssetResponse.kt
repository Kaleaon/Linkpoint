package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class InventoryAssetResponse : SLMessage {
    QueryData QueryData_Field = QueryData()

    class QueryData {
        UUID AssetID
        Boolean IsReadable
        UUID QueryID
    }

    InventoryAssetResponse() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 37
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleInventoryAssetResponse(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put(Ascii.ESC)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packUUID(byteBuffer, this.QueryData_Field.AssetID)
        packBoolean(byteBuffer, this.QueryData_Field.IsReadable)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.QueryData_Field.AssetID = unpackUUID(byteBuffer)
        this.QueryData_Field.IsReadable = unpackBoolean(byteBuffer)
    }
}
