package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class NameValuePair : SLMessage {
    ArrayList<NameValueData> NameValueData_Fields = ArrayList<>()
    TaskData TaskData_Field

    class NameValueData {
        ByteArray NVPair
    }

    class TaskData {
        UUID ID
    }

    NameValuePair() {
        this.zeroCoded = false
        this.TaskData_Field = TaskData()
    }

    Int CalcPayloadSize() {
        Int i = 21
        Iterator<T> it = this.NameValueData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((NameValueData) it.next()).NVPair.length + 2 + i2
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleNameValuePair(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 73)
        packUUID(byteBuffer, this.TaskData_Field.ID)
        byteBuffer.put((Byte) this.NameValueData_Fields.size())
        for (NameValueData nameValueData : this.NameValueData_Fields) {
            packVariable(byteBuffer, nameValueData.NVPair, 2)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.TaskData_Field.ID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            NameValueData nameValueData = NameValueData()
            nameValueData.NVPair = unpackVariable(byteBuffer, 2)
            this.NameValueData_Fields.add(nameValueData)
        }
    }
}
