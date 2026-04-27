package com.linkpoint.ui.inventory

import android.os.Parcel
import android.os.Parcelable
import com.linkpoint.slproto.inventory.SLAssetType
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class InventorySaveInfo : Parcelable {
    const val Parcelable.Creator<InventorySaveInfo> CREATOR = Parcelable.Creator<InventorySaveInfo>() {
         public fun createFromParcel(parcel: Parcel): InventorySaveInfo {
            return InventorySaveInfo(parcel)
        }

        public Array<InventorySaveInfo> newArray(Int i) {
            return InventorySaveInfo[i]
        }
    }
    val SLAssetType assetType
    val Long inventoryOfferMessageId
    val UUID notecardUUID
    val String saveItemName
    val UUID saveItemUUID
    val InventorySaveType saveType

    enum class InventorySaveType {
        NotecardItem,
        InventoryOffer
    }

    protected InventorySaveInfo(Parcel parcel) {
        this.saveType = InventorySaveType.values()[parcel.readInt()]
        if (parcel.readByte() != 0) {
            this.saveItemUUID = UUID.fromString(parcel.readString())
        } else {
            this.saveItemUUID = null
        }
        this.saveItemName = parcel.readString()
        if (parcel.readByte() != 0) {
            this.notecardUUID = UUID.fromString(parcel.readString())
        } else {
            this.notecardUUID = null
        }
        if (parcel.readByte() != 0) {
            this.assetType = SLAssetType.getByType(parcel.readInt())
        } else {
            this.assetType = null
        }
        this.inventoryOfferMessageId = parcel.readLong()
    }

    public InventorySaveInfo(InventorySaveType inventorySaveType, UUID uuid, String str, UUID uuid2, SLAssetType sLAssetType, Long j) {
        this.saveType = inventorySaveType
        this.saveItemUUID = uuid
        this.saveItemName = str
        this.notecardUUID = uuid2
        this.assetType = sLAssetType
        this.inventoryOfferMessageId = j
    }

     public fun describeContents(): Int {
        return 0
    }

    fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(this.saveType.ordinal())
        if (this.saveItemUUID != null) {
            parcel.writeByte((Byte) 1)
            parcel.writeString(this.saveItemUUID.toString())
        } else {
            parcel.writeByte((Byte) 0)
        }
        parcel.writeString(this.saveItemName)
        if (this.notecardUUID != null) {
            parcel.writeByte((Byte) 1)
            parcel.writeString(this.notecardUUID.toString())
        } else {
            parcel.writeByte((Byte) 0)
        }
        if (this.assetType != null) {
            parcel.writeByte((Byte) 1)
            parcel.writeInt(this.assetType.getTypeCode())
        } else {
            parcel.writeByte((Byte) 0)
        }
        parcel.writeLong(this.inventoryOfferMessageId)
    }
}
