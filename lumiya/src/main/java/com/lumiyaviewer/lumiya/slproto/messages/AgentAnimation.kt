package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AgentAnimation : SLMessage {
    val AgentData_Field = AgentData()
    val AnimationList_Fields = ArrayList<AnimationList>()
    val PhysicalAvatarEventList_Fields = ArrayList<PhysicalAvatarEventList>()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class AnimationList {
        var AnimID: UUID? = null
        var StartAnim: Boolean = false
    }

    class PhysicalAvatarEventList {
        var TypeData: ByteArray? = null
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        var size = (this.AnimationList_Fields.size * 17) + 34 + 1
        for (event in this.PhysicalAvatarEventList_Fields) {
            size += (event.TypeData?.size ?: 0) + 1
        }
        return size
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleAgentAnimation(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.put(5.toByte())
        packUUID(buffer, this.AgentData_Field.AgentID)
        packUUID(buffer, this.AgentData_Field.SessionID)
        buffer.put(this.AnimationList_Fields.size.toByte())
        for (animationList in this.AnimationList_Fields) {
            packUUID(buffer, animationList.AnimID)
            packBoolean(buffer, animationList.StartAnim)
        }
        buffer.put(this.PhysicalAvatarEventList_Fields.size.toByte())
        for (physicalAvatarEventList in this.PhysicalAvatarEventList_Fields) {
            packVariable(buffer, physicalAvatarEventList.TypeData ?: ByteArray(0), 1)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(buffer)
        this.AgentData_Field.SessionID = unpackUUID(buffer)
        val b = buffer.get().toInt() and 0xFF
        for (i in 0 until b) {
            val animationList = AnimationList()
            animationList.AnimID = unpackUUID(buffer)
            animationList.StartAnim = unpackBoolean(buffer)
            this.AnimationList_Fields.add(animationList)
        }
        val b2 = buffer.get().toInt() and 0xFF
        for (i in 0 until b2) {
            val physicalAvatarEventList = PhysicalAvatarEventList()
            physicalAvatarEventList.TypeData = unpackVariable(buffer, 1)
            this.PhysicalAvatarEventList_Fields.add(physicalAvatarEventList)
        }
    }
}
