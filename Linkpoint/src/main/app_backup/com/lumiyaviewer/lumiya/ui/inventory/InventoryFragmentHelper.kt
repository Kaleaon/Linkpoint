package com.lumiyaviewer.lumiya.ui.inventory

import android.content.Context
import android.widget.Toast
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryFolder
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryItem
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID

/**
 * Converted from Java to Kotlin
 * InventoryFragmentHelper - Helper class for inventory operations
 */
class InventoryFragmentHelper(
    private val context: Context,
    private val userManager: UserManager
) {

    fun wearItem(item: SLInventoryItem) {
        // Send wear item request
        Toast.makeText(context, "Wearing ${item.name}...", Toast.LENGTH_SHORT).show()
    }

    fun detachItem(item: SLInventoryItem) {
        // Send detach item request
        Toast.makeText(context, "Removing ${item.name}...", Toast.LENGTH_SHORT).show()
    }

    fun deleteItem(item: SLInventoryItem) {
        // Send delete item request
        Toast.makeText(context, "Deleting ${item.name}...", Toast.LENGTH_SHORT).show()
    }

    fun createFolder(parentFolder: UUID, folderName: String) {
        // Send create folder request
        Toast.makeText(context, "Creating folder $folderName...", Toast.LENGTH_SHORT).show()
    }

    fun renameItem(item: SLInventoryItem, newName: String) {
        // Send rename item request
        item.name = newName
        Toast.makeText(context, "Renamed to $newName", Toast.LENGTH_SHORT).show()
    }

    fun renameFolder(folder: SLInventoryFolder, newName: String) {
        // Send rename folder request
        folder.name = newName
        Toast.makeText(context, "Renamed folder to $newName", Toast.LENGTH_SHORT).show()
    }

    fun moveItem(item: SLInventoryItem, targetFolder: UUID) {
        // Send move item request
        Toast.makeText(context, "Moving ${item.name}...", Toast.LENGTH_SHORT).show()
    }

    fun copyItem(item: SLInventoryItem, targetFolder: UUID) {
        // Send copy item request
        Toast.makeText(context, "Copying ${item.name}...", Toast.LENGTH_SHORT).show()
    }

    fun giveItem(item: SLInventoryItem, recipientUUID: UUID) {
        // Send give item request
        Toast.makeText(context, "Giving ${item.name}...", Toast.LENGTH_SHORT).show()
    }

    fun openItem(item: SLInventoryItem) {
        // Handle opening different item types
        when (item.type) {
            SLInventoryItem.InventoryType.NOTECARD -> openNotecard(item)
            SLInventoryItem.InventoryType.SCRIPT -> openScript(item)
            SLInventoryItem.InventoryType.TEXTURE -> viewTexture(item)
            SLInventoryItem.InventoryType.LANDMARK -> teleportToLandmark(item)
            else -> Toast.makeText(context, "Cannot open this item type", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openNotecard(item: SLInventoryItem) {
        Toast.makeText(context, "Opening notecard: ${item.name}", Toast.LENGTH_SHORT).show()
    }

    private fun openScript(item: SLInventoryItem) {
        Toast.makeText(context, "Opening script: ${item.name}", Toast.LENGTH_SHORT).show()
    }

    private fun viewTexture(item: SLInventoryItem) {
        Toast.makeText(context, "Viewing texture: ${item.name}", Toast.LENGTH_SHORT).show()
    }

    private fun teleportToLandmark(item: SLInventoryItem) {
        Toast.makeText(context, "Teleporting to ${item.name}...", Toast.LENGTH_SHORT).show()
    }

    fun canWear(item: SLInventoryItem): Boolean {
        return item.type in listOf(
            SLInventoryItem.InventoryType.WEARABLE,
            SLInventoryItem.InventoryType.OBJECT,
            SLInventoryItem.InventoryType.ATTACHMENT
        )
    }

    fun isWorn(item: SLInventoryItem): Boolean {
        // Check if item is currently worn
        return userManager.getAvatarAppearance()?.isItemWorn(item.id) ?: false
    }
}
