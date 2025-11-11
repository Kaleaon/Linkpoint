package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MeanCollisionAlertMessage : SLMessage() {
    val meanCollision: MutableList<MeanCollisionBlock> = mutableListOf()

    data class MeanCollisionBlock(
        var victim: UUID = UUID(0L, 0L),
        var perp: UUID = UUID(0L, 0L),
        var time: Int = 0,
        var mag: Float = 0f,
        var type: Int = 0
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        require(meanCollision.size <= 0xFF) { "MeanCollision size exceeds 255 (" + meanCollision.size + ")" }
        packByte(buffer, meanCollision.size)
        meanCollision.forEach { entry ->
            packUUID(buffer, entry.victim)
            packUUID(buffer, entry.perp)
            packInt(buffer, entry.time)
            packFloat(buffer, entry.mag)
            packByte(buffer, entry.type)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            meanCollision.clear()
            repeat(count) {
                val entry = MeanCollisionBlock()
                entry.victim = unpackUUID(buffer)
                entry.perp = unpackUUID(buffer)
                entry.time = unpackInt(buffer)
                entry.mag = unpackFloat(buffer)
                entry.type = unpackByte(buffer)
                meanCollision += entry
            }
        }
    }

    override fun getMessageID(): Int = 0x00000088

    override fun getMessageName(): String = "MeanCollisionAlert"
}
