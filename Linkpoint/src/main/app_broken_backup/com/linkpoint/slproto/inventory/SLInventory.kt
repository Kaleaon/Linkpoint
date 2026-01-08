package com.linkpoint.slproto.inventory

import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

/**
 * Converted from Java to Kotlin
 * SLInventory - Manages user inventory structure and items
 */
class SLInventory(private val ownerUUID: UUID) {

    companion object {
        // System folder types
        const val FOLDER_TYPE_NONE = 0
        const val FOLDER_TYPE_TEXTURE = 1
        const val FOLDER_TYPE_SOUND = 2
        const val FOLDER_TYPE_CALLING_CARD = 3
        const val FOLDER_TYPE_LANDMARK = 4
        const val FOLDER_TYPE_CLOTHING = 5
        const val FOLDER_TYPE_OBJECT = 6
        const val FOLDER_TYPE_NOTECARD = 7
        const val FOLDER_TYPE_ROOT = 8
        const val FOLDER_TYPE_SCRIPT = 9
        const val FOLDER_TYPE_BODY_PART = 10
        const val FOLDER_TYPE_TRASH = 11
        const val FOLDER_TYPE_SNAPSHOT = 12
        const val FOLDER_TYPE_LOST_AND_FOUND = 13
        const val FOLDER_TYPE_ANIMATION = 14
        const val FOLDER_TYPE_GESTURE = 15
        const val FOLDER_TYPE_FAVORITES = 16
        const val FOLDER_TYPE_ENSEMBLE_START = 17
        const val FOLDER_TYPE_ENSEMBLE_END = 45
        const val FOLDER_TYPE_CURRENT_OUTFIT = 46
        const val FOLDER_TYPE_OUTFIT = 47
        const val FOLDER_TYPE_MY_OUTFITS = 48
        const val FOLDER_TYPE_MESH = 49
        const val FOLDER_TYPE_INBOX = 50
        const val FOLDER_TYPE_OUTBOX = 51
        const val FOLDER_TYPE_BASIC_ROOT = 52
        const val FOLDER_TYPE_MARKETPLACE_LISTINGS = 53
        const val FOLDER_TYPE_MARKETPLACE_STOCK = 54
        const val FOLDER_TYPE_SETTINGS = 56
    }

    data class SLInventoryFolder(
        val id: UUID,
        var name: String,
        val parentId: UUID?,
        val type: Int,
        val version: Int = 0
    ) {
        private val childFolders = mutableListOf<SLInventoryFolder>()
        private val childItems = mutableListOf<SLInventoryItem>()

        fun addChildFolder(folder: SLInventoryFolder) {
            childFolders.add(folder)
        }

        fun removeChildFolder(folderId: UUID) {
            childFolders.removeIf { it.id == folderId }
        }

        fun getChildFolders(): List<SLInventoryFolder> {
            return childFolders.toList()
        }

        fun addChildItem(item: SLInventoryItem) {
            childItems.add(item)
        }

        fun removeChildItem(itemId: UUID) {
            childItems.removeIf { it.id == itemId }
        }

        fun getChildItems(): List<SLInventoryItem> {
            return childItems.toList()
        }

        fun getItemCount(): Int {
            return childItems.size
        }

        fun getFolderCount(): Int {
            return childFolders.size
        }

        fun getTotalItemCount(): Int {
            var count = childItems.size
            childFolders.forEach { count += it.getTotalItemCount() }
            return count
        }
    }

