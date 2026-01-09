package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class CompleteAuction : SLMessage {
    ArrayList<ParcelData> ParcelData_Fields = ArrayList<>()

    class ParcelData {
        UUID ParcelID
    }

    CompleteAuction() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return (this.ParcelData_Fields.size() * 16) + 5
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleCompleteAuction(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -25)
        byteBuffer.put((this as byte).ParcelData_Fields.size())
        for (ParcelData parcelData : this.ParcelData_Fields) {
            packUUID(byteBuffer, parcelData.ParcelID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ParcelData parcelData = ParcelData()
            parcelData.ParcelID = unpackUUID(byteBuffer)
            this.ParcelData_Fields.add(parcelData)
        }
    }
}
