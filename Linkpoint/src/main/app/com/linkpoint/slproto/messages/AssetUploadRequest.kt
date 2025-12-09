package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AssetUploadRequest : SLMessage {
    AssetBlock AssetBlock_Field = AssetBlock()

    class AssetBlock {
        ByteArray AssetData
        Boolean StoreLocal
        Boolean Tempfile
        UUID TransactionID
        Int Type
    }

    AssetUploadRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.AssetBlock_Field.AssetData.size + 21 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleAssetUploadRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 77)
        packUUID(byteBuffer, this.AssetBlock_Field.TransactionID)
        packByte(byteBuffer, (this as byte).AssetBlock_Field.Type)
        packBoolean(byteBuffer, this.AssetBlock_Field.Tempfile)
        packBoolean(byteBuffer, this.AssetBlock_Field.StoreLocal)
        packVariable(byteBuffer, this.AssetBlock_Field.AssetData, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AssetBlock_Field.TransactionID = unpackUUID(byteBuffer)
        this.AssetBlock_Field.Type = unpackByte(byteBuffer)
        this.AssetBlock_Field.Tempfile = unpackBoolean(byteBuffer)
        this.AssetBlock_Field.StoreLocal = unpackBoolean(byteBuffer)
        this.AssetBlock_Field.AssetData = unpackVariable(byteBuffer, 2)
    }
}
