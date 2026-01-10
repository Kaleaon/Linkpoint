package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class DeactivateGestures : SLMessage {
    AgentData AgentData_Field
    ArrayList<Data> Data_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        Int Flags
        UUID SessionID
    }

    class Data {
        Int GestureFlags
        UUID ItemID
    }

    DeactivateGestures() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        return (this.Data_Fields.size() * 20) + 41
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleDeactivateGestures(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 61)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.Flags)
        byteBuffer.put((this as byte).Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packUUID(byteBuffer, data.ItemID)
            packInt(byteBuffer, data.GestureFlags)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.Flags = unpackInt(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            Data data = Data()
            data.ItemID = unpackUUID(byteBuffer)
            data.GestureFlags = unpackInt(byteBuffer)
            this.Data_Fields.add(data)
        }
    }
}
