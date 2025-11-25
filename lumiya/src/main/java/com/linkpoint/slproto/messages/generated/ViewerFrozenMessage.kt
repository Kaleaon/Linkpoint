package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ViewerFrozenMessage : SLMessage() {
    var data: Boolean = false


    override fun packPayload(buffer: ByteBuffer) {
        packBoolean(buffer, data)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        data = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0089.toInt()

    override fun getMessageName(): String = "ViewerFrozenMessage"
}
