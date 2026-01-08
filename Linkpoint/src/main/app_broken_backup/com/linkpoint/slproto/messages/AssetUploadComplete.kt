package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AssetUploadComplete : SLMessage {
    AssetBlock AssetBlock_Field = AssetBlock()

    class AssetBlock {
        Boolean Success
        Int Type
        UUID UUID
    }

    AssetUploadComplete() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 22
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleAssetUploadComplete(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 78)
        packUUID(byteBuffer, this.AssetBlock_Field.UUID)
        packByte(byteBuffer, (this as byte).AssetBlock_Field.Type)
        packBoolean(byteBuffer, this.AssetBlock_Field.Success)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AssetBlock_Field.UUID = unpackUUID(byteBuffer)
        this.AssetBlock_Field.Type = unpackByte(byteBuffer)
        this.AssetBlock_Field.Success = unpackBoolean(byteBuffer)
    }
}
