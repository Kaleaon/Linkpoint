package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer

class ChangeInventoryItemFlags : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var InventoryData_Fields: ArrayList<InventoryData> = ArrayList()

    class AgentData {
        var AgentID: com.lumiyaviewer.lumiya.slproto.types.UUID = com.lumiyaviewer.lumiya.slproto.types.UUIDPool.ZeroUUID
        var SessionID: com.lumiyaviewer.lumiya.slproto.types.UUID = com.lumiyaviewer.lumiya.slproto.types.UUIDPool.ZeroUUID
    }

    class InventoryData {
        var ItemID: com.lumiyaviewer.lumiya.slproto.types.UUID = com.lumiyaviewer.lumiya.slproto.types.UUIDPool.ZeroUUID
        var Flags: Int = 0
    }

    override fun CalcPayloadSize(): Int {
        var size = 32
        for (i in InventoryData_Fields) {
            size += 20
        }
        return size
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // Not implemented for this message
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put(this.AgentData_Field.AgentID.data)
        byteBuffer.put(this.AgentData_Field.SessionID.data)
        val size = this.InventoryData_Fields.size
        byteBuffer.put(size.toByte())
        for (data in this.InventoryData_Fields) {
            byteBuffer.put(data.ItemID.data)
            byteBuffer.putInt(data.Flags)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = com.lumiyaviewer.lumiya.slproto.types.UUID(byteBuffer)
        this.AgentData_Field.SessionID = com.lumiyaviewer.lumiya.slproto.types.UUID(byteBuffer)
        val count = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until count) {
            val data = InventoryData()
            data.ItemID = com.lumiyaviewer.lumiya.slproto.types.UUID(byteBuffer)
            data.Flags = byteBuffer.getInt()
            this.InventoryData_Fields.add(data)
        }
    }
}
