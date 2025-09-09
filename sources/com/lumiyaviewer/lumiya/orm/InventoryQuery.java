package com.lumiyaviewer.lumiya.orm;

import android.annotation.SuppressLint;
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
import java.util.UUID;
import javax.annotation.Nullable;

public abstract class InventoryQuery implements Parcelable {
    private static final int ASSET_TYPE_ANY = -1;
    public static final Parcelable.Creator<InventoryQuery> CREATOR = new Parcelable.Creator<InventoryQuery>() {
        public InventoryQuery createFromParcel(Parcel parcel) {
            Bundle readBundle = parcel.readBundle(getClass().getClassLoader());
            return InventoryQuery.create(UUIDPool.getUUID(readBundle.getString("folderId")), readBundle.getString("containsString"), readBundle.getBoolean("includeFolders"), readBundle.getBoolean("includeItems"), readBundle.getBoolean("newestFirst"), readBundle.getInt("assetType", -1));
        }

        public InventoryQuery[] newArray(int i) {
            return new InventoryQuery[i];
        }
    };
    private static final int FOLDER_TYPE_ANY = -1;

    public static InventoryQuery create(@Nullable UUID uuid, @Nullable String str, boolean z, boolean z2, boolean z3, int i) {
        return new AutoValue_InventoryQuery(uuid, str, z, z2, z3, -1, i);
    }

    public static InventoryQuery create(@Nullable UUID uuid, @Nullable String str, boolean z, boolean z2, boolean z3, @Nullable SLAssetType sLAssetType) {
        return new AutoValue_InventoryQuery(uuid, str, z, z2, z3, -1, sLAssetType != null ? sLAssetType.getTypeCode() : -1);
    }

    public static InventoryQuery findFolderWithType(@Nullable UUID uuid, int i) {
        return new AutoValue_InventoryQuery(uuid, (String) null, true, false, false, i, -1);
    }

    public abstract int assetType();

    @Nullable
    public abstract String containsString();

    public int describeContents() {
        return 0;
    }

    @Nullable
    public abstract UUID folderId();

    public abstract int folderType();

    public abstract boolean includeFolders();

    public abstract boolean includeItems();

    public abstract boolean newestFirst();

    @SuppressLint({"DefaultLocale"})
    public InventoryEntryList query(@Nullable SLInventoryEntry sLInventoryEntry, InventoryDB inventoryDB) {
        String str = null;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        if (sLInventoryEntry != null) {
            arrayList.add("parent_id = ?");
            arrayList2.add(Long.toString(sLInventoryEntry.getId()));
        }
        String containsString = containsString();
        if (!Strings.isNullOrEmpty(containsString)) {
            arrayList.add("name LIKE ?");
            arrayList2.add("%" + containsString + "%");
        }
        if (includeFolders() && (!includeItems())) {
            arrayList.add(String.format("(isFolder OR (invType == %d AND assetType == %d))", new Object[]{8, Integer.valueOf(SLAssetType.AT_LINK_FOLDER.getTypeCode())}));
        } else if (includeItems() && (!includeFolders())) {
            arrayList.add(String.format("(NOT (isFolder OR (invType == %d AND assetType == %d)))", new Object[]{8, Integer.valueOf(SLAssetType.AT_LINK_FOLDER.getTypeCode())}));
        }
        if (folderType() != -1) {
            arrayList.add("(typeDefault = ?)");
            arrayList2.add(Integer.toString(folderType()));
            arrayList.add("isFolder");
        }
        if (assetType() != -1) {
            arrayList.add(String.format("(isFolder OR assetType == %d)", new Object[]{Integer.valueOf(assetType())}));
        }
        String str2 = newestFirst() ? "creationDate DESC, name" : "name, creationDate DESC";
        String join = Joiner.on(" AND ").join((Iterable<?>) arrayList);
        String[] strArr = (String[]) Iterables.toArray(arrayList2, String.class);
        if (sLInventoryEntry != null) {
            str = sLInventoryEntry.name;
        }
        return new InventoryEntryList(str, sLInventoryEntry, SLInventoryEntry.query(inventoryDB.getDatabase(), join, strArr, "isFolder DESC, (isFolder AND (typeDefault >= 0)) DESC, (assetType == 25) DESC" + ", " + str2));
    }

    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        UUID folderId = folderId();
        if (folderId != null) {
            bundle.putString("folderId", folderId.toString());
        }
        bundle.putString("containsString", containsString());
        bundle.putBoolean("includeFolders", includeFolders());
        bundle.putBoolean("includeItems", includeItems());
        bundle.putBoolean("newestFirst", newestFirst());
        bundle.putInt("assetType", assetType());
        parcel.writeBundle(bundle);
    }
}
