package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class InventoryAssetResponseMessage : SLMessage() {
    var queryId: UUID = UUID(0L, 0L)
    var assetId: UUID = UUID(0L, 0L)
    var isReadable: Boolean = false


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, queryId)
        packUUID(buffer, assetId)
        packBoolean(buffer, isReadable)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        queryId = unpackUUID(buffer)
        assetId = unpackUUID(buffer)
        isReadable = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF011B.toInt()

    override fun getMessageName(): String = "InventoryAssetResponse"
}
