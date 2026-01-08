package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AvatarAnimation : SLMessage {
    var AnimationList_Fields: ArrayList<AnimationList>? = ArrayList()
    var AnimationSourceList_Fields: ArrayList<AnimationSourceList>? = ArrayList()
    var PhysicalAvatarEventList_Fields: ArrayList<PhysicalAvatarEventList>? = ArrayList()
    var Sender_Field: Sender? = null

    class AnimationList {
        var AnimID: UUID? = null
        var AnimSequenceID: Int = 0
    }

    class AnimationSourceList {
        var ObjectID: UUID? = null
    }

    class PhysicalAvatarEventList {
        var TypeData: ByteArray? = null
    }

    class Sender {
        var ID: UUID? = null
    }

    constructor() {
        this.zeroCoded = false
        this.Sender_Field = Sender()
    }

    override fun CalcPayloadSize(): Int {
        var size = (AnimationList_Fields?.size ?: 0) * 20 + 18 + 1 + (AnimationSourceList_Fields?.size ?: 0) * 16 + 1
        PhysicalAvatarEventList_Fields?.forEach {
            size += (it.TypeData?.size ?: 0) + 1
        }
        return size
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarAnimation(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put(0x14.toByte()) // DC4
        packUUID(byteBuffer, Sender_Field?.ID)
        byteBuffer.put((AnimationList_Fields?.size ?: 0).toByte())
        AnimationList_Fields?.forEach {
            packUUID(byteBuffer, it.AnimID)
            packInt(byteBuffer, it.AnimSequenceID)
        }
        byteBuffer.put((AnimationSourceList_Fields?.size ?: 0).toByte())
        AnimationSourceList_Fields?.forEach {
            packUUID(byteBuffer, it.ObjectID)
        }
        byteBuffer.put((PhysicalAvatarEventList_Fields?.size ?: 0).toByte())
        PhysicalAvatarEventList_Fields?.forEach {
            packVariable(byteBuffer, it.TypeData, 1)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        val sender = Sender_Field ?: Sender()
        sender.ID = unpackUUID(byteBuffer)
        this.Sender_Field = sender

        val count1 = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until count1) {
            val item = AnimationList()
            item.AnimID = unpackUUID(byteBuffer)
            item.AnimSequenceID = unpackInt(byteBuffer)
            AnimationList_Fields?.add(item)
        }

        val count2 = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until count2) {
            val item = AnimationSourceList()
            item.ObjectID = unpackUUID(byteBuffer)
            AnimationSourceList_Fields?.add(item)
        }

        val count3 = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until count3) {
            val item = PhysicalAvatarEventList()
            item.TypeData = unpackVariable(byteBuffer, 1)
            PhysicalAvatarEventList_Fields?.add(item)
        }
    }
}
