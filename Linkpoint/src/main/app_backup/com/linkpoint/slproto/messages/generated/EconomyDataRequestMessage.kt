package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class EconomyDataRequestMessage : SLMessage() {
    val economyData: MutableList<EconomyDataBlock> = mutableListOf()

    data class EconomyDataBlock(
        var unused: Int = 0
    )


    override fun packPayload(buffer: ByteBuffer) {
        economyData.forEach { entry ->
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        // This message has an empty variable block in the template usually, handling as special case if needed
        // But typically it just has a zero byte for count if empty
        // If it has blocks but they are empty, we still iterate
        // For now, assuming standard behavior:
        // If variable block is "EconomyData", it has a count byte.
        // But the block itself might be empty in definition?
        // Let's assume it might have empty blocks.
        /*
        run {
            val count = unpackByte(buffer)
            economyData.clear()
            repeat(count) {
                val entry = EconomyDataBlock()
                economyData += entry
            }
        }
        */
    }

    override fun getMessageID(): Int = 0xFFFF0018.toInt()

    override fun getMessageName(): String = "EconomyDataRequest"
}
