package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class CreateTrustedCircuit : SLMessage {
    var DataBlock_Field: DataBlock = DataBlock()

    class DataBlock {
        var Digest: ByteArray = ByteArray(0)
        var EndPointID: UUID = UUIDPool.ZeroUUID
    }

    init {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 52
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleCreateTrustedCircuit(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(1.toByte())
        byteBuffer.put((-120).toByte())
        packUUID(byteBuffer, this.DataBlock_Field.EndPointID)
        packFixed(byteBuffer, this.DataBlock_Field.Digest, 32)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.DataBlock_Field.EndPointID = unpackUUID(byteBuffer)
        this.DataBlock_Field.Digest = unpackFixed(byteBuffer, 32)
    }
}
