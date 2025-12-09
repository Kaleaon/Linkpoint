package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ParcelProperties : SLMessage {
    AgeVerificationBlock AgeVerificationBlock_Field = AgeVerificationBlock()
    ParcelData ParcelData_Field = ParcelData()

    class AgeVerificationBlock {
        Boolean RegionDenyAgeUnverified
    }

    class ParcelData {
        LLVector3 AABBMax
        LLVector3 AABBMin
        Int Area
        Int AuctionID
        UUID AuthBuyerID
        ByteArray Bitmap
        Int Category
        Int ClaimDate
        Int ClaimPrice
        ByteArray Desc
        UUID GroupID
        Int GroupPrims
        Boolean IsGroupOwned
        Int LandingType
        Int LocalID
        Int MaxPrims
        Int MediaAutoScale
        UUID MediaID
        ByteArray MediaURL
        ByteArray MusicURL
        ByteArray Name
        Int OtherCleanTime
        Int OtherCount
        Int OtherPrims
        UUID OwnerID
        Int OwnerPrims
        Int ParcelFlags
        Float ParcelPrimBonus
        Float PassHours
        Int PassPrice
        Int PublicCount
        Boolean RegionDenyAnonymous
        Boolean RegionDenyIdentified
        Boolean RegionDenyTransacted
        Boolean RegionPushOverride
        Int RentPrice
        Int RequestResult
        Int SalePrice
        Int SelectedPrims
        Int SelfCount
        Int SequenceID
        Int SimWideMaxPrims
        Int SimWideTotalPrims
        Boolean SnapSelection
        UUID SnapshotID
        Int Status
        Int TotalPrims
        LLVector3 UserLocation
        LLVector3 UserLookAt
    }

    ParcelProperties() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.ParcelData_Field.Bitmap.size + 84 + 4 + 1 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 1 + this.ParcelData_Field.Name.size + 1 + this.ParcelData_Field.Desc.size + 1 + this.ParcelData_Field.MusicURL.size + 1 + this.ParcelData_Field.MediaURL.size + 16 + 1 + 16 + 4 + 4 + 1 + 16 + 16 + 12 + 12 + 1 + 1 + 1 + 1 + 1 + 1 + 1
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleParcelProperties(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.put(Ascii.ETB)
        packInt(byteBuffer, this.ParcelData_Field.RequestResult)
        packInt(byteBuffer, this.ParcelData_Field.SequenceID)
        packBoolean(byteBuffer, this.ParcelData_Field.SnapSelection)
        packInt(byteBuffer, this.ParcelData_Field.SelfCount)
        packInt(byteBuffer, this.ParcelData_Field.OtherCount)
        packInt(byteBuffer, this.ParcelData_Field.PublicCount)
        packInt(byteBuffer, this.ParcelData_Field.LocalID)
        packUUID(byteBuffer, this.ParcelData_Field.OwnerID)
        packBoolean(byteBuffer, this.ParcelData_Field.IsGroupOwned)
        packInt(byteBuffer, this.ParcelData_Field.AuctionID)
        packInt(byteBuffer, this.ParcelData_Field.ClaimDate)
        packInt(byteBuffer, this.ParcelData_Field.ClaimPrice)
        packInt(byteBuffer, this.ParcelData_Field.RentPrice)
        packLLVector3(byteBuffer, this.ParcelData_Field.AABBMin)
        packLLVector3(byteBuffer, this.ParcelData_Field.AABBMax)
        packVariable(byteBuffer, this.ParcelData_Field.Bitmap, 2)
        packInt(byteBuffer, this.ParcelData_Field.Area)
        packByte(byteBuffer, (this as Byte).ParcelData_Field.Status)
        packInt(byteBuffer, this.ParcelData_Field.SimWideMaxPrims)
        packInt(byteBuffer, this.ParcelData_Field.SimWideTotalPrims)
        packInt(byteBuffer, this.ParcelData_Field.MaxPrims)
        packInt(byteBuffer, this.ParcelData_Field.TotalPrims)
        packInt(byteBuffer, this.ParcelData_Field.OwnerPrims)
        packInt(byteBuffer, this.ParcelData_Field.GroupPrims)
        packInt(byteBuffer, this.ParcelData_Field.OtherPrims)
        packInt(byteBuffer, this.ParcelData_Field.SelectedPrims)
        packFloat(byteBuffer, this.ParcelData_Field.ParcelPrimBonus)
        packInt(byteBuffer, this.ParcelData_Field.OtherCleanTime)
        packInt(byteBuffer, this.ParcelData_Field.ParcelFlags)
        packInt(byteBuffer, this.ParcelData_Field.SalePrice)
        packVariable(byteBuffer, this.ParcelData_Field.Name, 1)
        packVariable(byteBuffer, this.ParcelData_Field.Desc, 1)
        packVariable(byteBuffer, this.ParcelData_Field.MusicURL, 1)
        packVariable(byteBuffer, this.ParcelData_Field.MediaURL, 1)
        packUUID(byteBuffer, this.ParcelData_Field.MediaID)
        packByte(byteBuffer, (this as Byte).ParcelData_Field.MediaAutoScale)
        packUUID(byteBuffer, this.ParcelData_Field.GroupID)
        packInt(byteBuffer, this.ParcelData_Field.PassPrice)
        packFloat(byteBuffer, this.ParcelData_Field.PassHours)
        packByte(byteBuffer, (this as Byte).ParcelData_Field.Category)
        packUUID(byteBuffer, this.ParcelData_Field.AuthBuyerID)
        packUUID(byteBuffer, this.ParcelData_Field.SnapshotID)
        packLLVector3(byteBuffer, this.ParcelData_Field.UserLocation)
        packLLVector3(byteBuffer, this.ParcelData_Field.UserLookAt)
        packByte(byteBuffer, (this as Byte).ParcelData_Field.LandingType)
        packBoolean(byteBuffer, this.ParcelData_Field.RegionPushOverride)
        packBoolean(byteBuffer, this.ParcelData_Field.RegionDenyAnonymous)
        packBoolean(byteBuffer, this.ParcelData_Field.RegionDenyIdentified)
        packBoolean(byteBuffer, this.ParcelData_Field.RegionDenyTransacted)
        packBoolean(byteBuffer, this.AgeVerificationBlock_Field.RegionDenyAgeUnverified)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.ParcelData_Field.RequestResult = unpackInt(byteBuffer)
        this.ParcelData_Field.SequenceID = unpackInt(byteBuffer)
        this.ParcelData_Field.SnapSelection = unpackBoolean(byteBuffer)
        this.ParcelData_Field.SelfCount = unpackInt(byteBuffer)
        this.ParcelData_Field.OtherCount = unpackInt(byteBuffer)
        this.ParcelData_Field.PublicCount = unpackInt(byteBuffer)
        this.ParcelData_Field.LocalID = unpackInt(byteBuffer)
        this.ParcelData_Field.OwnerID = unpackUUID(byteBuffer)
        this.ParcelData_Field.IsGroupOwned = unpackBoolean(byteBuffer)
        this.ParcelData_Field.AuctionID = unpackInt(byteBuffer)
        this.ParcelData_Field.ClaimDate = unpackInt(byteBuffer)
        this.ParcelData_Field.ClaimPrice = unpackInt(byteBuffer)
        this.ParcelData_Field.RentPrice = unpackInt(byteBuffer)
        this.ParcelData_Field.AABBMin = unpackLLVector3(byteBuffer)
        this.ParcelData_Field.AABBMax = unpackLLVector3(byteBuffer)
        this.ParcelData_Field.Bitmap = unpackVariable(byteBuffer, 2)
        this.ParcelData_Field.Area = unpackInt(byteBuffer)
        this.ParcelData_Field.Status = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ParcelData_Field.SimWideMaxPrims = unpackInt(byteBuffer)
        this.ParcelData_Field.SimWideTotalPrims = unpackInt(byteBuffer)
        this.ParcelData_Field.MaxPrims = unpackInt(byteBuffer)
        this.ParcelData_Field.TotalPrims = unpackInt(byteBuffer)
        this.ParcelData_Field.OwnerPrims = unpackInt(byteBuffer)
        this.ParcelData_Field.GroupPrims = unpackInt(byteBuffer)
        this.ParcelData_Field.OtherPrims = unpackInt(byteBuffer)
        this.ParcelData_Field.SelectedPrims = unpackInt(byteBuffer)
        this.ParcelData_Field.ParcelPrimBonus = unpackFloat(byteBuffer)
        this.ParcelData_Field.OtherCleanTime = unpackInt(byteBuffer)
        this.ParcelData_Field.ParcelFlags = unpackInt(byteBuffer)
        this.ParcelData_Field.SalePrice = unpackInt(byteBuffer)
        this.ParcelData_Field.Name = unpackVariable(byteBuffer, 1)
        this.ParcelData_Field.Desc = unpackVariable(byteBuffer, 1)
        this.ParcelData_Field.MusicURL = unpackVariable(byteBuffer, 1)
        this.ParcelData_Field.MediaURL = unpackVariable(byteBuffer, 1)
        this.ParcelData_Field.MediaID = unpackUUID(byteBuffer)
        this.ParcelData_Field.MediaAutoScale = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ParcelData_Field.GroupID = unpackUUID(byteBuffer)
        this.ParcelData_Field.PassPrice = unpackInt(byteBuffer)
        this.ParcelData_Field.PassHours = unpackFloat(byteBuffer)
        this.ParcelData_Field.Category = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ParcelData_Field.AuthBuyerID = unpackUUID(byteBuffer)
        this.ParcelData_Field.SnapshotID = unpackUUID(byteBuffer)
        this.ParcelData_Field.UserLocation = unpackLLVector3(byteBuffer)
        this.ParcelData_Field.UserLookAt = unpackLLVector3(byteBuffer)
        this.ParcelData_Field.LandingType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ParcelData_Field.RegionPushOverride = unpackBoolean(byteBuffer)
        this.ParcelData_Field.RegionDenyAnonymous = unpackBoolean(byteBuffer)
        this.ParcelData_Field.RegionDenyIdentified = unpackBoolean(byteBuffer)
        this.ParcelData_Field.RegionDenyTransacted = unpackBoolean(byteBuffer)
        this.AgeVerificationBlock_Field.RegionDenyAgeUnverified = unpackBoolean(byteBuffer)
    }
}
