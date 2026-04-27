package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AssetUploadRequest : SLMessage() {
    public AssetBlock AssetBlock_Field = AssetBlock()

    @JvmStatic
    class AssetBlock {
        public ByteArray AssetData
        public Boolean StoreLocal
        public Boolean Tempfile
        public UUID TransactionID
        public Int Type
    }

    public AssetUploadRequest() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.AssetBlock_Field.AssetData.length + 21 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAssetUploadRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 77)
        packUUID(byteBuffer, this.AssetBlock_Field.TransactionID)
        packByte(byteBuffer, (Byte) this.AssetBlock_Field.Type)
        packBoolean(byteBuffer, this.AssetBlock_Field.Tempfile)
        packBoolean(byteBuffer, this.AssetBlock_Field.StoreLocal)
        packVariable(byteBuffer, this.AssetBlock_Field.AssetData, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AssetBlock_Field.TransactionID = unpackUUID(byteBuffer)
        this.AssetBlock_Field.Type = unpackByte(byteBuffer)
        this.AssetBlock_Field.Tempfile = unpackBoolean(byteBuffer)
        this.AssetBlock_Field.StoreLocal = unpackBoolean(byteBuffer)
        this.AssetBlock_Field.AssetData = unpackVariable(byteBuffer, 2)
    }
}
