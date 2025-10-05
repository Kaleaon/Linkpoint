package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelRename : SLMessage() {
    val ParcelData_Fields = ArrayList<ParcelData>()

    class ParcelData {
        var ParcelID: UUID? = null
        lateinit var NewName: ByteArray
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        var size = 5
        for (parcelData in ParcelData_Fields) {
            size += parcelData.NewName.size + 17
        }
        return size
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleParcelRename(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put((-110).toByte())
        buffer.put(ParcelData_Fields.size.toByte())
        for (parcelData in ParcelData_Fields) {
            packUUID(buffer, parcelData.ParcelID)
            packVariable(buffer, parcelData.NewName, 1)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        val count = buffer.get().toInt() and UnsignedBytes.MAX_VALUE.toInt()
        for (i in 0 until count) {
            val parcelData = ParcelData()
            parcelData.ParcelID = unpackUUID(buffer)
            parcelData.NewName = unpackVariable(buffer, 1)
            ParcelData_Fields.add(parcelData)
        }
    }
}