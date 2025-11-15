package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class EdgeDataPacketMessage : SLMessage() {
    var layerType: Int = 0
    var direction: Int = 0
    var layerData: ByteArray = ByteArray(0)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packByte(buffer, layerType)
        packByte(buffer, direction)
        packVariable(buffer, layerData, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        layerType = unpackByte(buffer)
        direction = unpackByte(buffer)
        layerData = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = 0x00000018

    override fun getMessageName(): String = "EdgeDataPacket"
}
