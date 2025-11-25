package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer
import java.util.ArrayList

class CopyInventoryItem : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var InventoryData_Fields: ArrayList<InventoryData> = ArrayList()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
        var SessionID: UUID = UUIDPool.ZeroUUID
    }

    class InventoryData {
        var CallbackID: Int = 0
        var NewFolderID: UUID = UUIDPool.ZeroUUID
        var NewName: ByteArray = ByteArray(0)
        var OldAgentID: UUID = UUIDPool.ZeroUUID
        var OldItemID: UUID = UUIDPool.ZeroUUID
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        var size = 32
        for (data in InventoryData_Fields) {
            size += data.NewName.size + 53
        }
        return size
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleCopyInventoryItem(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(29.toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        val count = this.InventoryData_Fields.size
        byteBuffer.put(count.toByte())
        for (data in this.InventoryData_Fields) {
            packInt(byteBuffer, data.CallbackID)
            packUUID(byteBuffer, data.OldAgentID)
            packUUID(byteBuffer, data.OldItemID)
            packUUID(byteBuffer, data.NewFolderID)
            packVariable(byteBuffer, data.NewName, 1)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        val count = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until count) {
            val data = InventoryData()
            data.CallbackID = unpackInt(byteBuffer)
            data.OldAgentID = unpackUUID(byteBuffer)
            data.OldItemID = unpackUUID(byteBuffer)
            data.NewFolderID = unpackUUID(byteBuffer)
            data.NewName = unpackVariable(byteBuffer, 1)
            this.InventoryData_Fields.add(data)
        }
    }
}
