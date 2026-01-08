package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AttachedSoundGainChange : SLMessage {
    var DataBlock_Field: DataBlock = DataBlock()

    class DataBlock {
        var Gain: Float = 0f
        var ObjectID: UUID? = null
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 22
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAttachedSoundGainChange(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put((-1).toByte())
        byteBuffer.put(14.toByte()) // Ascii.SO
        packUUID(byteBuffer, DataBlock_Field.ObjectID!!)
        packFloat(byteBuffer, DataBlock_Field.Gain)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        DataBlock_Field.ObjectID = unpackUUID(byteBuffer)
        DataBlock_Field.Gain = unpackFloat(byteBuffer)
    }
}
