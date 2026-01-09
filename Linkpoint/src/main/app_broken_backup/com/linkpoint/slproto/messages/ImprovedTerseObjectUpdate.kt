package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator

class ImprovedTerseObjectUpdate : SLMessage {
    ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()
    RegionData RegionData_Field

    class ObjectData {
        ByteArray Data
        ByteArray TextureEntry
    }

    class RegionData {
        Long RegionHandle
        Int TimeDilation
    }

    ImprovedTerseObjectUpdate() {
        this.zeroCoded = false
        this.RegionData_Field = RegionData()
    }

    fun CalcPayloadSize(): Int {
        var i: Int = 12
        Iterator<T> it = this.ObjectData_Fields.iterator()
        while (true) {
            var i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            ObjectData objectData = (it as ObjectData).next()
            i = objectData.TextureEntry.size + objectData.Data.size + 1 + 2 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleImprovedTerseObjectUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.put((byte) 15)
        packLong(byteBuffer, this.RegionData_Field.RegionHandle)
        packShort(byteBuffer, (this as short).RegionData_Field.TimeDilation)
        byteBuffer.put((this as byte).ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packVariable(byteBuffer, objectData.Data, 1)
            packVariable(byteBuffer, objectData.TextureEntry, 2)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer)
        this.RegionData_Field.TimeDilation = unpackShort(byteBuffer) & 65535
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ObjectData objectData = ObjectData()
            objectData.Data = unpackVariable(byteBuffer, 1)
            objectData.TextureEntry = unpackVariable(byteBuffer, 2)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
