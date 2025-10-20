package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ParcelSales : SLMessage() {
    public ArrayList<ParcelData> ParcelData_Fields = ArrayList<>()

    @JvmStatic
    class ParcelData {
        public UUID BuyerID
        public UUID ParcelID
    }

    public ParcelSales() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return (this.ParcelData_Fields.size() * 32) + 5
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelSales(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -30)
        byteBuffer.put((Byte) this.ParcelData_Fields.size())
        for (ParcelData parcelData : this.ParcelData_Fields) {
            packUUID(byteBuffer, parcelData.ParcelID)
            packUUID(byteBuffer, parcelData.BuyerID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ParcelData parcelData = ParcelData()
            parcelData.ParcelID = unpackUUID(byteBuffer)
            parcelData.BuyerID = unpackUUID(byteBuffer)
            this.ParcelData_Fields.add(parcelData)
        }
    }
}
