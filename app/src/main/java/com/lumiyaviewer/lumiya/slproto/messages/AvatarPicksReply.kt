package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
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
        byte[] PickName
    }

    AvatarPicksReply() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    Int CalcPayloadSize() {
        Int i = 37
        Iterator<T> it = this.Data_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((Data) it.next()).PickName.length + 17 + i2
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarPicksReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -78)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.TargetID)
        byteBuffer.put((byte) this.Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packUUID(byteBuffer, data.PickID)
            packVariable(byteBuffer, data.PickName, 1)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.TargetID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            Data data = Data()
            data.PickID = unpackUUID(byteBuffer)
            data.PickName = unpackVariable(byteBuffer, 1)
            this.Data_Fields.add(data)
        }
    }
}
