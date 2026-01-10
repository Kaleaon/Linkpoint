package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class AvatarPickerReply : SLMessage {
    AgentData AgentData_Field
    ArrayList<Data> Data_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID QueryID
    }

    class Data {
        UUID AvatarID
        ByteArray FirstName
        ByteArray LastName
    }

    AvatarPickerReply() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        var i: Int = 37
        Iterator<T> it = this.Data_Fields.iterator()
        while (true) {
            var i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            Data data = (it as Data).next()
            i = data.LastName.size + data.FirstName.size + 17 + 1 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleAvatarPickerReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put(Ascii.FS)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.QueryID)
        byteBuffer.put((this as byte).Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packUUID(byteBuffer, data.AvatarID)
            packVariable(byteBuffer, data.FirstName, 1)
            packVariable(byteBuffer, data.LastName, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.QueryID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            Data data = Data()
            data.AvatarID = unpackUUID(byteBuffer)
            data.FirstName = unpackVariable(byteBuffer, 1)
            data.LastName = unpackVariable(byteBuffer, 1)
            this.Data_Fields.add(data)
        }
    }
}
