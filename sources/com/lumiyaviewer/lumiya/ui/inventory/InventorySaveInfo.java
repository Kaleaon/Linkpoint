// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.os.Parcel;
import android.os.Parcelable;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import java.util.UUID;

public class InventorySaveInfo
    implements Parcelable
{
    public static final class InventorySaveType extends Enum
    {

        private static final InventorySaveType $VALUES[];
        public static final InventorySaveType InventoryOffer;
        public static final InventorySaveType NotecardItem;

        public static InventorySaveType valueOf(String s)
        {
            return (InventorySaveType)Enum.valueOf(com/lumiyaviewer/lumiya/ui/inventory/InventorySaveInfo$InventorySaveType, s);
        }

        public static InventorySaveType[] values()
        {
            return $VALUES;
        }

        static 
        {
            NotecardItem = new InventorySaveType("NotecardItem", 0);
            InventoryOffer = new InventorySaveType("InventoryOffer", 1);
            $VALUES = (new InventorySaveType[] {
                NotecardItem, InventoryOffer
            });
        }

        private InventorySaveType(String s, int i)
        {
            super(s, i);
        }
    }


    public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public InventorySaveInfo createFromParcel(Parcel parcel)
        {
            return new InventorySaveInfo(parcel);
        }

        public volatile Object createFromParcel(Parcel parcel)
        {
            return createFromParcel(parcel);
        }

        public InventorySaveInfo[] newArray(int i)
        {
            return new InventorySaveInfo[i];
        }

        public volatile Object[] newArray(int i)
        {
            return newArray(i);
        }

    };
    public final SLAssetType assetType;
    public final long inventoryOfferMessageId;
    public final UUID notecardUUID;
    public final String saveItemName;
    public final UUID saveItemUUID;
    public final InventorySaveType saveType;

    protected InventorySaveInfo(Parcel parcel)
    {
        saveType = InventorySaveType.values()[parcel.readInt()];
        if (parcel.readByte() != 0)
        {
            saveItemUUID = UUID.fromString(parcel.readString());
        } else
        {
            saveItemUUID = null;
        }
        saveItemName = parcel.readString();
        if (parcel.readByte() != 0)
        {
            notecardUUID = UUID.fromString(parcel.readString());
        } else
        {
            notecardUUID = null;
        }
        if (parcel.readByte() != 0)
        {
            assetType = SLAssetType.getByType(parcel.readInt());
        } else
        {
            assetType = null;
        }
        inventoryOfferMessageId = parcel.readLong();
    }

    public InventorySaveInfo(InventorySaveType inventorysavetype, UUID uuid, String s, UUID uuid1, SLAssetType slassettype, long l)
    {
        saveType = inventorysavetype;
        saveItemUUID = uuid;
        saveItemName = s;
        notecardUUID = uuid1;
        assetType = slassettype;
        inventoryOfferMessageId = l;
    }

    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(saveType.ordinal());
        if (saveItemUUID != null)
        {
            parcel.writeByte((byte)1);
            parcel.writeString(saveItemUUID.toString());
        } else
        {
            parcel.writeByte((byte)0);
        }
        parcel.writeString(saveItemName);
        if (notecardUUID != null)
        {
            parcel.writeByte((byte)1);
            parcel.writeString(notecardUUID.toString());
        } else
        {
            parcel.writeByte((byte)0);
        }
        if (assetType != null)
        {
            parcel.writeByte((byte)1);
            parcel.writeInt(assetType.getTypeCode());
        } else
        {
            parcel.writeByte((byte)0);
        }
        parcel.writeLong(inventoryOfferMessageId);
    }

}
