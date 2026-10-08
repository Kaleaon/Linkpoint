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
        val context = anchorView.context
        val popup = PopupMenu(context, anchorView, Gravity.END)

        popup.menuInflater.inflate(R.menu.menu_inventory_item, popup.menu)

        // Enable/disable items based on permissions
        val canCopy = item.permissions.canCopy
        val canModify = item.permissions.canModify
        val canTransfer = item.permissions.canTransfer
        val canDelete = canModify && canTransfer

        val copyItem = popup.menu.findItem(R.id.action_copy)
        copyItem?.isEnabled = canCopy
        if (!canCopy) {
            copyItem?.tooltipText = context.getString(R.string.inventory_item_no_copy_tooltip)
        } else {
            copyItem?.tooltipText = null
        }

        val deleteItem = popup.menu.findItem(R.id.action_delete)
        deleteItem?.isEnabled = canDelete
        if (!canDelete) {
            deleteItem?.tooltipText = when {
                !canModify && !canTransfer -> context.getString(R.string.inventory_item_no_modify_no_transfer_tooltip)
                !canModify -> context.getString(R.string.inventory_item_no_modify_tooltip)
                else -> context.getString(R.string.inventory_item_no_transfer_tooltip)
            }
        } else {
            deleteItem?.tooltipText = null
        }

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
