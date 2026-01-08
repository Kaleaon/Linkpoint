package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class DenyTrustedCircuit : SLMessage {
    var DataBlock_Field: DataBlock = DataBlock()

    class DataBlock {
        var EndPointID: UUID = UUIDPool.ZeroUUID
    }

    init {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 20
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDenyTrustedCircuit(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(1.toByte())
        byteBuffer.put((-119).toByte())
        packUUID(byteBuffer, this.DataBlock_Field.EndPointID)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.DataBlock_Field.EndPointID = unpackUUID(byteBuffer)
    }
}
