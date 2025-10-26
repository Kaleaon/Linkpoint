package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class AvatarPickerReply : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<Data> Data_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID QueryID
    }

    @JvmStatic
    class Data {
        public UUID AvatarID
        public ByteArray FirstName
        public ByteArray LastName
    }

    public AvatarPickerReply() {
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
            val data: Data = (Data) it.next()
            i = data.LastName.length + data.FirstName.length + 17 + 1 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarPickerReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.FS)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.QueryID)
        byteBuffer.put((Byte) this.Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packUUID(byteBuffer, data.AvatarID)
            packVariable(byteBuffer, data.FirstName, 1)
            packVariable(byteBuffer, data.LastName, 1)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.QueryID = unpackUUID(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val data: Data = Data()
            data.AvatarID = unpackUUID(byteBuffer)
            data.FirstName = unpackVariable(byteBuffer, 1)
            data.LastName = unpackVariable(byteBuffer, 1)
            this.Data_Fields.add(data)
        }
    }
}
