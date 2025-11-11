package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.UUID

/**
 * Parcel properties message (0x17)
 */
class ParcelPropertiesMessage : SLMessage() {
    data class ParcelData(
        var requestResult: Int = 0,
        var sequenceId: Int = 0,
        var snapSelection: Boolean = false,
        var selfCount: Int = 0,
        var otherCount: Int = 0,
        var publicCount: Int = 0,
        var localId: Int = 0,
        var ownerId: UUID = UUID.randomUUID(),
        var isGroupOwned: Boolean = false,
        var auctionId: Int = 0,
        var claimDate: Int = 0,
        var claimPrice: Int = 0,
        var rentPrice: Int = 0,
        var aabbMin: LLVector3 = LLVector3(),
        var aabbMax: LLVector3 = LLVector3(),
        var bitmap: ByteArray = ByteArray(0),
        var area: Int = 0,
        var status: Int = 0,
        var simWideMaxPrims: Int = 0,
        var simWideTotalPrims: Int = 0,
        var maxPrims: Int = 0,
        var totalPrims: Int = 0,
        var ownerPrims: Int = 0,
        var groupPrims: Int = 0,
        var otherPrims: Int = 0,
        var selectedPrims: Int = 0,
        var parcelPrimBonus: Float = 0f,
        var otherCleanTime: Int = 0,
        var parcelFlags: Int = 0,
        var salePrice: Int = 0,
        var name: String = "",
        var description: String = "",
        var musicUrl: String = "",
        var mediaUrl: String = "",
        var mediaId: UUID = UUID.randomUUID(),
        var mediaAutoScale: Int = 0,
        var groupId: UUID = UUID.randomUUID(),
        var passPrice: Int = 0,
        var passHours: Float = 0f,
        var category: Int = 0,
        var authorizedBuyerId: UUID = UUID.randomUUID(),
        var snapshotId: UUID = UUID.randomUUID(),
        var userLocation: LLVector3 = LLVector3(),
        var userLookAt: LLVector3 = LLVector3(),
        var landingType: Int = 0,
        var regionPushOverride: Boolean = false,
        var regionDenyAnonymous: Boolean = false,
        var regionDenyIdentified: Boolean = false,
        var regionDenyTransacted: Boolean = false,
    )

    data class AgeVerificationBlock(
        var regionDenyAgeUnverified: Boolean = false,
    )

    var parcelData: ParcelData = ParcelData()
    var ageVerification: AgeVerificationBlock = AgeVerificationBlock()

    private val charset: Charset = Charsets.UTF_8

    init {
        zeroCoded = true
        parcelData.ownerId = UUID(0L, 0L)
        parcelData.groupId = UUID(0L, 0L)
        parcelData.mediaId = UUID(0L, 0L)
        parcelData.authorizedBuyerId = UUID(0L, 0L)
        parcelData.snapshotId = UUID(0L, 0L)
    }

