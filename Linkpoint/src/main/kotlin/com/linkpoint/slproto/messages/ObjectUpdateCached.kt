package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList

class ObjectUpdateCached : SLMessage() {
    public ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()
    public RegionData RegionData_Field

    @JvmStatic
    class ObjectData {
        public Int CRC
        public Int ID
        public Int UpdateFlags
    }

    @JvmStatic
    class RegionData {
        public Long RegionHandle
        public Int TimeDilation
    }

    public ObjectUpdateCached() {
        this.zeroCoded = false
        this.RegionData_Field = RegionData()
    }

    public fun CalcPayloadSize(): Int {
        return (this.ObjectData_Fields.size() * 12) + 12
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleObjectUpdateCached(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put(Ascii.SO)
        packLong(byteBuffer, this.RegionData_Field.RegionHandle)
        packShort(byteBuffer, (Short) this.RegionData_Field.TimeDilation)
        byteBuffer.put((Byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ID)
            packInt(byteBuffer, objectData.CRC)
            packInt(byteBuffer, objectData.UpdateFlags)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer)
        this.RegionData_Field.TimeDilation = unpackShort(byteBuffer) & 65535
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val objectData: ObjectData = ObjectData()
            objectData.ID = unpackInt(byteBuffer)
            objectData.CRC = unpackInt(byteBuffer)
            objectData.UpdateFlags = unpackInt(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
