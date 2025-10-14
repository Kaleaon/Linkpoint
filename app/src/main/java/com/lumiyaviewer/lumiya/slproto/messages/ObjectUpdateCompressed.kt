package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator

class ObjectUpdateCompressed : SLMessage {
    ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()
    RegionData RegionData_Field

    class ObjectData {
        Byte[] Data
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

    Int CalcPayloadSize() {
        Int i = 12
        Iterator<T> it = this.ObjectData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((ObjectData) it.next()).Data.length + 6 + i2
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectUpdateCompressed(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.CR)
        packLong(byteBuffer, this.RegionData_Field.RegionHandle)
        packShort(byteBuffer, (Short) this.RegionData_Field.TimeDilation)
        byteBuffer.put((Byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.UpdateFlags)
            packVariable(byteBuffer, objectData.Data, 2)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer)
        this.RegionData_Field.TimeDilation = unpackShort(byteBuffer) & 65535
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ObjectData objectData = ObjectData()
            objectData.UpdateFlags = unpackInt(byteBuffer)
            objectData.Data = unpackVariable(byteBuffer, 2)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
