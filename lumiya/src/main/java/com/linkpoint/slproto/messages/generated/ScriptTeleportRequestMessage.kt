package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer

class ScriptTeleportRequestMessage : SLMessage() {
    var objectName: ByteArray = ByteArray(0)
    var simName: ByteArray = ByteArray(0)
    var simPosition: LLVector3 = LLVector3()
    var lookAt: LLVector3 = LLVector3()
    val options: MutableList<OptionsBlock> = mutableListOf()

    data class OptionsBlock(
        var flags: Int = 0
    )


    override fun packPayload(buffer: ByteBuffer) {
        packVariable(buffer, objectName, 1)
        packVariable(buffer, simName, 1)
        simPosition.pack(buffer)
        lookAt.pack(buffer)
        require(options.size <= 0xFF) { "Options size exceeds 255 (" + options.size + ")" }
        packByte(buffer, options.size)
        options.forEach { entry ->
            packInt(buffer, entry.flags)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        objectName = unpackVariable(buffer, 1)
        simName = unpackVariable(buffer, 1)
        simPosition = LLVector3.unpack(buffer)
        lookAt = LLVector3.unpack(buffer)
        run {
            val count = unpackByte(buffer)
            options.clear()
            repeat(count) {
                val entry = OptionsBlock()
                entry.flags = unpackInt(buffer)
                options += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00C3.toInt()

    override fun getMessageName(): String = "ScriptTeleportRequest"
}
