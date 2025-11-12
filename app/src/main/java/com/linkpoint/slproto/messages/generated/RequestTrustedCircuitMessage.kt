package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class RequestTrustedCircuitMessage : SLMessage() {
    val rezSingleAttachmentFromInv: MutableList<RezSingleAttachmentFromInvBlock> = mutableListOf()

    data class RezSingleAttachmentFromInvBlock(
    )


    override fun packPayload(buffer: ByteBuffer) {
        rezSingleAttachmentFromInv.forEach { entry ->
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            rezSingleAttachmentFromInv.clear()
            repeat(count) {
                val entry = RezSingleAttachmentFromInvBlock()
                rezSingleAttachmentFromInv += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF018A

    override fun getMessageName(): String = "RequestTrustedCircuit"
}
