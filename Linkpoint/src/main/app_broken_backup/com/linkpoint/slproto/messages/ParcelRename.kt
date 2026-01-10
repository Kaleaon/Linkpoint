package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class ParcelRename : SLMessage {
    ArrayList<ParcelData> ParcelData_Fields = ArrayList<>()

    class ParcelData {
        ByteArray NewName
        UUID ParcelID
    }

    ParcelRename() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        var i: Int = 5
        Iterator<T> it = this.ParcelData_Fields.iterator()
        while (true) {
            var i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((it as ParcelData).next()).NewName.size + 17 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleParcelRename(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -110)
        byteBuffer.put((this as Byte).ParcelData_Fields.size())
        for (ParcelData parcelData : this.ParcelData_Fields) {
            packUUID(byteBuffer, parcelData.ParcelID)
            packVariable(byteBuffer, parcelData.NewName, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ParcelData parcelData = ParcelData()
            parcelData.ParcelID = unpackUUID(byteBuffer)
            parcelData.NewName = unpackVariable(byteBuffer, 1)
            this.ParcelData_Fields.add(parcelData)
        }
    }
}