    override fun packPayload(buffer: ByteBuffer) {
        packInt(buffer, parcelData.requestResult)
        packInt(buffer, parcelData.sequenceId)
        packBoolean(buffer, parcelData.snapSelection)
        packInt(buffer, parcelData.selfCount)
        packInt(buffer, parcelData.otherCount)
        packInt(buffer, parcelData.publicCount)
        packInt(buffer, parcelData.localId)
        packUUID(buffer, parcelData.ownerId)
        packBoolean(buffer, parcelData.isGroupOwned)
        packInt(buffer, parcelData.auctionId)
        packInt(buffer, parcelData.claimDate)
        packInt(buffer, parcelData.claimPrice)
        packInt(buffer, parcelData.rentPrice)
        parcelData.aabbMin.pack(buffer)
        parcelData.aabbMax.pack(buffer)
        packVariable(buffer, parcelData.bitmap, 2)
        packInt(buffer, parcelData.area)
        require(parcelData.status in 0..0xFF) { "status out of range (${parcelData.status})" }
        packByte(buffer, parcelData.status)
        packInt(buffer, parcelData.simWideMaxPrims)
        packInt(buffer, parcelData.simWideTotalPrims)
        packInt(buffer, parcelData.maxPrims)
        packInt(buffer, parcelData.totalPrims)
        packInt(buffer, parcelData.ownerPrims)
        packInt(buffer, parcelData.groupPrims)
        packInt(buffer, parcelData.otherPrims)
        packInt(buffer, parcelData.selectedPrims)
        packFloat(buffer, parcelData.parcelPrimBonus)
        packInt(buffer, parcelData.otherCleanTime)
        packInt(buffer, parcelData.parcelFlags)
        packInt(buffer, parcelData.salePrice)
        packVariable(buffer, parcelData.name.toByteArray(charset), 1)
        packVariable(buffer, parcelData.description.toByteArray(charset), 1)
        packVariable(buffer, parcelData.musicUrl.toByteArray(charset), 1)
        packVariable(buffer, parcelData.mediaUrl.toByteArray(charset), 1)
        packUUID(buffer, parcelData.mediaId)
        require(parcelData.mediaAutoScale in 0..0xFF) { "mediaAutoScale out of range (${parcelData.mediaAutoScale})" }
        packByte(buffer, parcelData.mediaAutoScale)
        packUUID(buffer, parcelData.groupId)
        packInt(buffer, parcelData.passPrice)
        packFloat(buffer, parcelData.passHours)
        require(parcelData.category in 0..0xFF) { "category out of range (${parcelData.category})" }
        packByte(buffer, parcelData.category)
        packUUID(buffer, parcelData.authorizedBuyerId)
        packUUID(buffer, parcelData.snapshotId)
        parcelData.userLocation.pack(buffer)
        parcelData.userLookAt.pack(buffer)
        require(parcelData.landingType in 0..0xFF) { "landingType out of range (${parcelData.landingType})" }
        packByte(buffer, parcelData.landingType)
        packBoolean(buffer, parcelData.regionPushOverride)
        packBoolean(buffer, parcelData.regionDenyAnonymous)
        packBoolean(buffer, parcelData.regionDenyIdentified)
        packBoolean(buffer, parcelData.regionDenyTransacted)
        packBoolean(buffer, ageVerification.regionDenyAgeUnverified)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        parcelData.requestResult = unpackInt(buffer)
        parcelData.sequenceId = unpackInt(buffer)
        parcelData.snapSelection = unpackBoolean(buffer)
        parcelData.selfCount = unpackInt(buffer)
        parcelData.otherCount = unpackInt(buffer)
        parcelData.publicCount = unpackInt(buffer)
        parcelData.localId = unpackInt(buffer)
        parcelData.ownerId = unpackUUID(buffer)
        parcelData.isGroupOwned = unpackBoolean(buffer)
        parcelData.auctionId = unpackInt(buffer)
        parcelData.claimDate = unpackInt(buffer)
        parcelData.claimPrice = unpackInt(buffer)
        parcelData.rentPrice = unpackInt(buffer)
        parcelData.aabbMin = LLVector3.unpack(buffer)
        parcelData.aabbMax = LLVector3.unpack(buffer)
        parcelData.bitmap = unpackVariable(buffer, 2)
        parcelData.area = unpackInt(buffer)
        parcelData.status = unpackByte(buffer)
        parcelData.simWideMaxPrims = unpackInt(buffer)
        parcelData.simWideTotalPrims = unpackInt(buffer)
        parcelData.maxPrims = unpackInt(buffer)
        parcelData.totalPrims = unpackInt(buffer)
        parcelData.ownerPrims = unpackInt(buffer)
        parcelData.groupPrims = unpackInt(buffer)
        parcelData.otherPrims = unpackInt(buffer)
        parcelData.selectedPrims = unpackInt(buffer)
        parcelData.parcelPrimBonus = unpackFloat(buffer)
        parcelData.otherCleanTime = unpackInt(buffer)
        parcelData.parcelFlags = unpackInt(buffer)
        parcelData.salePrice = unpackInt(buffer)
        parcelData.name = String(unpackVariable(buffer, 1), charset)
        parcelData.description = String(unpackVariable(buffer, 1), charset)
        parcelData.musicUrl = String(unpackVariable(buffer, 1), charset)
        parcelData.mediaUrl = String(unpackVariable(buffer, 1), charset)
        parcelData.mediaId = unpackUUID(buffer)
        parcelData.mediaAutoScale = unpackByte(buffer)
        parcelData.groupId = unpackUUID(buffer)
        parcelData.passPrice = unpackInt(buffer)
        parcelData.passHours = unpackFloat(buffer)
        parcelData.category = unpackByte(buffer)
        parcelData.authorizedBuyerId = unpackUUID(buffer)
        parcelData.snapshotId = unpackUUID(buffer)
        parcelData.userLocation = LLVector3.unpack(buffer)
        parcelData.userLookAt = LLVector3.unpack(buffer)
        parcelData.landingType = unpackByte(buffer)
        parcelData.regionPushOverride = unpackBoolean(buffer)
        parcelData.regionDenyAnonymous = unpackBoolean(buffer)
        parcelData.regionDenyIdentified = unpackBoolean(buffer)
        parcelData.regionDenyTransacted = unpackBoolean(buffer)
        ageVerification.regionDenyAgeUnverified = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.PARCEL_PROPERTIES

    override fun getMessageName(): String = "ParcelProperties"
}
