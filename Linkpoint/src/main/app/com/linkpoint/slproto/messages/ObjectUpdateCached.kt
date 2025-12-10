package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList

class ObjectUpdateCached : SLMessage {
    ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()
    RegionData RegionData_Field

    class ObjectData {
        Int CRC
        Int ID
        Int UpdateFlags
    }

    class RegionData {
        Long RegionHandle
        Int TimeDilation
    }

    ObjectUpdateCached() {
        this.zeroCoded = false
        this.RegionData_Field = RegionData()
    }

    fun CalcPayloadSize(): Int {
        return (this.ObjectData_Fields.size() * 12) + 12
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleObjectUpdateCached(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.put(Ascii.SO)
        packLong(byteBuffer, this.RegionData_Field.RegionHandle)
        packShort(byteBuffer, (this as Short).RegionData_Field.TimeDilation)
        byteBuffer.put((this as Byte).ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ID)
            packInt(byteBuffer, objectData.CRC)
            packInt(byteBuffer, objectData.UpdateFlags)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer)
        this.RegionData_Field.TimeDilation = unpackShort(byteBuffer) & 65535
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ObjectData objectData = ObjectData()
            objectData.ID = unpackInt(byteBuffer)
            objectData.CRC = unpackInt(byteBuffer)
            objectData.UpdateFlags = unpackInt(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
