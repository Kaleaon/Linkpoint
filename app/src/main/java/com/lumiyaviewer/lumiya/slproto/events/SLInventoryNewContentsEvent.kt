package com.lumiyaviewer.lumiya.slproto.events

import java.util.UUID

class SLInventoryNewContentsEvent {
    var firstIsFolder = false
    var firstItemName: String? = null
    var firstParentUUID: UUID? = null
    var newFolderCount = 0
    var newItemCount = 0
    
    fun AddItem(isFolder: Boolean, parentUUID: UUID, itemName: String) {
        if (isFolder) {
            newFolderCount++
        } else {
            newItemCount++
        }
        
        if (firstParentUUID == null) {
            firstIsFolder = isFolder
            firstParentUUID = parentUUID
            firstItemName = itemName
        } else if (isFolder && !firstIsFolder) {
            firstIsFolder = isFolder
            firstParentUUID = parentUUID
            firstItemName = itemName
        }
    }
    
    fun isEmpty(): Boolean = newFolderCount == 0 && newItemCount == 0
}