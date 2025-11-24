package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AgentCachedTextureResponse : SLMessage {
    var AgentData_Field: AgentData? = null
    var WearableData_Fields: ArrayList<WearableData>? = ArrayList()

    class AgentData {
        var AgentID: UUID? = null
        var SerialNum: Int = 0
        var SessionID: UUID? = null
    }

    class WearableData {
        var HostName: ByteArray? = null
        var TextureID: UUID? = null
        var TextureIndex: Int = 0
    }

    constructor() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    override fun CalcPayloadSize(): Int {
        var size = 41
        WearableData_Fields?.forEach { item ->
            size += (item.HostName?.size ?: 0) + 18
        }
        return size
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAgentCachedTextureResponse(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(1.toByte())
        byteBuffer.put((-127).toByte())
        packUUID(byteBuffer, AgentData_Field?.AgentID)
        packUUID(byteBuffer, AgentData_Field?.SessionID)
        packInt(byteBuffer, AgentData_Field?.SerialNum ?: 0)
        
        val count = WearableData_Fields?.size ?: 0
        byteBuffer.put(count.toByte())
        
        WearableData_Fields?.forEach { wearableData ->
            packUUID(byteBuffer, wearableData.TextureID)
            packByte(byteBuffer, wearableData.TextureIndex.toByte())
            packVariable(byteBuffer, wearableData.HostName, 1)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        val agentData = AgentData_Field ?: AgentData()
        agentData.AgentID = unpackUUID(byteBuffer)
        agentData.SessionID = unpackUUID(byteBuffer)
        agentData.SerialNum = unpackInt(byteBuffer)
        this.AgentData_Field = agentData
        
        val count = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until count) {
            val wearableData = WearableData()
            wearableData.TextureID = unpackUUID(byteBuffer)
            wearableData.TextureIndex = unpackByte(byteBuffer).toInt() and 0xFF
            wearableData.HostName = unpackVariable(byteBuffer, 1)
            WearableData_Fields?.add(wearableData)
        }
    }
}
