// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.orm;

import android.annotation.SuppressLint;
import com.google.common.collect.Iterables;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import javax.annotation.Nullable;
import java.util.UUID;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

public abstract class InventoryQuery implements Parcelable
{
    private static final int ASSET_TYPE_ANY = -1;
    public static final Parcelable$Creator<InventoryQuery> CREATOR;
    private static final int FOLDER_TYPE_ANY = -1;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<InventoryQuery>() {
            public InventoryQuery createFromParcel(final Parcel parcel) {
                final Bundle bundle = parcel.readBundle(this.getClass().getClassLoader());
                return InventoryQuery.create(UUIDPool.getUUID(bundle.getString("folderId")), bundle.getString("containsString"), bundle.getBoolean("includeFolders"), bundle.getBoolean("includeItems"), bundle.getBoolean("newestFirst"), bundle.getInt("assetType", -1));
            }
            
            public InventoryQuery[] newArray(final int n) {
                return new InventoryQuery[n];
            }
        };
    }
    
    public static InventoryQuery create(@Nullable final UUID uuid, @Nullable final String s, final boolean b, final boolean b2, final boolean b3, final int n) {
        return new AutoValue_InventoryQuery(uuid, s, b, b2, b3, -1, n);
    }
    
    public static InventoryQuery create(@Nullable final UUID uuid, @Nullable final String s, final boolean b, final boolean b2, final boolean b3, @Nullable final SLAssetType slAssetType) {
        int typeCode;
        if (slAssetType != null) {
            typeCode = slAssetType.getTypeCode();
        }
        else {
            typeCode = -1;
        }
        return new AutoValue_InventoryQuery(uuid, s, b, b2, b3, -1, typeCode);
    }
    
    public static InventoryQuery findFolderWithType(@Nullable final UUID uuid, final int n) {
        return new AutoValue_InventoryQuery(uuid, null, true, false, false, n, -1);
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
    
    @SuppressLint({ "DefaultLocale" })
    public InventoryEntryList query(@Nullable final SLInventoryEntry slInventoryEntry, final InventoryDB inventoryDB) {
        String name = null;
        final ArrayList list = new ArrayList();
        final ArrayList list2 = new ArrayList();
        if (slInventoryEntry != null) {
            list.add("parent_id = ?");
            list2.add(Long.toString(slInventoryEntry.getId()));
        }
        final String containsString = this.containsString();
        if (!Strings.isNullOrEmpty(containsString)) {
            list.add("name LIKE ?");
            list2.add("%" + containsString + "%");
        }
        if (this.includeFolders() && (this.includeItems() ^ true)) {
            list.add(String.format("(isFolder OR (invType == %d AND assetType == %d))", 8, SLAssetType.AT_LINK_FOLDER.getTypeCode()));
        }
        else if (this.includeItems() && (this.includeFolders() ^ true)) {
            list.add(String.format("(NOT (isFolder OR (invType == %d AND assetType == %d)))", 8, SLAssetType.AT_LINK_FOLDER.getTypeCode()));
        }
        if (this.folderType() != -1) {
            list.add("(typeDefault = ?)");
            list2.add(Integer.toString(this.folderType()));
            list.add("isFolder");
        }
        if (this.assetType() != -1) {
            list.add(String.format("(isFolder OR assetType == %d)", this.assetType()));
        }
        String str;
        if (this.newestFirst()) {
            str = "creationDate DESC, name";
        }
        else {
            str = "name, creationDate DESC";
        }
        final String join = Joiner.on(" AND ").join(list);
        final String[] array = Iterables.toArray(list2, String.class);
        if (slInventoryEntry != null) {
            name = slInventoryEntry.name;
        }
        return new InventoryEntryList(name, slInventoryEntry, SLInventoryEntry.query(inventoryDB.getDatabase(), join, array, "isFolder DESC, (isFolder AND (typeDefault >= 0)) DESC, (assetType == 25) DESC" + ", " + str));
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        final Bundle bundle = new Bundle();
        final UUID folderId = this.folderId();
        if (folderId != null) {
            bundle.putString("folderId", folderId.toString());
        }
        bundle.putString("containsString", this.containsString());
        bundle.putBoolean("includeFolders", this.includeFolders());
        bundle.putBoolean("includeItems", this.includeItems());
        bundle.putBoolean("newestFirst", this.newestFirst());
        bundle.putInt("assetType", this.assetType());
        parcel.writeBundle(bundle);
    }
}
