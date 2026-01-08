package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class AvatarPicksReply : SLMessage {
    AgentData AgentData_Field
    ArrayList<Data> Data_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID TargetID
    }

    class Data {
        UUID PickID
        ByteArray PickName
    }

    AvatarPicksReply() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        Int i = 37
        Iterator<T> it = this.Data_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((it as Data).next()).PickName.size + 17 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleAvatarPicksReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -78)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.TargetID)
        byteBuffer.put((this as byte).Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packUUID(byteBuffer, data.PickID)
            packVariable(byteBuffer, data.PickName, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.TargetID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            Data data = Data()
            data.PickID = unpackUUID(byteBuffer)
            data.PickName = unpackVariable(byteBuffer, 1)
            this.Data_Fields.add(data)
        }
    }
}
