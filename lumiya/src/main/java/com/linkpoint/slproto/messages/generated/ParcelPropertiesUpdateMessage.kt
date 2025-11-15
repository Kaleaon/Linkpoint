package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ParcelPropertiesUpdateMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var localId: Int = 0
    var flags: Int = 0
    var parcelFlags: Int = 0
    var salePrice: Int = 0
    var name: ByteArray = ByteArray(0)
    var desc: ByteArray = ByteArray(0)
    var musicUrl: ByteArray = ByteArray(0)
    var mediaUrl: ByteArray = ByteArray(0)
    var mediaId: UUID = UUID(0L, 0L)
    var mediaAutoScale: Int = 0
    var groupId: UUID = UUID(0L, 0L)
    var passPrice: Int = 0
    var passHours: Float = 0f
    var category: Int = 0
    var authBuyerId: UUID = UUID(0L, 0L)
    var snapshotId: UUID = UUID(0L, 0L)
    var userLocation: LLVector3 = LLVector3()
    var userLookAt: LLVector3 = LLVector3()
    var landingType: Int = 0


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, localId)
        packInt(buffer, flags)
        packInt(buffer, parcelFlags)
        packInt(buffer, salePrice)
        packVariable(buffer, name, 1)
        packVariable(buffer, desc, 1)
        packVariable(buffer, musicUrl, 1)
        packVariable(buffer, mediaUrl, 1)
        packUUID(buffer, mediaId)
        packByte(buffer, mediaAutoScale)
        packUUID(buffer, groupId)
        packInt(buffer, passPrice)
        packFloat(buffer, passHours)
        packByte(buffer, category)
        packUUID(buffer, authBuyerId)
        packUUID(buffer, snapshotId)
        userLocation.pack(buffer)
        userLookAt.pack(buffer)
        packByte(buffer, landingType)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        localId = unpackInt(buffer)
        flags = unpackInt(buffer)
        parcelFlags = unpackInt(buffer)
        salePrice = unpackInt(buffer)
        name = unpackVariable(buffer, 1)
        desc = unpackVariable(buffer, 1)
        musicUrl = unpackVariable(buffer, 1)
        mediaUrl = unpackVariable(buffer, 1)
        mediaId = unpackUUID(buffer)
        mediaAutoScale = unpackByte(buffer)
        groupId = unpackUUID(buffer)
        passPrice = unpackInt(buffer)
        passHours = unpackFloat(buffer)
        category = unpackByte(buffer)
        authBuyerId = unpackUUID(buffer)
        snapshotId = unpackUUID(buffer)
        userLocation = LLVector3.unpack(buffer)
        userLookAt = LLVector3.unpack(buffer)
        landingType = unpackByte(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00C6

    override fun getMessageName(): String = "ParcelPropertiesUpdate"
}
