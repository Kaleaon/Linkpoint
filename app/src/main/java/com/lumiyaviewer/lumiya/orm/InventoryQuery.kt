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
import java.util.UUID

/**
 * Query parameters for inventory searches
 */
abstract class InventoryQuery : Parcelable {
    
    private val ASSET_TYPE_ANY = -1
    private val FOLDER_TYPE_ANY = -1

    abstract fun assetType(): Int
    abstract fun containsString(): String?
    abstract fun folderId(): UUID?
    abstract fun folderType(): Int
    abstract fun includeFolders(): Boolean
    abstract fun includeItems(): Boolean
    abstract fun newestFirst(): Boolean

    override fun describeContents(): Int = 0

    /**
     * Execute the query
     */
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
        containsString()?.let { search ->
            if (!Strings.isNullOrEmpty(search)) {
                conditions.add("name LIKE ?")
                args.add("%$search%")
            }
        }
        
        // Filter by folders/items
        when {
            includeFolders() && !includeItems() -> {
                conditions.add(
                    String.format(
                        "(isFolder OR (invType == %d AND assetType == %d))",
                        8,
                        SLAssetType.AT_LINK_FOLDER.getTypeCode()
                    )
                )
            }
            includeItems() && !includeFolders() -> {
                conditions.add(
                    String.format(
                        "(NOT (isFolder OR (invType == %d AND assetType == %d)))",
                        8,
                        SLAssetType.AT_LINK_FOLDER.getTypeCode()
                    )
                )
            }
        }
        
        // Filter by folder type
        if (folderType() != FOLDER_TYPE_ANY) {
            conditions.add("(typeDefault = ?)")
            args.add(folderType().toString())
            conditions.add("isFolder")
        }
        
        // Filter by asset type
        if (assetType() != ASSET_TYPE_ANY) {
            conditions.add(
                String.format(
                    "(isFolder OR assetType == %d)",
                    assetType()
                )
            )
        }
        
        // Build order by clause
        val sortOrder = if (newestFirst()) {
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        val bundle = Bundle().apply {
            folderId()?.let { putString("folderId", it.toString()) }
            putString("containsString", containsString())
            putBoolean("includeFolders", includeFolders())
            putBoolean("includeItems", includeItems())
            putBoolean("newestFirst", newestFirst())
            putInt("assetType", assetType())
        }
        parcel.writeBundle(bundle)
    }
    
    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<InventoryQuery> {
            override fun createFromParcel(parcel: Parcel): InventoryQuery {
                val bundle = parcel.readBundle(javaClass.classLoader)!!
                return create(
                    UUIDPool.getUUID(bundle.getString("folderId")),
                    bundle.getString("containsString"),
                    bundle.getBoolean("includeFolders"),
                    bundle.getBoolean("includeItems"),
                    bundle.getBoolean("newestFirst"),
                    bundle.getInt("assetType", -1)
                )
            }

            override fun newArray(size: Int): Array<InventoryQuery?> {
                return arrayOfNulls(size)
            }
        }
        
        /**
         * Create general inventory query
         */
        fun create(
            folderId: UUID?,
            searchString: String?,
            includeFolders: Boolean,
            includeItems: Boolean,
            newestFirst: Boolean,
            assetType: Int
        ): InventoryQuery {
            return AutoValue_InventoryQuery(
                folderId,
                searchString,
                includeFolders,
                includeItems,
                newestFirst,
                -1,
                assetType
            )
        }

        /**
         * Create inventory query with asset type filter
         */
        fun create(
            folderId: UUID?,
            searchString: String?,
            includeFolders: Boolean,
            includeItems: Boolean,
            newestFirst: Boolean,
            assetType: SLAssetType?
        ): InventoryQuery {
            return AutoValue_InventoryQuery(
                folderId,
                searchString,
                includeFolders,
                includeItems,
                newestFirst,
                -1,
                assetType?.getTypeCode() ?: -1
            )
        }

        /**
         * Find folder with specific type
         */
        fun findFolderWithType(folderId: UUID, folderType: Int): InventoryQuery {
            return AutoValue_InventoryQuery(
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
}
