package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class AgentAnimation : SLMessage {
    AgentData AgentData_Field
    ArrayList<AnimationList> AnimationList_Fields = ArrayList<>()
    ArrayList<PhysicalAvatarEventList> PhysicalAvatarEventList_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class AnimationList {
        UUID AnimID
        Boolean StartAnim
    }

    class PhysicalAvatarEventList {
        ByteArray TypeData
    }

    constructor() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        Int size = (this.AnimationList_Fields.size() * 17) + 34 + 1
        Iterator<T> it = this.PhysicalAvatarEventList_Fields.iterator()
        while (true) {
            Int i = size
            if (!it.hasNext()) {
                return i
            }
            size = ((it as PhysicalAvatarEventList).next()).TypeData.size + 1 + i
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler): Unit {
        sLMessageHandler.HandleAgentAnimation(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer): Unit {
        byteBuffer.put((Byte) 5)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((this as Byte).AnimationList_Fields.size())
        for (AnimationList animationList : this.AnimationList_Fields) {
            packUUID(byteBuffer, animationList.AnimID)
            packBoolean(byteBuffer, animationList.StartAnim)
        }
        byteBuffer.put((this as Byte).PhysicalAvatarEventList_Fields.size())
        for (PhysicalAvatarEventList physicalAvatarEventList : this.PhysicalAvatarEventList_Fields) {
            packVariable(byteBuffer, physicalAvatarEventList.TypeData, 1)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            AnimationList animationList = AnimationList()
            animationList.AnimID = unpackUUID(byteBuffer)
            animationList.StartAnim = unpackBoolean(byteBuffer)
            this.AnimationList_Fields.add(animationList)
        }
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i2 in 0 until b2) {
            PhysicalAvatarEventList physicalAvatarEventList = PhysicalAvatarEventList()
            physicalAvatarEventList.TypeData = unpackVariable(byteBuffer, 1)
            this.PhysicalAvatarEventList_Fields.add(physicalAvatarEventList)
        }
    }
}
