package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class TestMessage : SLMessage() {
    var test1: Int = 0
    val neighborBlock: MutableList<NeighborBlock> = MutableList(4) { NeighborBlock() }

    data class NeighborBlock(
        var test0: Int = 0,
        var test1: Int = 0,
        var test2: Int = 0
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packInt(buffer, test1)
        require(neighborBlock.size == 4) { "NeighborBlock must contain exactly 4 entries" }
        neighborBlock.forEach { entry ->
            packInt(buffer, entry.test0)
            packInt(buffer, entry.test1)
            packInt(buffer, entry.test2)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        test1 = unpackInt(buffer)
        neighborBlock.clear()
        repeat(4) {
            val entry = NeighborBlock()
            entry.test0 = unpackInt(buffer)
            entry.test1 = unpackInt(buffer)
            entry.test2 = unpackInt(buffer)
            neighborBlock += entry
        }
    }

    override fun getMessageID(): Int = 0xFFFF0001.toInt()

    override fun getMessageName(): String = "TestMessage"
}
