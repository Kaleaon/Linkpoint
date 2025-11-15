package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class PayPriceReplyMessage : SLMessage() {
    var objectId: UUID = UUID(0L, 0L)
    var defaultPayPrice: Int = 0
    val buttonData: MutableList<ButtonDataBlock> = mutableListOf()

    data class ButtonDataBlock(
        var payButton: Int = 0
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, objectId)
        packInt(buffer, defaultPayPrice)
        require(buttonData.size <= 0xFF) { "ButtonData size exceeds 255 (" + buttonData.size + ")" }
        packByte(buffer, buttonData.size)
        buttonData.forEach { entry ->
            packInt(buffer, entry.payButton)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        objectId = unpackUUID(buffer)
        defaultPayPrice = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            buttonData.clear()
            repeat(count) {
                val entry = ButtonDataBlock()
                entry.payButton = unpackInt(buffer)
                buttonData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00A2

    override fun getMessageName(): String = "PayPriceReply"
}
