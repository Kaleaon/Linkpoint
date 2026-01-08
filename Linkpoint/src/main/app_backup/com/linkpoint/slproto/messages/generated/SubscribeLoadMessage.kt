package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SubscribeLoadMessage : SLMessage() {
    val unsubscribeLoad: MutableList<UnsubscribeLoadBlock> = mutableListOf()

    class UnsubscribeLoadBlock

    override fun packPayload(buffer: ByteBuffer) {
        unsubscribeLoad.forEach { entry ->
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            unsubscribeLoad.clear()
            repeat(count) {
                val entry = UnsubscribeLoadBlock()
                unsubscribeLoad += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0007.toInt()

    override fun getMessageName(): String = "SubscribeLoad"
}
