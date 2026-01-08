package com.lumiyaviewer.lumiya.orm

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import com.google.common.base.Joiner
import com.google.common.base.Strings
import com.google.common.collect.Iterables
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.util.UUID

/**
 * Query parameters for inventory searches
 */
data class InventoryQuery(
    val folderId: UUID?,
    val containsString: String?,
    val includeFolders: Boolean,
    val includeItems: Boolean,
    val newestFirst: Boolean,
    val folderType: Int,
    val assetType: Int
) : Parcelable {

    private val FOLDER_TYPE_ANY = -1
    private val ASSET_TYPE_ANY = -1

    constructor(parcel: Parcel) : this(
        UUIDPool.getUUID(parcel.readString()),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(folderId?.toString())
        parcel.writeString(containsString)
        parcel.writeByte(if (includeFolders) 1 else 0)
        parcel.writeByte(if (includeItems) 1 else 0)
        parcel.writeByte(if (newestFirst) 1 else 0)
        parcel.writeInt(folderType)
        parcel.writeInt(assetType)
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<InventoryQuery> {
            override fun createFromParcel(parcel: Parcel): InventoryQuery {
                return InventoryQuery(parcel)
            }

            override fun newArray(size: Int): Array<InventoryQuery?> {
                return arrayOfNulls(size)
            }
        }

        fun create(
            folderId: UUID?,
            searchString: String?,
            includeFolders: Boolean,
            includeItems: Boolean,
            newestFirst: Boolean,
            assetType: Int
        ): InventoryQuery {
            return InventoryQuery(
                folderId,
                searchString,
                includeFolders,
                includeItems,
                newestFirst,
                -1,
                assetType
            )
        }

        fun create(
            folderId: UUID?,
            searchString: String?,
            includeFolders: Boolean,
            includeItems: Boolean,
            newestFirst: Boolean,
            assetType: SLAssetType?
        ): InventoryQuery {
            return InventoryQuery(
                folderId,
                searchString,
                includeFolders,
                includeItems,
                newestFirst,
                -1,
                assetType?.typeCode ?: -1
            )
        }

        fun findFolderWithType(folderId: UUID, folderType: Int): InventoryQuery {
            return InventoryQuery(
                folderId,
                null,
                true,
                false,
                false,
                folderType,
                -1
            )
        }
    }

    @SuppressLint("DefaultLocale")
    fun query(folder: SLInventoryEntry?, inventoryDB: InventoryDB): InventoryEntryList {
        val conditions = mutableListOf<String>()
        val args = mutableListOf<String>()
        
        // Filter by parent folder
        folder?.let {
            conditions.add("parent_id = ?")
            args.add(it.getId().toString())
        }
        
        // Filter by name
        containsString?.let { search ->
            if (!Strings.isNullOrEmpty(search)) {
                conditions.add("name LIKE ?")
                args.add("%$search%")
            }
        }
        
        // Filter by folders/items
        when {
            includeFolders && !includeItems -> {
                conditions.add(
                    String.format(
                        "(isFolder OR (invType == %d AND assetType == %d))",
                        8,
                        SLAssetType.AT_LINK_FOLDER.typeCode
                    )
                )
            }
            includeItems && !includeFolders -> {
                conditions.add(
                    String.format(
                        "(NOT (isFolder OR (invType == %d AND assetType == %d)))",
                        8,
                        SLAssetType.AT_LINK_FOLDER.typeCode
                    )
                )
            }
        }
        
        // Filter by folder type
        if (folderType != FOLDER_TYPE_ANY) {
            conditions.add("(typeDefault = ?)")
            args.add(folderType.toString())
            conditions.add("isFolder")
        }
        
        // Filter by asset type
        if (assetType != ASSET_TYPE_ANY) {
            conditions.add(
                String.format(
                    "(isFolder OR assetType == %d)",
                    assetType
                )
            )
        }
        
        // Build order by clause
        val sortOrder = if (newestFirst) {
            "creationDate DESC, name"
        } else {
            "name, creationDate DESC"
        }
        
        val finalOrder = "isFolder DESC, (isFolder AND (typeDefault >= 0)) DESC, " +
                        "(assetType == 25) DESC, $sortOrder"
        
        val whereClause = Joiner.on(" AND ").join(conditions)
        val whereArgs = Iterables.toArray(args, String::class.java)
        
        val results = SLInventoryEntry.query(
            inventoryDB.getDatabase(),
            whereClause,
            whereArgs,
            finalOrder
        )
        
        return InventoryEntryList(folder?.name, folder, results)
    }
}
