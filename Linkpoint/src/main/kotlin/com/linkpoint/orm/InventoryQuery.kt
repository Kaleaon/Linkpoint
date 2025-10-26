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
    private const val ASSET_TYPE_ANY: Int = -1
    const val Parcelable.Creator<InventoryQuery> CREATOR = Parcelable.Creator<InventoryQuery>() {
         public fun createFromParcel(parcel: Parcel): InventoryQuery {
            val readBundle: Bundle = parcel.readBundle(getClass().getClassLoader())
            return InventoryQuery.create(UUIDPool.getUUID(readBundle.getString("folderId")), readBundle.getString("containsString"), readBundle.getBoolean("includeFolders"), readBundle.getBoolean("includeItems"), readBundle.getBoolean("newestFirst"), readBundle.getInt("assetType", -1))
        }

        public Array<InventoryQuery> newArray(Int i) {
            return InventoryQuery[i]
        }
    }
    private const val FOLDER_TYPE_ANY: Int = -1

    @JvmStatic
     fun create(uuid: UUID, str: String, z: Boolean, z2: Boolean, z3: Boolean, i: Int): InventoryQuery {
        return AutoValue_InventoryQuery(uuid, str, z, z2, z3, -1, i)
    }

    @JvmStatic
     fun create(uuid: UUID, str: String, z: Boolean, z2: Boolean, z3: Boolean, sLAssetType: SLAssetType): InventoryQuery {
        return AutoValue_InventoryQuery(uuid, str, z, z2, z3, -1, sLAssetType != null ? sLAssetType.getTypeCode() : -1)
    }

    @JvmStatic
     fun findFolderWithType(uuid: UUID, i: Int): InventoryQuery {
        return AutoValue_InventoryQuery(uuid, (String) null, true, false, false, i, -1)
    }

    public abstract Int assetType()

    public abstract String containsString()

     public fun describeContents(): Int {
        return 0
    }

    public abstract UUID folderId()

    public abstract Int folderType()

    public abstract Boolean includeFolders()

    public abstract Boolean includeItems()

    public abstract Boolean newestFirst()

    @SuppressLint({"DefaultLocale"})
     public fun query(sLInventoryEntry: SLInventoryEntry, inventoryDB: InventoryDB): InventoryEntryList {
        val str: String = null
        val arrayList: ArrayList = ArrayList()
        val arrayList2: ArrayList = ArrayList()
        if (sLInventoryEntry != null) {
            arrayList.add("parent_id = ?")
            arrayList2.add(Long.toString(sLInventoryEntry.getId()))
        }
        val containsString: String = containsString()
        if (!Strings.isNullOrEmpty(containsString)) {
            arrayList.add("name LIKE ?")
            arrayList2.add("%" + containsString + "%")
        }
        if (includeFolders() && (!includeItems())) {
            arrayList.add(String.format("(isFolder OR (invType == %d AND assetType == %d))", Array<Any>{8, Integer.valueOf(SLAssetType.AT_LINK_FOLDER.getTypeCode())}))
        } else if (includeItems() && (!includeFolders())) {
            arrayList.add(String.format("(NOT (isFolder OR (invType == %d AND assetType == %d)))", Array<Any>{8, Integer.valueOf(SLAssetType.AT_LINK_FOLDER.getTypeCode())}))
        }
        if (folderType() != -1) {
            arrayList.add("(typeDefault = ?)")
            arrayList2.add(Integer.toString(folderType()))
            arrayList.add("isFolder")
        }
        if (assetType() != -1) {
            arrayList.add(String.format("(isFolder OR assetType == %d)", Array<Any>{Integer.valueOf(assetType())}))
        }
        val str2: String = newestFirst() ? "creationDate DESC, name" : "name, creationDate DESC"
        val join: String = Joiner.on(" AND ").join((Iterable<?>) arrayList)
        val strArr: Array<String> = (Array<String>) Iterables.toArray(arrayList2, String.class)
        if (sLInventoryEntry != null) {
            str = sLInventoryEntry.name
        }
        return InventoryEntryList(str, sLInventoryEntry, SLInventoryEntry.query(inventoryDB.getDatabase(), join, strArr, "isFolder DESC, (isFolder AND (typeDefault >= 0)) DESC, (assetType == 25) DESC" + ", " + str2))
    }

    fun writeToParcel(parcel: Parcel, i: Int) {
        val bundle: Bundle = Bundle()
        val folderId: UUID = folderId()
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