    data class SLInventoryItem(
        val id: UUID,
        var name: String,
        val parentId: UUID,
        val type: InventoryType,
        val assetUUID: UUID,
        val creatorUUID: UUID,
        val ownerUUID: UUID,
        val groupUUID: UUID?,
        val permissions: ItemPermissions,
        var description: String = "",
        val creationDate: Long = System.currentTimeMillis()
    ) {
        enum class InventoryType {
            TEXTURE,
            SOUND,
            CALLING_CARD,
            LANDMARK,
            CLOTHING,
            OBJECT,
            NOTECARD,
            CATEGORY,
            ROOT_CATEGORY,
            LSL_TEXT,
            LSL_BYTECODE,
            TEXTURE_TGA,
            BODY_PART,
            TRASH,
            SNAPSHOT_CATEGORY,
            LOST_AND_FOUND,
            SOUND_WAV,
            IMAGE_TGA,
            IMAGE_JPEG,
            ANIMATION,
            GESTURE,
            SIMSTATE,
            FAVORITE,
            LINK,
            LINK_FOLDER,
            FOR_SALE,
            MARKETPLACE_FOLDER,
            WEARABLE,
            ATTACHMENT,
            SCRIPT,
            MESH
        }

        data class ItemPermissions(
            val baseMask: Int,
            val ownerMask: Int,
            val groupMask: Int,
            val everyoneMask: Int,
            val nextOwnerMask: Int
        ) {
            fun canCopy(): Boolean = (ownerMask and PERM_COPY) != 0
            fun canModify(): Boolean = (ownerMask and PERM_MODIFY) != 0
            fun canTransfer(): Boolean = (ownerMask and PERM_TRANSFER) != 0

            companion object {
                const val PERM_NONE = 0
                const val PERM_TRANSFER = 1 shl 13
                const val PERM_MODIFY = 1 shl 14
                const val PERM_COPY = 1 shl 15
                const val PERM_EXPORT = 1 shl 16
                const val PERM_MOVE = 1 shl 19
                const val PERM_ALL = 0x7FFFFFFF
            }
        }
    }

    private var rootFolder: SLInventoryFolder? = null
    private val folderCache = mutableMapOf<UUID, SLInventoryFolder>()
    private val itemCache = mutableMapOf<UUID, SLInventoryItem>()
    private val systemFolders = mutableMapOf<Int, UUID>()

    fun setRootFolder(folder: SLInventoryFolder) {
        this.rootFolder = folder
        folderCache[folder.id] = folder
    }

    fun getRootFolder(): SLInventoryFolder? {
        return rootFolder
    }

    fun addFolder(folder: SLInventoryFolder) {
        folderCache[folder.id] = folder
        folder.parentId?.let { parentId ->
            folderCache[parentId]?.addChildFolder(folder)
        }
    }

    fun removeFolder(folderId: UUID) {
        folderCache.remove(folderId)?.let { folder ->
            folder.parentId?.let { parentId ->
                folderCache[parentId]?.removeChildFolder(folderId)
            }
        }
    }

    fun getFolder(folderId: UUID): SLInventoryFolder? {
        return folderCache[folderId]
    }

    fun addItem(item: SLInventoryItem) {
        itemCache[item.id] = item
        folderCache[item.parentId]?.addChildItem(item)
    }

    fun removeItem(itemId: UUID) {
        itemCache.remove(itemId)?.let { item ->
            folderCache[item.parentId]?.removeChildItem(itemId)
        }
    }

    fun getItem(itemId: UUID): SLInventoryItem? {
        return itemCache[itemId]
    }

    fun moveItem(itemId: UUID, newParentId: UUID) {
        itemCache[itemId]?.let { item ->
            folderCache[item.parentId]?.removeChildItem(itemId)
            itemCache[itemId] = item.copy(parentId = newParentId)
            folderCache[newParentId]?.addChildItem(item)
        }
    }

    fun moveFolder(folderId: UUID, newParentId: UUID) {
        folderCache[folderId]?.let { folder ->
            folder.parentId?.let { oldParentId ->
                folderCache[oldParentId]?.removeChildFolder(folderId)
            }
            folderCache[folderId] = folder.copy(parentId = newParentId)
            folderCache[newParentId]?.addChildFolder(folder)
        }
    }

    fun setSystemFolder(folderType: Int, folderId: UUID) {
        systemFolders[folderType] = folderId
    }

    fun getSystemFolder(folderType: Int): SLInventoryFolder? {
        return systemFolders[folderType]?.let { folderId ->
            folderCache[folderId]
        }
    }

    fun findItemsByName(name: String): List<SLInventoryItem> {
        return itemCache.values.filter { it.name.contains(name, ignoreCase = true) }
    }

    fun findItemsByType(type: SLInventoryItem.InventoryType): List<SLInventoryItem> {
        return itemCache.values.filter { it.type == type }
    }

    fun getTotalItemCount(): Int {
        return itemCache.size
    }

    fun getTotalFolderCount(): Int {
        return folderCache.size
    }

    fun clear() {
        rootFolder = null
        folderCache.clear()
        itemCache.clear()
        systemFolders.clear()
    }
}
