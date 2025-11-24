package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AgentCachedTexture : SLMessage {
    var AgentData_Field: AgentData? = null
    var WearableData_Fields: ArrayList<WearableData>? = ArrayList()

    class AgentData {
        var AgentID: UUID? = null
        var SerialNum: Int = 0
        var SessionID: UUID? = null
    }

    class WearableData {
        var ID: UUID? = null
        var TextureIndex: Int = 0
    }

    constructor() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    override fun CalcPayloadSize(): Int {
        val size = WearableData_Fields?.size ?: 0
        return (size * 17) + 41
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAgentCachedTexture(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(1.toByte())
        byteBuffer.put(Byte.MIN_VALUE)
        packUUID(byteBuffer, AgentData_Field?.AgentID)
        packUUID(byteBuffer, AgentData_Field?.SessionID)
        packInt(byteBuffer, AgentData_Field?.SerialNum ?: 0)
        
        val size = WearableData_Fields?.size ?: 0
        byteBuffer.put(size.toByte())
        
        WearableData_Fields?.forEach { wearableData ->
            packUUID(byteBuffer, wearableData.ID)
            packByte(byteBuffer, wearableData.TextureIndex.toByte())
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
            wearableData.ID = unpackUUID(byteBuffer)
            wearableData.TextureIndex = unpackByte(byteBuffer).toInt() and 0xFF
            WearableData_Fields?.add(wearableData)
        }
    }
}
