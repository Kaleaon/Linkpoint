package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ParcelAuctions : SLMessage {
    ArrayList<ParcelData> ParcelData_Fields = ArrayList<>()

    class ParcelData {
        UUID ParcelID
        UUID WinnerID
    }

    ParcelAuctions() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return (this.ParcelData_Fields.size() * 32) + 5
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleParcelAuctions(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -22)
        byteBuffer.put((this as Byte).ParcelData_Fields.size())
        for (ParcelData parcelData : this.ParcelData_Fields) {
            packUUID(byteBuffer, parcelData.ParcelID)
            packUUID(byteBuffer, parcelData.WinnerID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ParcelData parcelData = ParcelData()
            parcelData.ParcelID = unpackUUID(byteBuffer)
            parcelData.WinnerID = unpackUUID(byteBuffer)
            this.ParcelData_Fields.add(parcelData)
        }
    }
}
