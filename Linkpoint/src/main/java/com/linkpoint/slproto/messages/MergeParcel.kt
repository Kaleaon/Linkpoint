package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MergeParcel : SLMessage() {
    val MasterParcelData_Field = MasterParcelData()
    val SlaveParcelData_Fields = ArrayList<SlaveParcelData>()

    class MasterParcelData {
        var MasterID: UUID? = null
    }

    class SlaveParcelData {
        var SlaveID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = (SlaveParcelData_Fields.size * 16) + 21

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleMergeParcel(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-33).toByte())
        packUUID(buffer, MasterParcelData_Field.MasterID)
        buffer.put(SlaveParcelData_Fields.size.toByte())
        for (slaveData in SlaveParcelData_Fields) {
            packUUID(buffer, slaveData.SlaveID)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        MasterParcelData_Field.MasterID = unpackUUID(buffer)
        val count = buffer.get().toInt() and UnsignedBytes.MAX_VALUE.toInt()
        for (i in 0 until count) {
            val slaveData = SlaveParcelData()
            slaveData.SlaveID = unpackUUID(buffer)
            SlaveParcelData_Fields.add(slaveData)
        }
    }
}