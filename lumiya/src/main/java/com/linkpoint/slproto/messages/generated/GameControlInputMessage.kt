package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GameControlInputMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    val axisData: MutableList<AxisDataBlock> = mutableListOf()
    val buttonData: MutableList<ButtonDataBlock> = mutableListOf()

    data class AxisDataBlock(
        var index: Int = 0,
        var value: Int = 0
    )
    data class ButtonDataBlock(
        var data: ByteArray = ByteArray(0)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        require(axisData.size <= 0xFF) { "AxisData size exceeds 255 (" + axisData.size + ")" }
        packByte(buffer, axisData.size)
        axisData.forEach { entry ->
            packByte(buffer, entry.index)
            packShort(buffer, entry.value)
        }
        require(buttonData.size <= 0xFF) { "ButtonData size exceeds 255 (" + buttonData.size + ")" }
        packByte(buffer, buttonData.size)
        buttonData.forEach { entry ->
            packVariable(buffer, entry.data, 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            axisData.clear()
            repeat(count) {
                val entry = AxisDataBlock()
                entry.index = unpackByte(buffer)
                entry.value = unpackShort(buffer)
                axisData += entry
            }
        }
        run {
            val count = unpackByte(buffer)
            buttonData.clear()
            repeat(count) {
                val entry = ButtonDataBlock()
                entry.data = unpackVariable(buffer, 1)
                buttonData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0x00000020

    override fun getMessageName(): String = "GameControlInput"
}
