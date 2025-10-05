package com.linkpoint.orm

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import com.google.common.base.Joiner
import com.google.common.base.Strings
import com.google.common.collect.Iterables
import com.linkpoint.slproto.inventory.SLAssetType
import com.linkpoint.slproto.inventory.SLInventoryEntry
import com.linkpoint.utils.UUIDPool
import java.util.ArrayList
import java.util.UUID
import javax.annotation.Nullable

abstract class InventoryQuery : Parcelable {
    private const val Int ASSET_TYPE_ANY = -1
    const val Parcelable.Creator<InventoryQuery> CREATOR = Parcelable.Creator<InventoryQuery>() {
        public InventoryQuery createFromParcel(Parcel parcel) {
            Bundle readBundle = parcel.readBundle(getClass().getClassLoader())
            return InventoryQuery.create(UUIDPool.getUUID(readBundle.getString("folderId")), readBundle.getString("containsString"), readBundle.getBoolean("includeFolders"), readBundle.getBoolean("includeItems"), readBundle.getBoolean("newestFirst"), readBundle.getInt("assetType", -1))
        }

        public InventoryQuery[] newArray(Int i) {
            return InventoryQuery[i]
        }
    }
    private const val Int FOLDER_TYPE_ANY = -1

    @JvmStatic
    InventoryQuery create(UUID uuid, String str, Boolean z, Boolean z2, Boolean z3, Int i) {
        return AutoValue_InventoryQuery(uuid, str, z, z2, z3, -1, i)
    }

    @JvmStatic
    InventoryQuery create(UUID uuid, String str, Boolean z, Boolean z2, Boolean z3, SLAssetType sLAssetType) {
        return AutoValue_InventoryQuery(uuid, str, z, z2, z3, -1, sLAssetType != null ? sLAssetType.getTypeCode() : -1)
    }

    @JvmStatic
    InventoryQuery findFolderWithType(UUID uuid, Int i) {
        return AutoValue_InventoryQuery(uuid, (String) null, true, false, false, i, -1)
    }

    public abstract Int assetType()

    public abstract String containsString()

    public Int describeContents() {
        return 0
    }

    public abstract UUID folderId()

    public abstract Int folderType()

    public abstract Boolean includeFolders()

    public abstract Boolean includeItems()

    public abstract Boolean newestFirst()

    @SuppressLint({"DefaultLocale"})
    public InventoryEntryList query(SLInventoryEntry sLInventoryEntry, InventoryDB inventoryDB) {
        String str = null
        ArrayList arrayList = ArrayList()
        ArrayList arrayList2 = ArrayList()
        if (sLInventoryEntry != null) {
            arrayList.add("parent_id = ?")
            arrayList2.add(Long.toString(sLInventoryEntry.getId()))
        }
        String containsString = containsString()
        if (!Strings.isNullOrEmpty(containsString)) {
            arrayList.add("name LIKE ?")
            arrayList2.add("%" + containsString + "%")
        }
        if (includeFolders() && (!includeItems())) {
            arrayList.add(String.format("(isFolder OR (invType == %d AND assetType == %d))", Object[]{8, Integer.valueOf(SLAssetType.AT_LINK_FOLDER.getTypeCode())}))
        } else if (includeItems() && (!includeFolders())) {
            arrayList.add(String.format("(NOT (isFolder OR (invType == %d AND assetType == %d)))", Object[]{8, Integer.valueOf(SLAssetType.AT_LINK_FOLDER.getTypeCode())}))
        }
        if (folderType() != -1) {
            arrayList.add("(typeDefault = ?)")
            arrayList2.add(Integer.toString(folderType()))
            arrayList.add("isFolder")
        }
        if (assetType() != -1) {
            arrayList.add(String.format("(isFolder OR assetType == %d)", Object[]{Integer.valueOf(assetType())}))
        }
        String str2 = newestFirst() ? "creationDate DESC, name" : "name, creationDate DESC"
        String join = Joiner.on(" AND ").join((Iterable<?>) arrayList)
        String[] strArr = (String[]) Iterables.toArray(arrayList2, String.class)
        if (sLInventoryEntry != null) {
            str = sLInventoryEntry.name
        }
        return InventoryEntryList(str, sLInventoryEntry, SLInventoryEntry.query(inventoryDB.getDatabase(), join, strArr, "isFolder DESC, (isFolder AND (typeDefault >= 0)) DESC, (assetType == 25) DESC" + ", " + str2))
    }

    public Unit writeToParcel(Parcel parcel, Int i) {
        Bundle bundle = Bundle()
        UUID folderId = folderId()
        if (folderId != null) {
            bundle.putString("folderId", folderId.toString())
        }
        bundle.putString("containsString", containsString())
        bundle.putBoolean("includeFolders", includeFolders())
        bundle.putBoolean("includeItems", includeItems())
        bundle.putBoolean("newestFirst", newestFirst())
        bundle.putInt("assetType", assetType())
        parcel.writeBundle(bundle)
    }
}
