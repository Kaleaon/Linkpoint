package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AssetUploadComplete : SLMessage {
    var AssetBlock_Field: AssetBlock = AssetBlock()

    class AssetBlock {
        var Success: Boolean = false
        var Type: Int = 0
        var UUID: UUID? = null
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 22
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAssetUploadComplete(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(1.toByte())
        byteBuffer.put(78.toByte())
        packUUID(byteBuffer, AssetBlock_Field.UUID!!)
        packByte(byteBuffer, AssetBlock_Field.Type.toByte())
        packBoolean(byteBuffer, AssetBlock_Field.Success)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        AssetBlock_Field.UUID = unpackUUID(byteBuffer)
        AssetBlock_Field.Type = unpackByte(byteBuffer).toInt()
        AssetBlock_Field.Success = unpackBoolean(byteBuffer)
    }
}
