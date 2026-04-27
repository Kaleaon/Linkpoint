// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.orm;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.orm:
//            AutoValue_InventoryQuery, InventoryEntryList, InventoryDB

public abstract class InventoryQuery
    implements Parcelable
{

    private static final int ASSET_TYPE_ANY = -1;
    public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public InventoryQuery createFromParcel(Parcel parcel)
        {
            parcel = parcel.readBundle(getClass().getClassLoader());
            return InventoryQuery.create(UUIDPool.getUUID(parcel.getString("folderId")), parcel.getString("containsString"), parcel.getBoolean("includeFolders"), parcel.getBoolean("includeItems"), parcel.getBoolean("newestFirst"), parcel.getInt("assetType", -1));
        }

        public volatile Object createFromParcel(Parcel parcel)
        {
            return createFromParcel(parcel);
        }

        public InventoryQuery[] newArray(int i)
        {
            return new InventoryQuery[i];
        }

        public volatile Object[] newArray(int i)
        {
            return newArray(i);
        }

    };
    private static final int FOLDER_TYPE_ANY = -1;

    public InventoryQuery()
    {
    }

    public static InventoryQuery create(UUID uuid, String s, boolean flag, boolean flag1, boolean flag2, int i)
    {
        return new AutoValue_InventoryQuery(uuid, s, flag, flag1, flag2, -1, i);
    }

    public static InventoryQuery create(UUID uuid, String s, boolean flag, boolean flag1, boolean flag2, SLAssetType slassettype)
    {
        int i;
        if (slassettype != null)
        {
            i = slassettype.getTypeCode();
        } else
        {
            i = -1;
        }
        return new AutoValue_InventoryQuery(uuid, s, flag, flag1, flag2, -1, i);
    }

    public static InventoryQuery findFolderWithType(UUID uuid, int i)
    {
        return new AutoValue_InventoryQuery(uuid, null, true, false, false, i, -1);
    }

    public abstract int assetType();

    public abstract String containsString();

    public int describeContents()
    {
        return 0;
    }

    public abstract UUID folderId();

    public abstract int folderType();

    public abstract boolean includeFolders();

    public abstract boolean includeItems();

    public abstract boolean newestFirst();

    public InventoryEntryList query(SLInventoryEntry slinventoryentry, InventoryDB inventorydb)
    {
        String s1 = null;
        Object obj = new ArrayList();
        ArrayList arraylist = new ArrayList();
        if (slinventoryentry != null)
        {
            ((List) (obj)).add("parent_id = ?");
            arraylist.add(Long.toString(slinventoryentry.getId()));
        }
        String s = containsString();
        if (!Strings.isNullOrEmpty(s))
        {
            ((List) (obj)).add("name LIKE ?");
            arraylist.add((new StringBuilder()).append("%").append(s).append("%").toString());
        }
        String as[];
        if (includeFolders() && includeItems() ^ true)
        {
            ((List) (obj)).add(String.format("(isFolder OR (invType == %d AND assetType == %d))", new Object[] {
                Integer.valueOf(8), Integer.valueOf(SLAssetType.AT_LINK_FOLDER.getTypeCode())
            }));
        } else
        if (includeItems() && includeFolders() ^ true)
        {
            ((List) (obj)).add(String.format("(NOT (isFolder OR (invType == %d AND assetType == %d)))", new Object[] {
                Integer.valueOf(8), Integer.valueOf(SLAssetType.AT_LINK_FOLDER.getTypeCode())
            }));
        }
        if (folderType() != -1)
        {
            ((List) (obj)).add("(typeDefault = ?)");
            arraylist.add(Integer.toString(folderType()));
            ((List) (obj)).add("isFolder");
        }
        if (assetType() != -1)
        {
            ((List) (obj)).add(String.format("(isFolder OR assetType == %d)", new Object[] {
                Integer.valueOf(assetType())
            }));
        }
        if (newestFirst())
        {
            s = "creationDate DESC, name";
        } else
        {
            s = "name, creationDate DESC";
        }
        obj = Joiner.on(" AND ").join(((Iterable) (obj)));
        as = (String[])Iterables.toArray(arraylist, java/lang/String);
        if (slinventoryentry != null)
        {
            s1 = slinventoryentry.name;
        }
        return new InventoryEntryList(s1, slinventoryentry, SLInventoryEntry.query(inventorydb.getDatabase(), ((String) (obj)), as, (new StringBuilder()).append("isFolder DESC, (isFolder AND (typeDefault >= 0)) DESC, (assetType == 25) DESC").append(", ").append(s).toString()));
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        Bundle bundle = new Bundle();
        UUID uuid = folderId();
        if (uuid != null)
        {
            bundle.putString("folderId", uuid.toString());
        }
        bundle.putString("containsString", containsString());
        bundle.putBoolean("includeFolders", includeFolders());
        bundle.putBoolean("includeItems", includeItems());
        bundle.putBoolean("newestFirst", newestFirst());
        bundle.putInt("assetType", assetType());
        parcel.writeBundle(bundle);
    }

}
