package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList

class KillObject : SLMessage {
    ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    class ObjectData {
        Int ID
    }

    KillObject() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return (this.ObjectData_Fields.size() * 4) + 2
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleKillObject(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.put((byte) 16)
        byteBuffer.put((this as byte).ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ObjectData objectData = ObjectData()
            objectData.ID = unpackInt(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
