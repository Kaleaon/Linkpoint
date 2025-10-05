package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class InventoryAssetResponse : SLMessage() {
    public QueryData QueryData_Field = QueryData()

    @JvmStatic
    class QueryData {
        public UUID AssetID
        public Boolean IsReadable
        public UUID QueryID
    }

    public InventoryAssetResponse() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 37
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleInventoryAssetResponse(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put(Ascii.ESC)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packUUID(byteBuffer, this.QueryData_Field.AssetID)
        packBoolean(byteBuffer, this.QueryData_Field.IsReadable)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.QueryData_Field.AssetID = unpackUUID(byteBuffer)
        this.QueryData_Field.IsReadable = unpackBoolean(byteBuffer)
    }
}
