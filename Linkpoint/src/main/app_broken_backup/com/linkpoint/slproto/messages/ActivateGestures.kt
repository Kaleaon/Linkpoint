package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ActivateGestures : SLMessage {
    AgentData AgentData_Field
    ArrayList<Data> Data_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        Int Flags
        UUID SessionID
    }

    class Data {
        UUID AssetID
        Int GestureFlags
        UUID ItemID
    }

    constructor() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        return (this.Data_Fields.size() * 36) + 41
    }

    fun Handle(sLMessageHandler: SLMessageHandler)  {
        sLMessageHandler.HandleActivateGestures(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 60)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.Flags)
        byteBuffer.put((this as Byte).Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packUUID(byteBuffer, data.ItemID)
            packUUID(byteBuffer, data.AssetID)
            packInt(byteBuffer, data.GestureFlags)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.Flags = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            Data data = Data()
            data.ItemID = unpackUUID(byteBuffer)
            data.AssetID = unpackUUID(byteBuffer)
            data.GestureFlags = unpackInt(byteBuffer)
            this.Data_Fields.add(data)
        }
    }
}
