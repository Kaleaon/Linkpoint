package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ParcelOverlay : SLMessage() {
    val ParcelData_Field = ParcelData()

    class ParcelData {
        var SequenceID: Int = 0
        lateinit var Data: ByteArray
    }

    init {
        zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return ParcelData_Field.Data.size + 6 + 4
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleParcelOverlay(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-60).toByte())
        packInt(buffer, ParcelData_Field.SequenceID)
        packVariable(buffer, ParcelData_Field.Data, 2)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        ParcelData_Field.SequenceID = unpackInt(buffer)
        ParcelData_Field.Data = unpackVariable(buffer, 2)
    }
}