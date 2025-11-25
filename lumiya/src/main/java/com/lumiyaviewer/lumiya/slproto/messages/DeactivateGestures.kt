package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer
import java.util.ArrayList

class DeactivateGestures : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var Data_Fields: ArrayList<Data> = ArrayList()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
        var Flags: Int = 0
        var SessionID: UUID = UUIDPool.ZeroUUID
    }

    class Data {
        var GestureFlags: Int = 0
        var ItemID: UUID = UUIDPool.ZeroUUID
    }

    init {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return (this.Data_Fields.size * 20) + 41
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDeactivateGestures(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(1.toByte())
        byteBuffer.put(61.toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.Flags)
        byteBuffer.put(this.Data_Fields.size.toByte())
        for (data in this.Data_Fields) {
            packUUID(byteBuffer, data.ItemID)
            packInt(byteBuffer, data.GestureFlags)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.Flags = unpackInt(byteBuffer)
        val b = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until b) {
            val data = Data()
            data.ItemID = unpackUUID(byteBuffer)
            data.GestureFlags = unpackInt(byteBuffer)
            this.Data_Fields.add(data)
        }
    }
}
