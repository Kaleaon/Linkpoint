package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GroupNoticesListReply : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<Data> Data_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID GroupID
    }

    @JvmStatic
    class Data {
        public Int AssetType
        public ByteArray FromName
        public Boolean HasAttachment
        public UUID NoticeID
        public ByteArray Subject
        public Int Timestamp
    }

    public GroupNoticesListReply() {
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
            Data data = (Data) it.next()
            i = data.Subject.length + data.FromName.length + 22 + 2 + 1 + 1 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupNoticesListReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 59)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        byteBuffer.put((Byte) this.Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packUUID(byteBuffer, data.NoticeID)
            packInt(byteBuffer, data.Timestamp)
            packVariable(byteBuffer, data.FromName, 2)
            packVariable(byteBuffer, data.Subject, 2)
            packBoolean(byteBuffer, data.HasAttachment)
            packByte(byteBuffer, (Byte) data.AssetType)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            Data data = Data()
            data.NoticeID = unpackUUID(byteBuffer)
            data.Timestamp = unpackInt(byteBuffer)
            data.FromName = unpackVariable(byteBuffer, 2)
            data.Subject = unpackVariable(byteBuffer, 2)
            data.HasAttachment = unpackBoolean(byteBuffer)
            data.AssetType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.Data_Fields.add(data)
        }
    }
}
