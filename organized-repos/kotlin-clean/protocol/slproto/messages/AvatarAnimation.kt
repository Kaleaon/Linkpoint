package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class AvatarAnimation : SLMessage() {
    public ArrayList<AnimationList> AnimationList_Fields = ArrayList<>()
    public ArrayList<AnimationSourceList> AnimationSourceList_Fields = ArrayList<>()
    public ArrayList<PhysicalAvatarEventList> PhysicalAvatarEventList_Fields = ArrayList<>()
    public Sender Sender_Field

    @JvmStatic
    class AnimationList {
        public UUID AnimID
        public Int AnimSequenceID
    }

    @JvmStatic
    class AnimationSourceList {
        public UUID ObjectID
    }

    @JvmStatic
    class PhysicalAvatarEventList {
        public Byte[] TypeData
    }

    @JvmStatic
    class Sender {
        public UUID ID
    }

    public AvatarAnimation() {
        this.zeroCoded = false
        this.Sender_Field = Sender()
    }

    public Int CalcPayloadSize() {
        Int size = (this.AnimationList_Fields.size() * 20) + 18 + 1 + (this.AnimationSourceList_Fields.size() * 16) + 1
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
        sLMessageHandler.HandleAvatarAnimation(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.DC4)
        packUUID(byteBuffer, this.Sender_Field.ID)
        byteBuffer.put((Byte) this.AnimationList_Fields.size())
        for (AnimationList animationList : this.AnimationList_Fields) {
            packUUID(byteBuffer, animationList.AnimID)
            packInt(byteBuffer, animationList.AnimSequenceID)
        }
        byteBuffer.put((Byte) this.AnimationSourceList_Fields.size())
        for (AnimationSourceList animationSourceList : this.AnimationSourceList_Fields) {
            packUUID(byteBuffer, animationSourceList.ObjectID)
        }
        byteBuffer.put((Byte) this.PhysicalAvatarEventList_Fields.size())
        for (PhysicalAvatarEventList physicalAvatarEventList : this.PhysicalAvatarEventList_Fields) {
            packVariable(byteBuffer, physicalAvatarEventList.TypeData, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.Sender_Field.ID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            AnimationList animationList = AnimationList()
            animationList.AnimID = unpackUUID(byteBuffer)
            animationList.AnimSequenceID = unpackInt(byteBuffer)
            this.AnimationList_Fields.add(animationList)
        }
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            AnimationSourceList animationSourceList = AnimationSourceList()
            animationSourceList.ObjectID = unpackUUID(byteBuffer)
            this.AnimationSourceList_Fields.add(animationSourceList)
        }
        Byte b3 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i3 = 0; i3 < b3; i3++) {
            PhysicalAvatarEventList physicalAvatarEventList = PhysicalAvatarEventList()
            physicalAvatarEventList.TypeData = unpackVariable(byteBuffer, 1)
            this.PhysicalAvatarEventList_Fields.add(physicalAvatarEventList)
        }
    }
}
