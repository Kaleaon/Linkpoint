package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AgentIsNowWearing : SLMessage {
    var AgentData_Field: AgentData? = AgentData()
    var WearableData_Fields: ArrayList<WearableData>? = ArrayList()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class WearableData {
        var ItemID: UUID? = null
        var WearableType: Int = 0
    }

    constructor() {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return (WearableData_Fields?.size ?: 0) * 17 + 37
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAgentIsNowWearing(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(1.toByte())
        byteBuffer.put(127.toByte()) // Ascii.DEL
        packUUID(byteBuffer, AgentData_Field?.AgentID)
        packUUID(byteBuffer, AgentData_Field?.SessionID)
        
        val size = WearableData_Fields?.size ?: 0
        byteBuffer.put(size.toByte())
        
        WearableData_Fields?.forEach { item ->
            packUUID(byteBuffer, item.ItemID)
            packByte(byteBuffer, item.WearableType.toByte())
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        val agentData = AgentData_Field ?: AgentData()
        agentData.AgentID = unpackUUID(byteBuffer)
        agentData.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field = agentData
        
        val count = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until count) {
            val item = WearableData()
            item.ItemID = unpackUUID(byteBuffer)
            item.WearableType = unpackByte(byteBuffer).toInt() and 0xFF
            WearableData_Fields?.add(item)
        }
    }
}
