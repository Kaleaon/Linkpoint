package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class UpdateParcelMessage : SLMessage() {
    var parcelId: UUID = UUID(0L, 0L)
    var regionHandle: Long = 0L
    var ownerId: UUID = UUID(0L, 0L)
    var groupOwned: Boolean = false
    var status: Int = 0
    var name: ByteArray = ByteArray(0)
    var description: ByteArray = ByteArray(0)
    var musicUrl: ByteArray = ByteArray(0)
    var regionX: Float = 0f
    var regionY: Float = 0f
    var actualArea: Int = 0
    var billableArea: Int = 0
    var showDir: Boolean = false
    var isForSale: Boolean = false
    var category: Int = 0
    var snapshotId: UUID = UUID(0L, 0L)
    var userLocation: LLVector3 = LLVector3()
    var salePrice: Int = 0
    var authorizedBuyerId: UUID = UUID(0L, 0L)
    var allowPublish: Boolean = false
    var maturePublish: Boolean = false


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, parcelId)
        packLong(buffer, regionHandle)
        packUUID(buffer, ownerId)
        packBoolean(buffer, groupOwned)
        packByte(buffer, status)
        packVariable(buffer, name, 1)
        packVariable(buffer, description, 1)
        packVariable(buffer, musicUrl, 1)
        packFloat(buffer, regionX)
        packFloat(buffer, regionY)
        packInt(buffer, actualArea)
        packInt(buffer, billableArea)
        packBoolean(buffer, showDir)
        packBoolean(buffer, isForSale)
        packByte(buffer, category)
        packUUID(buffer, snapshotId)
        userLocation.pack(buffer)
        packInt(buffer, salePrice)
        packUUID(buffer, authorizedBuyerId)
        packBoolean(buffer, allowPublish)
        packBoolean(buffer, maturePublish)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        parcelId = unpackUUID(buffer)
        regionHandle = unpackLong(buffer)
        ownerId = unpackUUID(buffer)
        groupOwned = unpackBoolean(buffer)
        status = unpackByte(buffer)
        name = unpackVariable(buffer, 1)
        description = unpackVariable(buffer, 1)
        musicUrl = unpackVariable(buffer, 1)
        regionX = unpackFloat(buffer)
        regionY = unpackFloat(buffer)
        actualArea = unpackInt(buffer)
        billableArea = unpackInt(buffer)
        showDir = unpackBoolean(buffer)
        isForSale = unpackBoolean(buffer)
        category = unpackByte(buffer)
        snapshotId = unpackUUID(buffer)
        userLocation = LLVector3.unpack(buffer)
        salePrice = unpackInt(buffer)
        authorizedBuyerId = unpackUUID(buffer)
        allowPublish = unpackBoolean(buffer)
        maturePublish = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00DD.toInt()

    override fun getMessageName(): String = "UpdateParcel"
}
