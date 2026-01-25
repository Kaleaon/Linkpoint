package com.linkpoint.ui.inventory

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.linkpoint.R
import com.linkpoint.inventory.InventoryItem
import java.text.SimpleDateFormat
import java.util.*

/**
 * Dialog showing detailed information about an inventory item
 */
class ItemDetailDialog : DialogFragment() {

    private lateinit var item: InventoryItem

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        item = requireArguments().getParcelable<InventoryItem>("item")
            ?: throw IllegalArgumentException("InventoryItem argument is required")

        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_item_detail, null)

        setupView(view)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(item.name)
            .setView(view)
            .setPositiveButton(R.string.close) { _, _ -> dismiss() }
            .create()
    }

    private fun setupView(view: View) {
        view.findViewById<TextView>(R.id.item_description).text = 
            item.description.ifEmpty { getString(R.string.no_description) }
        
        view.findViewById<TextView>(R.id.item_type).text = 
            item.assetTypeEnum.toString()
        
        view.findViewById<TextView>(R.id.item_inventory_type).text = 
            item.inventoryType.toString()
        
        view.findViewById<TextView>(R.id.item_creation_date).text = 
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date(item.creationDate.toLong() * 1000))
        
        // Permissions
        val permissionsText = buildString {
            append("Base: ${formatPermissions(item.permissions.baseMask)}\n")
            append("Owner: ${formatPermissions(item.permissions.ownerMask)}\n")
            append("Group: ${formatPermissions(item.permissions.groupMask)}\n")
            append("Everyone: ${formatPermissions(item.permissions.everyoneMask)}\n")
            append("Next Owner: ${formatPermissions(item.permissions.nextOwnerMask)}")
        }
        view.findViewById<TextView>(R.id.item_permissions).text = permissionsText
        
        // Sale info
        val saleInfoText = if (item.saleInfo.saleType != 0) {
            "Price: ${item.saleInfo.salePrice} L$\n" +
            "Type: ${item.saleInfo.saleType}"
        } else {
            "Not for sale"
        }
        view.findViewById<TextView>(R.id.item_sale_info).text = saleInfoText
    }

    private fun formatPermissions(mask: Int): String {
        val permissions = mutableListOf<String>()
        if (mask and 0x00000004 != 0) permissions.add("Modify")
        if (mask and 0x00000008 != 0) permissions.add("Transfer")
        if (mask and 0x00000010 != 0) permissions.add("Copy")
        if (mask and 0x00000020 != 0) permissions.add("Everyone Copy")
        if (mask and 0x00000080 != 0) permissions.add("Everyone Modify")
        if (mask and 0x00000100 != 0) permissions.add("Everyone Transfer")
        return if (permissions.isEmpty()) "None" else permissions.joinToString(", ")
    }

    companion object {
        fun newInstance(item: InventoryItem): ItemDetailDialog {
            return ItemDetailDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("item", item)
                }
            }
        }
    }
}