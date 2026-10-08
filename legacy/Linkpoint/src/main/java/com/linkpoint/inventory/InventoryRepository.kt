package com.linkpoint.inventory

import com.linkpoint.inventory.persistence.InventoryFolderDao
import com.linkpoint.inventory.persistence.InventoryFolderEntity
import com.linkpoint.inventory.persistence.InventoryFolderVersionDao
import com.linkpoint.inventory.persistence.InventoryFolderVersionEntity
import com.linkpoint.inventory.persistence.InventoryItemDao
import com.linkpoint.inventory.persistence.InventoryItemEntity
import java.util.UUID

/**
 * Inventory repository with AISv3 primary path and UDP fallback.
 */
class InventoryRepository(
    private val aisClient: AisOperations,
    private val folderDao: InventoryFolderDao,
    private val itemDao: InventoryItemDao,
    private val folderVersionDao: InventoryFolderVersionDao,
    private val udpFallback: UdpInventoryDataSource
) {

    suspend fun getSubtree(folderId: UUID, knownFolderVersion: Int? = null): InventorySubtreeData {
        val cachedVersion = folderVersionDao.getVersion(folderId.toString())?.version
        val hasCache = folderDao.getFolder(folderId.toString()) != null

        if (hasCache && (knownFolderVersion == null || knownFolderVersion == cachedVersion)) {
            return cachedSubtree(folderId)
        }

        val remote = try {
            aisClient.getSubtree(folderId)
        } catch (_: AisCapabilityUnavailableException) {
            udpFallback.fetchInventoryDescendents2(folderId)
        }

        persistSubtree(remote)
        return remote
    }

    suspend fun getItem(itemId: UUID, parentFolderIdForFallback: UUID): InventoryItemData? {
        val cached = itemDao.getItem(itemId.toString())?.toDomain()
        if (cached != null) return cached

        val remote = try {
            aisClient.getItem(itemId)
        } catch (_: AisCapabilityUnavailableException) {
            val subtree = udpFallback.fetchInventoryDescendents2(parentFolderIdForFallback)
            subtree.items.firstOrNull { it.itemId == itemId } ?: return null
        }

        itemDao.upsert(remote.toEntity())
        return remote
    }

    suspend fun patchItem(itemId: UUID, patch: InventoryItemPatch): Boolean {
        aisClient.patchItem(itemId, patch)
        val existing = itemDao.getItem(itemId.toString()) ?: return true
        itemDao.upsert(
            existing.copy(
                name = patch.name ?: existing.name,
                parentId = patch.parentId ?: existing.parentId,
                updatedAtEpochMs = System.currentTimeMillis()
            )
        )
        return true
    }

    suspend fun createFolder(parentId: UUID, name: String, type: Int = -1): UUID {
        val createdId = aisClient.postFolder(CreateFolderRequest(parentId.toString(), name, type))
        folderDao.upsert(
            InventoryFolderEntity(
                folderId = createdId.toString(),
                parentId = parentId.toString(),
                name = name,
                type = type,
                version = 0,
                updatedAtEpochMs = System.currentTimeMillis()
            )
        )
        return createdId
    }

    suspend fun deleteItem(itemId: UUID): Boolean {
        aisClient.deleteNode("item/$itemId")
        itemDao.delete(itemId.toString())
        return true
    }

    suspend fun deleteFolder(folderId: UUID): Boolean {
        aisClient.deleteNode("category/$folderId")
        folderDao.delete(folderId.toString())
        folderVersionDao.delete(folderId.toString())
        return true
    }

    private suspend fun persistSubtree(subtree: InventorySubtreeData) {
        val now = System.currentTimeMillis()
        folderDao.upsert(
            InventoryFolderEntity(
                folderId = subtree.folder.folderId.toString(),
                parentId = subtree.folder.parentId.toString(),
                name = subtree.folder.name,
                type = subtree.folder.type,
                version = subtree.folder.version,
                updatedAtEpochMs = now
            )
        )
        folderDao.deleteChildrenOfParent(subtree.folder.folderId.toString())
        itemDao.deleteByParentId(subtree.folder.folderId.toString())

        folderDao.upsertAll(
            subtree.folders.map {
                InventoryFolderEntity(
                    folderId = it.folderId.toString(),
                    parentId = it.parentId.toString(),
                    name = it.name,
                    type = it.type,
                    version = it.version,
                    updatedAtEpochMs = now
                )
            }
        )
        itemDao.upsertAll(subtree.items.map { it.toEntity(now) })
        folderVersionDao.upsert(
            InventoryFolderVersionEntity(
                folderId = subtree.folder.folderId.toString(),
                version = subtree.folder.version,
                updatedAtEpochMs = now
            )
        )
    }

    private suspend fun cachedSubtree(folderId: UUID): InventorySubtreeData {
        val folder = requireNotNull(folderDao.getFolder(folderId.toString()))
        return InventorySubtreeData(
            folder = folder.toDomain(),
            folders = folderDao.getFoldersByParent(folderId.toString()).map { it.toDomain() },
            items = itemDao.getItemsByParent(folderId.toString()).map { it.toDomain() }
        )
    }
}

interface UdpInventoryDataSource {
    suspend fun fetchInventoryDescendents2(folderId: UUID): InventorySubtreeData
}

private fun InventoryFolderEntity.toDomain(): InventoryFolderData =
    InventoryFolderData(
        folderId = UUID.fromString(folderId),
        parentId = UUID.fromString(parentId),
        name = name,
        version = version,
        type = type
    )

private fun InventoryItemEntity.toDomain(): InventoryItemData =
    InventoryItemData(
        itemId = UUID.fromString(itemId),
        parentId = UUID.fromString(parentId),
        name = name,
        assetId = UUID.fromString(assetId),
        inventoryType = inventoryType,
        assetType = assetType,
        version = version
    )

private fun InventoryItemData.toEntity(now: Long = System.currentTimeMillis()): InventoryItemEntity =
    InventoryItemEntity(
        itemId = itemId.toString(),
        parentId = parentId.toString(),
        name = name,
        assetId = assetId.toString(),
        inventoryType = inventoryType,
        assetType = assetType,
        version = version,
        updatedAtEpochMs = now
    )
