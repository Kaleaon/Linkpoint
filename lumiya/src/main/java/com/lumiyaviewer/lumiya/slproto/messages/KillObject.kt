package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return (this.ObjectData_Fields.size() * 4) + 2
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleKillObject(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 16)
        byteBuffer.put((byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ID)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ObjectData objectData = ObjectData()
            objectData.ID = unpackInt(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
