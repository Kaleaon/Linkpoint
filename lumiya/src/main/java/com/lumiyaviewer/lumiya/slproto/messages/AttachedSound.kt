package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AttachedSound : SLMessage {
    var DataBlock_Field: DataBlock = DataBlock()

    class DataBlock {
        var Flags: Int = 0
        var Gain: Float = 0f
        var ObjectID: UUID? = null
        var OwnerID: UUID? = null
        var SoundID: UUID? = null
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 55
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAttachedSound(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put((-1).toByte())
        byteBuffer.put(13.toByte()) // Ascii.CR
        packUUID(byteBuffer, DataBlock_Field.SoundID!!)
        packUUID(byteBuffer, DataBlock_Field.ObjectID!!)
        packUUID(byteBuffer, DataBlock_Field.OwnerID!!)
        packFloat(byteBuffer, DataBlock_Field.Gain)
        packByte(byteBuffer, DataBlock_Field.Flags.toByte())
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        DataBlock_Field.SoundID = unpackUUID(byteBuffer)
        DataBlock_Field.ObjectID = unpackUUID(byteBuffer)
        DataBlock_Field.OwnerID = unpackUUID(byteBuffer)
        DataBlock_Field.Gain = unpackFloat(byteBuffer)
        DataBlock_Field.Flags = (unpackByte(byteBuffer).toInt() and 0xFF)
    }
}
