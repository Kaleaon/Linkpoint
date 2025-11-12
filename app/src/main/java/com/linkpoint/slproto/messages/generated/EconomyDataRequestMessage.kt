package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class EconomyDataRequestMessage : SLMessage() {
    val economyData: MutableList<EconomyDataBlock> = mutableListOf()

    data class EconomyDataBlock(
    )


    override fun packPayload(buffer: ByteBuffer) {
        economyData.forEach { entry ->
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            economyData.clear()
            repeat(count) {
                val entry = EconomyDataBlock()
                economyData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0018

    override fun getMessageName(): String = "EconomyDataRequest"
}
