package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class AvatarClassifiedReply : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<Data> Data_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID TargetID
    }

    @JvmStatic
    class Data {
        public UUID ClassifiedID
        public Byte[] Name
    }

    public AvatarClassifiedReply() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
        Int i = 37
        Iterator<T> it = this.Data_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((Data) it.next()).Name.length + 17 + i2
        }
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarClassifiedReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 42)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.TargetID)
        byteBuffer.put((Byte) this.Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packUUID(byteBuffer, data.ClassifiedID)
            packVariable(byteBuffer, data.Name, 1)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.TargetID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            Data data = Data()
            data.ClassifiedID = unpackUUID(byteBuffer)
            data.Name = unpackVariable(byteBuffer, 1)
            this.Data_Fields.add(data)
        }
    }
}
