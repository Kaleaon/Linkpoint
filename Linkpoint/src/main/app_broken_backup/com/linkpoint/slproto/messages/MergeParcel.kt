package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class MergeParcel : SLMessage {
    MasterParcelData MasterParcelData_Field
    ArrayList<SlaveParcelData> SlaveParcelData_Fields = ArrayList<>()

    class MasterParcelData {
        UUID MasterID
    }

    class SlaveParcelData {
        UUID SlaveID
    }

    MergeParcel() {
        this.zeroCoded = false
        this.MasterParcelData_Field = MasterParcelData()
    }

    fun CalcPayloadSize(): Int {
        return (this.SlaveParcelData_Fields.size() * 16) + 21
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleMergeParcel(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -33)
        packUUID(byteBuffer, this.MasterParcelData_Field.MasterID)
        byteBuffer.put((this as Byte).SlaveParcelData_Fields.size())
        for (SlaveParcelData slaveParcelData : this.SlaveParcelData_Fields) {
            packUUID(byteBuffer, slaveParcelData.SlaveID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.MasterParcelData_Field.MasterID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            SlaveParcelData slaveParcelData = SlaveParcelData()
            slaveParcelData.SlaveID = unpackUUID(byteBuffer)
            this.SlaveParcelData_Fields.add(slaveParcelData)
        }
    }
}
