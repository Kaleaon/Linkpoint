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

    Int CalcPayloadSize() {
        return 22
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAssetUploadComplete(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 78)
        packUUID(byteBuffer, this.AssetBlock_Field.UUID)
        packByte(byteBuffer, (byte) this.AssetBlock_Field.Type)
        packBoolean(byteBuffer, this.AssetBlock_Field.Success)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AssetBlock_Field.UUID = unpackUUID(byteBuffer)
        this.AssetBlock_Field.Type = unpackByte(byteBuffer)
        this.AssetBlock_Field.Success = unpackBoolean(byteBuffer)
    }
}
