package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class AgentAnimation : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<AnimationList> AnimationList_Fields = ArrayList<>()
    public ArrayList<PhysicalAvatarEventList> PhysicalAvatarEventList_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class AnimationList {
        public UUID AnimID
        public Boolean StartAnim
    }

    @JvmStatic
    class PhysicalAvatarEventList {
        public Byte[] TypeData
    }

    public AgentAnimation() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
        Int size = (this.AnimationList_Fields.size() * 17) + 34 + 1
        Iterator<T> it = this.PhysicalAvatarEventList_Fields.iterator()
        while (true) {
            Int i = size
            if (!it.hasNext()) {
                return i
            }
            size = ((PhysicalAvatarEventList) it.next()).TypeData.length + 1 + i
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentAnimation(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) 5)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((Byte) this.AnimationList_Fields.size())
        for (AnimationList animationList : this.AnimationList_Fields) {
            packUUID(byteBuffer, animationList.AnimID)
            packBoolean(byteBuffer, animationList.StartAnim)
        }
        byteBuffer.put((Byte) this.PhysicalAvatarEventList_Fields.size())
        for (PhysicalAvatarEventList physicalAvatarEventList : this.PhysicalAvatarEventList_Fields) {
            packVariable(byteBuffer, physicalAvatarEventList.TypeData, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            AnimationList animationList = AnimationList()
            animationList.AnimID = unpackUUID(byteBuffer)
            animationList.StartAnim = unpackBoolean(byteBuffer)
            this.AnimationList_Fields.add(animationList)
        }
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            PhysicalAvatarEventList physicalAvatarEventList = PhysicalAvatarEventList()
            physicalAvatarEventList.TypeData = unpackVariable(byteBuffer, 1)
            this.PhysicalAvatarEventList_Fields.add(physicalAvatarEventList)
        }
    }
}
