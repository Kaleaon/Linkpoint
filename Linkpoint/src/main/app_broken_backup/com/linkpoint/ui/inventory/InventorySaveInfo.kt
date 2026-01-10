package com.linkpoint.ui.inventory

import android.os.Parcel
import android.os.Parcelable
import com.linkpoint.slproto.inventory.SLAssetType
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class InventorySaveInfo : Parcelable {
    Parcelable.Creator<InventorySaveInfo> CREATOR = Parcelable.Creator<InventorySaveInfo>() {
        fun createFromParcel(Parcel parcel): InventorySaveInfo {
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
    @NonNull
    String saveItemName
    @Nullable
    UUID saveItemUUID
    @NonNull
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

    InventorySaveInfo(@NonNull InventorySaveType inventorySaveType, @Nullable UUID uuid, @NonNull String str, @Nullable UUID uuid2, @Nullable SLAssetType sLAssetType, Long j) {
        this.saveType = inventorySaveType
        this.saveItemUUID = uuid
        this.saveItemName = str
        this.notecardUUID = uuid2
        this.assetType = sLAssetType
        this.inventoryOfferMessageId = j
    }

    fun describeContents(): Int {
        return 0
    }

    fun writeToParcel(Parcel parcel, Int i)  {
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
