package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class DerezContainer : SLMessage {
    var Data_Field: Data = Data()

    class Data {
        var Delete: Boolean = false
        var ObjectID: UUID = UUIDPool.ZeroUUID
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return 21
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDerezContainer(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(104.toByte())
        packUUID(byteBuffer, this.Data_Field.ObjectID)
        packBoolean(byteBuffer, this.Data_Field.Delete)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.Data_Field.ObjectID = unpackUUID(byteBuffer)
        this.Data_Field.Delete = unpackBoolean(byteBuffer)
    }
}
