package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class AvatarAnimation : SLMessage {
    ArrayList<AnimationList> AnimationList_Fields = ArrayList<>()
    ArrayList<AnimationSourceList> AnimationSourceList_Fields = ArrayList<>()
    ArrayList<PhysicalAvatarEventList> PhysicalAvatarEventList_Fields = ArrayList<>()
    Sender Sender_Field

    class AnimationList {
        UUID AnimID
        Int AnimSequenceID
    }

    class AnimationSourceList {
        UUID ObjectID
    }

    class PhysicalAvatarEventList {
        ByteArray TypeData
    }

    class Sender {
        UUID ID
    }

    AvatarAnimation() {
        this.zeroCoded = false
        this.Sender_Field = Sender()
    }

    fun CalcPayloadSize(): Int {
        var size: Int = (this.AnimationList_Fields.size() * 20) + 18 + 1 + (this.AnimationSourceList_Fields.size() * 16) + 1
        Iterator<T> it = this.PhysicalAvatarEventList_Fields.iterator()
        while (true) {
            var i: Int = size
            if (!it.hasNext()) {
                return i
            }
            size = ((it as PhysicalAvatarEventList).next()).TypeData.size + 1 + i
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleAvatarAnimation(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.put(Ascii.DC4)
        packUUID(byteBuffer, this.Sender_Field.ID)
        byteBuffer.put((this as byte).AnimationList_Fields.size())
        for (AnimationList animationList : this.AnimationList_Fields) {
            packUUID(byteBuffer, animationList.AnimID)
            packInt(byteBuffer, animationList.AnimSequenceID)
        }
        byteBuffer.put((this as byte).AnimationSourceList_Fields.size())
        for (AnimationSourceList animationSourceList : this.AnimationSourceList_Fields) {
            packUUID(byteBuffer, animationSourceList.ObjectID)
        }
        byteBuffer.put((this as byte).PhysicalAvatarEventList_Fields.size())
        for (PhysicalAvatarEventList physicalAvatarEventList : this.PhysicalAvatarEventList_Fields) {
            packVariable(byteBuffer, physicalAvatarEventList.TypeData, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.Sender_Field.ID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            AnimationList animationList = AnimationList()
            animationList.AnimID = unpackUUID(byteBuffer)
            animationList.AnimSequenceID = unpackInt(byteBuffer)
            this.AnimationList_Fields.add(animationList)
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i2 in 0 until b2) {
            AnimationSourceList animationSourceList = AnimationSourceList()
            animationSourceList.ObjectID = unpackUUID(byteBuffer)
            this.AnimationSourceList_Fields.add(animationSourceList)
        }
        byte b3 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i3 in 0 until b3) {
            PhysicalAvatarEventList physicalAvatarEventList = PhysicalAvatarEventList()
            physicalAvatarEventList.TypeData = unpackVariable(byteBuffer, 1)
            this.PhysicalAvatarEventList_Fields.add(physicalAvatarEventList)
        }
    }
}
