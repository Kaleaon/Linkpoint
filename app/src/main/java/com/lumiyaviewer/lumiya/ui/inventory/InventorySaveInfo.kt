package com.lumiyaviewer.lumiya.ui.inventory

import android.os.Parcel
import android.os.Parcelable
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class InventorySaveInfo : Parcelable {
    Parcelable.Creator<InventorySaveInfo> CREATOR = Parcelable.Creator<InventorySaveInfo>() {
        InventorySaveInfo createFromParcel(Parcel parcel) {
            return InventorySaveInfo(parcel)
        }

        InventorySaveInfo[] newArray(Int i) {
            return InventorySaveInfo[i]
        }
    }
    @Nullable
    SLAssetType assetType
    Long inventoryOfferMessageId
    @Nullable
    UUID notecardUUID
    @Nonnull
    String saveItemName
    @Nullable
    UUID saveItemUUID
    @Nonnull
    InventorySaveType saveType

    enum InventorySaveType {
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

    InventorySaveInfo(@Nonnull InventorySaveType inventorySaveType, @Nullable UUID uuid, @Nonnull String str, @Nullable UUID uuid2, @Nullable SLAssetType sLAssetType, Long j) {
        this.saveType = inventorySaveType
        this.saveItemUUID = uuid
        this.saveItemName = str
        this.notecardUUID = uuid2
        this.assetType = sLAssetType
        this.inventoryOfferMessageId = j
    }

    Int describeContents() {
        return 0
    }

    Unit writeToParcel(Parcel parcel, Int i) {
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
