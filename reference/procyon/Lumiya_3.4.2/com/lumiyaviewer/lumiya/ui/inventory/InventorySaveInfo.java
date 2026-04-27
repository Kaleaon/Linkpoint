// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.os.Parcel;
import javax.annotation.Nonnull;
import java.util.UUID;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

public class InventorySaveInfo implements Parcelable
{
    public static final Parcelable$Creator<InventorySaveInfo> CREATOR;
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
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<InventorySaveInfo>() {
            public InventorySaveInfo createFromParcel(final Parcel parcel) {
                return new InventorySaveInfo(parcel);
            }
            
            public InventorySaveInfo[] newArray(final int n) {
                return new InventorySaveInfo[n];
            }
        };
    }
    
    protected InventorySaveInfo(final Parcel parcel) {
        this.saveType = InventorySaveType.values()[parcel.readInt()];
        if (parcel.readByte() != 0) {
            this.saveItemUUID = UUID.fromString(parcel.readString());
        }
        else {
            this.saveItemUUID = null;
        }
        this.saveItemName = parcel.readString();
        if (parcel.readByte() != 0) {
            this.notecardUUID = UUID.fromString(parcel.readString());
        }
        else {
            this.notecardUUID = null;
        }
        if (parcel.readByte() != 0) {
            this.assetType = SLAssetType.getByType(parcel.readInt());
        }
        else {
            this.assetType = null;
        }
        this.inventoryOfferMessageId = parcel.readLong();
    }
    
    public InventorySaveInfo(@Nonnull final InventorySaveType saveType, @Nullable final UUID saveItemUUID, @Nonnull final String saveItemName, @Nullable final UUID notecardUUID, @Nullable final SLAssetType assetType, final long inventoryOfferMessageId) {
        this.saveType = saveType;
        this.saveItemUUID = saveItemUUID;
        this.saveItemName = saveItemName;
        this.notecardUUID = notecardUUID;
        this.assetType = assetType;
        this.inventoryOfferMessageId = inventoryOfferMessageId;
    }
    
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeInt(this.saveType.ordinal());
        if (this.saveItemUUID != null) {
            parcel.writeByte((byte)1);
            parcel.writeString(this.saveItemUUID.toString());
        }
        else {
            parcel.writeByte((byte)0);
        }
        parcel.writeString(this.saveItemName);
        if (this.notecardUUID != null) {
            parcel.writeByte((byte)1);
            parcel.writeString(this.notecardUUID.toString());
        }
        else {
            parcel.writeByte((byte)0);
        }
        if (this.assetType != null) {
            parcel.writeByte((byte)1);
            parcel.writeInt(this.assetType.getTypeCode());
        }
        else {
            parcel.writeByte((byte)0);
        }
        parcel.writeLong(this.inventoryOfferMessageId);
    }
    
    public enum InventorySaveType
    {
        InventoryOffer("InventoryOffer", 1), 
        NotecardItem("NotecardItem", 0);
        
        private InventorySaveType(final String name, final int ordinal) {
        }
    }
}
