package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return (this.SlaveParcelData_Fields.size() * 16) + 21
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMergeParcel(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -33)
        packUUID(byteBuffer, this.MasterParcelData_Field.MasterID)
        byteBuffer.put((Byte) this.SlaveParcelData_Fields.size())
        for (SlaveParcelData slaveParcelData : this.SlaveParcelData_Fields) {
            packUUID(byteBuffer, slaveParcelData.SlaveID)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.MasterParcelData_Field.MasterID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            SlaveParcelData slaveParcelData = SlaveParcelData()
            slaveParcelData.SlaveID = unpackUUID(byteBuffer)
            this.SlaveParcelData_Fields.add(slaveParcelData)
        }
    }
}
