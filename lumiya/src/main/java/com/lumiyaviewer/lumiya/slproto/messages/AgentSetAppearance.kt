package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AgentSetAppearance : SLMessage {
    var AgentData_Field: AgentData? = AgentData()
    var ObjectData_Field: ObjectData? = ObjectData()
    var VisualParam_Fields: ArrayList<VisualParam>? = ArrayList()
    var WearableData_Fields: ArrayList<WearableData>? = ArrayList()

    class AgentData {
        var AgentID: UUID? = null
        var SerialNum: Int = 0
        var SessionID: UUID? = null
        var Size: LLVector3? = LLVector3()
    }

    class ObjectData {
        var TextureEntry: ByteArray? = null
    }

    class VisualParam {
        var ParamValue: Int = 0
    }

    class WearableData {
        var CacheID: UUID? = null
        var TextureIndex: Int = 0
    }

    constructor() {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return (WearableData_Fields?.size ?: 0) * 17 + 53 + (ObjectData_Field?.TextureEntry?.size ?: 0) + 2 + 1 + (VisualParam_Fields?.size ?: 0) * 1
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAgentSetAppearance(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(84.toByte())
        packUUID(byteBuffer, AgentData_Field?.AgentID)
        packUUID(byteBuffer, AgentData_Field?.SessionID)
        packInt(byteBuffer, AgentData_Field?.SerialNum ?: 0)
        packLLVector3(byteBuffer, AgentData_Field?.Size)
        
        val wearableSize = WearableData_Fields?.size ?: 0
        byteBuffer.put(wearableSize.toByte())
        
        WearableData_Fields?.forEach { item ->
            packUUID(byteBuffer, item.CacheID)
            packByte(byteBuffer, item.TextureIndex.toByte())
        }
        
        packVariable(byteBuffer, ObjectData_Field?.TextureEntry, 2)
        
        val visualParamSize = VisualParam_Fields?.size ?: 0
        byteBuffer.put(visualParamSize.toByte())
        
        VisualParam_Fields?.forEach { item ->
            packByte(byteBuffer, item.ParamValue.toByte())
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        val agentData = AgentData_Field ?: AgentData()
        agentData.AgentID = unpackUUID(byteBuffer)
        agentData.SessionID = unpackUUID(byteBuffer)
        agentData.SerialNum = unpackInt(byteBuffer)
        agentData.Size = unpackLLVector3(byteBuffer)
        this.AgentData_Field = agentData
        
        val wearableCount = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until wearableCount) {
            val item = WearableData()
            item.CacheID = unpackUUID(byteBuffer)
            item.TextureIndex = unpackByte(byteBuffer).toInt() and 0xFF
            WearableData_Fields?.add(item)
        }
        
        val objectData = ObjectData_Field ?: ObjectData()
        objectData.TextureEntry = unpackVariable(byteBuffer, 2)
        this.ObjectData_Field = objectData
        
        val visualParamCount = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until visualParamCount) {
            val item = VisualParam()
            item.ParamValue = unpackByte(byteBuffer).toInt() and 0xFF
            VisualParam_Fields?.add(item)
        }
    }
}
