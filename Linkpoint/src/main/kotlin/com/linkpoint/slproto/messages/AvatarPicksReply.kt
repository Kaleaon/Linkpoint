package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class AvatarPicksReply : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<Data> Data_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID TargetID
    }

    @JvmStatic
    class Data {
        public UUID PickID
        public ByteArray PickName
    }

    public AvatarPicksReply() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    public fun CalcPayloadSize(): Int {
        val i: Int = 37
        val it: Iterator<T> = this.Data_Fields.iterator()
        while (true) {
            val i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((Data) it.next()).PickName.length + 17 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarPicksReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -78)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.TargetID)
        byteBuffer.put((Byte) this.Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packUUID(byteBuffer, data.PickID)
            packVariable(byteBuffer, data.PickName, 1)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.TargetID = unpackUUID(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val data: Data = Data()
            data.PickID = unpackUUID(byteBuffer)
            data.PickName = unpackVariable(byteBuffer, 1)
            this.Data_Fields.add(data)
        }
    }
}
