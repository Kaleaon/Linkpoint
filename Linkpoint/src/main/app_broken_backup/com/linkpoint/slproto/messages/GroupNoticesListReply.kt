package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GroupNoticesListReply : SLMessage {
    AgentData AgentData_Field
    ArrayList<Data> Data_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID GroupID
    }

    class Data {
        Int AssetType
        ByteArray FromName
        Boolean HasAttachment
        UUID NoticeID
        ByteArray Subject
        Int Timestamp
    }

    GroupNoticesListReply() {
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
            i = data.Subject.size + data.FromName.size + 22 + 2 + 1 + 1 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleGroupNoticesListReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 59)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        byteBuffer.put((this as byte).Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packUUID(byteBuffer, data.NoticeID)
            packInt(byteBuffer, data.Timestamp)
            packVariable(byteBuffer, data.FromName, 2)
            packVariable(byteBuffer, data.Subject, 2)
            packBoolean(byteBuffer, data.HasAttachment)
            packByte(byteBuffer, (data as byte).AssetType)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
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
