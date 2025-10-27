package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator

class ImprovedTerseObjectUpdate : SLMessage() {
    public ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()
    public RegionData RegionData_Field

    @JvmStatic
    class ObjectData {
        public ByteArray Data
        public ByteArray TextureEntry
    }

    @JvmStatic
    class RegionData {
        public Long RegionHandle
        public Int TimeDilation
    }

    public ImprovedTerseObjectUpdate() {
        this.zeroCoded = false
        this.RegionData_Field = RegionData()
    }

    public fun CalcPayloadSize(): Int {
        val i: Int = 12
        val it: Iterator<T> = this.ObjectData_Fields.iterator()
        while (true) {
            val i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            val objectData: ObjectData = (ObjectData) it.next()
            i = objectData.TextureEntry.length + objectData.Data.length + 1 + 2 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleImprovedTerseObjectUpdate(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put((Byte) 15)
        packLong(byteBuffer, this.RegionData_Field.RegionHandle)
        packShort(byteBuffer, (Short) this.RegionData_Field.TimeDilation)
        byteBuffer.put((Byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packVariable(byteBuffer, objectData.Data, 1)
            packVariable(byteBuffer, objectData.TextureEntry, 2)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer)
        this.RegionData_Field.TimeDilation = unpackShort(byteBuffer) & 65535
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val objectData: ObjectData = ObjectData()
            objectData.Data = unpackVariable(byteBuffer, 1)
            objectData.TextureEntry = unpackVariable(byteBuffer, 2)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
