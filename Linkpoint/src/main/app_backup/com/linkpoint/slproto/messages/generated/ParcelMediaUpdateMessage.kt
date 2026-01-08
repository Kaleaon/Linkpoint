package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelMediaUpdateMessage : SLMessage() {
    var mediaUrl: ByteArray = ByteArray(0)
    var mediaId: UUID = UUID(0L, 0L)
    var mediaAutoScale: Int = 0
    var mediaType: ByteArray = ByteArray(0)
    var mediaDesc: ByteArray = ByteArray(0)
    var mediaWidth: Int = 0
    var mediaHeight: Int = 0
    var mediaLoop: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packVariable(buffer, mediaUrl, 1)
        packUUID(buffer, mediaId)
        packByte(buffer, mediaAutoScale)
        packVariable(buffer, mediaType, 1)
        packVariable(buffer, mediaDesc, 1)
        packInt(buffer, mediaWidth)
        packInt(buffer, mediaHeight)
        packByte(buffer, mediaLoop)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        mediaUrl = unpackVariable(buffer, 1)
        mediaId = unpackUUID(buffer)
        mediaAutoScale = unpackByte(buffer)
        mediaType = unpackVariable(buffer, 1)
        mediaDesc = unpackVariable(buffer, 1)
        mediaWidth = unpackInt(buffer)
        mediaHeight = unpackInt(buffer)
        mediaLoop = unpackByte(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF01A4.toInt()

    override fun getMessageName(): String = "ParcelMediaUpdate"
}
