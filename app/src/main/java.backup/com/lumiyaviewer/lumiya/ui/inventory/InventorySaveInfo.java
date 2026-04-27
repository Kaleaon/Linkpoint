package com.lumiyaviewer.lumiya.ui.inventory;

import android.os.Parcel;
import android.os.Parcelable;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InventorySaveInfo implements Parcelable {
    public static final Parcelable.Creator<InventorySaveInfo> CREATOR = new Parcelable.Creator<InventorySaveInfo>() {
        public InventorySaveInfo createFromParcel(Parcel parcel) {
            return new InventorySaveInfo(parcel);
        }

        public InventorySaveInfo[] newArray(int i) {
            return new InventorySaveInfo[i];
        }
    };
    @Nullable
    public final SLAssetType assetType;
    public final long inventoryOfferMessageId;
    @Nullable
    public final UUID notecardUUID;
    @Nonnull
    public final String saveItemName;
    @Nullable
    public final UUID saveItemUUID;
    @Nonnull
    public final InventorySaveType saveType;

    public enum InventorySaveType {
        NotecardItem,
        InventoryOffer
    }

    protected InventorySaveInfo(Parcel parcel) {
        this.saveType = InventorySaveType.values()[parcel.readInt()];
        if (parcel.readByte() != 0) {
            this.saveItemUUID = UUID.fromString(parcel.readString());
        } else {
            this.saveItemUUID = null;
        }
        this.saveItemName = parcel.readString();
        if (parcel.readByte() != 0) {
            this.notecardUUID = UUID.fromString(parcel.readString());
        } else {
            this.notecardUUID = null;
        }
        if (parcel.readByte() != 0) {
            this.assetType = SLAssetType.getByType(parcel.readInt());
        } else {
            this.assetType = null;
        }
        this.inventoryOfferMessageId = parcel.readLong();
    }

    public InventorySaveInfo(@Nonnull InventorySaveType inventorySaveType, @Nullable UUID uuid, @Nonnull String str, @Nullable UUID uuid2, @Nullable SLAssetType sLAssetType, long j) {
        this.saveType = inventorySaveType;
        this.saveItemUUID = uuid;
        this.saveItemName = str;
        this.notecardUUID = uuid2;
        this.assetType = sLAssetType;
        this.inventoryOfferMessageId = j;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.saveType.ordinal());
        if (this.saveItemUUID != null) {
            parcel.writeByte((byte) 1);
            parcel.writeString(this.saveItemUUID.toString());
        } else {
            parcel.writeByte((byte) 0);
        }
        parcel.writeString(this.saveItemName);
        if (this.notecardUUID != null) {
            parcel.writeByte((byte) 1);
            parcel.writeString(this.notecardUUID.toString());
        } else {
            parcel.writeByte((byte) 0);
        }
        if (this.assetType != null) {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.assetType.getTypeCode());
        } else {
            parcel.writeByte((byte) 0);
        }
        parcel.writeLong(this.inventoryOfferMessageId);
    }
}
