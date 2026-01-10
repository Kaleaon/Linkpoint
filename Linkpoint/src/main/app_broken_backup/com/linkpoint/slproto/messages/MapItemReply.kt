package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class MapItemReply : SLMessage {
    AgentData AgentData_Field
    ArrayList<Data> Data_Fields = ArrayList<>()
    RequestData RequestData_Field

    class AgentData {
        UUID AgentID
        Int Flags
    }

    class Data {
        Int Extra
        Int Extra2
        UUID ID
        ByteArray Name
        Int X
        Int Y
    }

    class RequestData {
        Int ItemType
    }

    MapItemReply() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
        this.RequestData_Field = RequestData()
    }

    fun CalcPayloadSize(): Int {
        var i: Int = 29
        Iterator<T> it = this.Data_Fields.iterator()
        while (true) {
            var i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((it as Data).next()).Name.size + 33 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleMapItemReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -101)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packInt(byteBuffer, this.AgentData_Field.Flags)
        packInt(byteBuffer, this.RequestData_Field.ItemType)
        byteBuffer.put((this as Byte).Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packInt(byteBuffer, data.X)
            packInt(byteBuffer, data.Y)
            packUUID(byteBuffer, data.ID)
            packInt(byteBuffer, data.Extra)
            packInt(byteBuffer, data.Extra2)
            packVariable(byteBuffer, data.Name, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.Flags = unpackInt(byteBuffer)
        this.RequestData_Field.ItemType = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            Data data = Data()
            data.X = unpackInt(byteBuffer)
            data.Y = unpackInt(byteBuffer)
            data.ID = unpackUUID(byteBuffer)
            data.Extra = unpackInt(byteBuffer)
            data.Extra2 = unpackInt(byteBuffer)
            data.Name = unpackVariable(byteBuffer, 1)
            this.Data_Fields.add(data)
        }
    }
}
