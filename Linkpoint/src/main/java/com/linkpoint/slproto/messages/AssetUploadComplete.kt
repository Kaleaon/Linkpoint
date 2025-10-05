package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AssetUploadComplete : SLMessage() {
    val AssetBlock_Field = AssetBlock()

    class AssetBlock {
        var UUID: UUID? = null
        var Type: Int = 0
        var Success: Boolean = false
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 22

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleAssetUploadComplete(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(78.toByte())
        packUUID(buffer, AssetBlock_Field.UUID)
        packByte(buffer, AssetBlock_Field.Type.toByte())
        packBoolean(buffer, AssetBlock_Field.Success)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AssetBlock_Field.UUID = unpackUUID(buffer)
        AssetBlock_Field.Type = unpackByte(buffer).toInt()
        AssetBlock_Field.Success = unpackBoolean(buffer)
    }
}