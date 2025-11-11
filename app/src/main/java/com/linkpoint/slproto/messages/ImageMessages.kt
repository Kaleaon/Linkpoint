package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

/**
 * Image data message (0x09)
 */
class ImageDataMessage : SLMessage() {
    var imageId: UUID = UUID.randomUUID()
    var codec: Int = 0
    var size: Int = 0
    var packets: Int = 0
    var data: ByteArray = ByteArray(0)

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, imageId)
        require(codec in 0..0xFF) { "codec out of range ($codec)" }
        packByte(buffer, codec)
        packInt(buffer, size)
        require(packets in 0..0xFFFF) { "packets out of range ($packets)" }
        packUInt16(buffer, packets)
        packVariable(buffer, data, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        imageId = unpackUUID(buffer)
        codec = unpackByte(buffer)
        size = unpackInt(buffer)
        packets = unpackUInt16(buffer)
        data = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.IMAGE_DATA

    override fun getMessageName(): String = "ImageData"
}

/**
 * Image packet message (0x0A)
 */
class ImagePacketMessage : SLMessage() {
    var imageId: UUID = UUID.randomUUID()
    var packet: Int = 0
    var data: ByteArray = ByteArray(0)

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, imageId)
        require(packet in 0..0xFFFF) { "packet out of range ($packet)" }
        packUInt16(buffer, packet)
        packVariable(buffer, data, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        imageId = unpackUUID(buffer)
        packet = unpackUInt16(buffer)
        data = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.IMAGE_PACKET

    override fun getMessageName(): String = "ImagePacket"
}

/**
 * Layer data message (0x0B)
 */
class LayerDataMessage : SLMessage() {
    var layerType: Int = 0
    var data: ByteArray = ByteArray(0)

    override fun packPayload(buffer: ByteBuffer) {
        require(layerType in 0..0xFF) { "layerType out of range ($layerType)" }
        packByte(buffer, layerType)
        packVariable(buffer, data, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        layerType = unpackByte(buffer)
        data = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.LAYER_DATA

    override fun getMessageName(): String = "LayerData"
}
