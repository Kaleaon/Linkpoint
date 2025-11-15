package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return 37
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleInventoryAssetResponse(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put(Ascii.ESC)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packUUID(byteBuffer, this.QueryData_Field.AssetID)
        packBoolean(byteBuffer, this.QueryData_Field.IsReadable)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.QueryData_Field.AssetID = unpackUUID(byteBuffer)
        this.QueryData_Field.IsReadable = unpackBoolean(byteBuffer)
    }
}
