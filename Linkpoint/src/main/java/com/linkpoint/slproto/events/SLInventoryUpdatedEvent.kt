package com.linkpoint.slproto.events

class SLInventoryUpdatedEvent(
    private val updatedFolders: Set<Long>?,
    private val updatedItems: Set<Long>?,
    private val needsReload: Boolean
) {
    fun isFolderUpdated(folderId: Long): Boolean {
        return updatedFolders?.contains(folderId) ?: false
    }

    fun isItemUpdated(itemId: Long): Boolean {
        return updatedItems?.contains(itemId) ?: false
    }

    fun isReloadNeeded(): Boolean {
        return needsReload
    }
}