package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AssetUploadRequest : SLMessage {
    var AssetBlock_Field: AssetBlock = AssetBlock()

    class AssetBlock {
        var AssetData: ByteArray = ByteArray(0)
        var StoreLocal: Boolean = false
        var Tempfile: Boolean = false
        var TransactionID: UUID? = null
        var Type: Int = 0
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return AssetBlock_Field.AssetData.size + 21 + 4
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAssetUploadRequest(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(1.toByte())
        byteBuffer.put(77.toByte())
        packUUID(byteBuffer, AssetBlock_Field.TransactionID!!)
        packByte(byteBuffer, AssetBlock_Field.Type.toByte())
        packBoolean(byteBuffer, AssetBlock_Field.Tempfile)
        packBoolean(byteBuffer, AssetBlock_Field.StoreLocal)
        packVariable(byteBuffer, AssetBlock_Field.AssetData, 2)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        AssetBlock_Field.TransactionID = unpackUUID(byteBuffer)
        AssetBlock_Field.Type = unpackByte(byteBuffer).toInt()
        AssetBlock_Field.Tempfile = unpackBoolean(byteBuffer)
        AssetBlock_Field.StoreLocal = unpackBoolean(byteBuffer)
        AssetBlock_Field.AssetData = unpackVariable(byteBuffer, 2)
    }
}
