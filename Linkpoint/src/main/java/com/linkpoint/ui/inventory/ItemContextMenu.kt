package com.linkpoint.ui.inventory

import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import com.linkpoint.R
import com.linkpoint.inventory.InventoryItem

/**
 * Context menu for inventory items
 */
object ItemContextMenu {

    enum class Action {
        WEAR,
        COPY,
        DELETE,
        PROPERTIES
    }

    fun show(
        anchorView: View,
        item: InventoryItem,
        onActionSelected: (Action) -> Unit
    ) {
        val popup = PopupMenu(anchorView.context, anchorView, Gravity.END)
        
        popup.menuInflater.inflate(R.menu.menu_inventory_item, popup.menu)
        
        // Enable/disable items based on permissions
        val canCopy = item.permissions.ownerMask and 0x00000010 != 0
        val canModify = item.permissions.ownerMask and 0x00000004 != 0
        val canTransfer = item.permissions.ownerMask and 0x00000008 != 0
        
        popup.menu.findItem(R.id.action_copy)?.isEnabled = canCopy
        popup.menu.findItem(R.id.action_delete)?.isEnabled = canModify && canTransfer
        
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_wear -> {
                    onActionSelected(Action.WEAR)
                    true
                }
                R.id.action_copy -> {
                    onActionSelected(Action.COPY)
                    true
                }
                R.id.action_delete -> {
                    onActionSelected(Action.DELETE)
                    true
                }
                R.id.action_properties -> {
                    onActionSelected(Action.PROPERTIES)
                    true
                }
                else -> false
            }
        }
        
        popup.show()
    }
}