package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
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

    fun CalcPayloadSize(): Int {
        var i: Int = 21
        Iterator<T> it = this.NameValueData_Fields.iterator()
        while (true) {
            var i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((it as NameValueData).next()).NVPair.size + 2 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleNameValuePair(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 73)
        packUUID(byteBuffer, this.TaskData_Field.ID)
        byteBuffer.put((this as Byte).NameValueData_Fields.size())
        for (NameValueData nameValueData : this.NameValueData_Fields) {
            packVariable(byteBuffer, nameValueData.NVPair, 2)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.TaskData_Field.ID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            NameValueData nameValueData = NameValueData()
            nameValueData.NVPair = unpackVariable(byteBuffer, 2)
            this.NameValueData_Fields.add(nameValueData)
        }
    }
}
