package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator

class ObjectUpdateCompressed : SLMessage {
    ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()
    RegionData RegionData_Field

    class ObjectData {
        ByteArray Data
        Int UpdateFlags
    }

    class RegionData {
        Long RegionHandle
        Int TimeDilation
    }

    ObjectUpdateCompressed() {
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
            i = ((it as ObjectData).next()).Data.size + 6 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleObjectUpdateCompressed(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.put(Ascii.CR)
        packLong(byteBuffer, this.RegionData_Field.RegionHandle)
        packShort(byteBuffer, (this as Short).RegionData_Field.TimeDilation)
        byteBuffer.put((this as Byte).ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.UpdateFlags)
            packVariable(byteBuffer, objectData.Data, 2)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer)
        this.RegionData_Field.TimeDilation = unpackShort(byteBuffer) & 65535
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ObjectData objectData = ObjectData()
            objectData.UpdateFlags = unpackInt(byteBuffer)
            objectData.Data = unpackVariable(byteBuffer, 2)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
