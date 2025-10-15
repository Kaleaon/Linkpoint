package com.lumiyaviewer.lumiya.orm

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import com.google.common.base.Joiner
import com.google.common.base.Strings
import com.google.common.collect.Iterables
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.util.ArrayList
import java.util.UUID
import javax.annotation.Nullable

abstract class InventoryQuery implements Parcelable {
    private int ASSET_TYPE_ANY = -1
    Parcelable.Creator<InventoryQuery> CREATOR = new Parcelable.Creator<InventoryQuery>() {
        fun createFromParcel(parcel: Parcel): InventoryQuery {
            Bundle readBundle = parcel.readBundle(getClass().getClassLoader())
            return InventoryQuery.create(UUIDPool.getUUID(readBundle.getString("folderId")), readBundle.getString("containsString"), readBundle.getBoolean("includeFolders"), readBundle.getBoolean("includeItems"), readBundle.getBoolean("newestFirst"), readBundle.getInt("assetType", -1))
        }

        InventoryQuery[] newArray(int i) {
            return new InventoryQuery[i]
        }
    }
    private int FOLDER_TYPE_ANY = -1

    fun create(uuid: UUID, str: String, z: Boolean, z2: Boolean, z3: Boolean, i: Int): InventoryQuery {
        return AutoValue_InventoryQuery(uuid, str, z, z2, z3, -1, i)
    }

    fun create(uuid: UUID, str: String, z: Boolean, z2: Boolean, z3: Boolean, sLAssetType: SLAssetType): InventoryQuery {
        return AutoValue_InventoryQuery(uuid, str, z, z2, z3, -1, sLAssetType != null ? sLAssetType.getTypeCode() : -1)
    }

    fun findFolderWithType(uuid: UUID, i: Int): InventoryQuery {
        return AutoValue_InventoryQuery(uuid, (String) null, true, false, false, i, -1)
    }

    abstract fun assetType(): Int

    @Nullable
    abstract fun containsString(): String

    fun describeContents(): Int {
        return 0
    }

    @Nullable
    abstract fun folderId(): UUID

    abstract fun folderType(): Int

    abstract fun includeFolders(): Boolean

    abstract fun includeItems(): Boolean

    abstract fun newestFirst(): Boolean

    @SuppressLint({"DefaultLocale"})
    fun query(sLInventoryEntry: SLInventoryEntry, inventoryDB: InventoryDB): InventoryEntryList {
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
            arrayList.add(String.format("(isFolder OR (invType == %d AND assetType == %d))", new Object[]{8, Integer.valueOf(SLAssetType.AT_LINK_FOLDER.getTypeCode())}))
        } else if (includeItems() && (!includeFolders())) {
            arrayList.add(String.format("(NOT (isFolder OR (invType == %d AND assetType == %d)))", new Object[]{8, Integer.valueOf(SLAssetType.AT_LINK_FOLDER.getTypeCode())}))
        }
        if (folderType() != -1) {
            arrayList.add("(typeDefault = ?)")
            arrayList2.add(Integer.toString(folderType()))
            arrayList.add("isFolder")
        }
        if (assetType() != -1) {
            arrayList.add(String.format("(isFolder OR assetType == %d)", new Object[]{Integer.valueOf(assetType())}))
        }
        String str2 = newestFirst() ? "creationDate DESC, name" : "name, creationDate DESC"
        String join = Joiner.on(" AND ").join((Iterable<?>) arrayList)
        String[] strArr = (String[]) Iterables.toArray(arrayList2, String.class)
        if (sLInventoryEntry != null) {
            str = sLInventoryEntry.name
        }
        return InventoryEntryList(str, sLInventoryEntry, SLInventoryEntry.query(inventoryDB.getDatabase(), join, strArr, "isFolder DESC, (isFolder AND (typeDefault >= 0)) DESC, (assetType == 25) DESC" + ", " + str2))
    }

    fun writeToParcel(parcel: Parcel, i: Int): Unit {
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
