package com.linkpoint.ui.inventory

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.linkpoint.R
import com.linkpoint.inventory.InventoryItem

/**
 * Dialog showing item properties
 */
class ItemPropertiesDialog : DialogFragment() {

    private lateinit var item: InventoryItem

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        item = requireArguments().getParcelable<InventoryItem>("item")
            ?: throw IllegalArgumentException("InventoryItem argument is required")

        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_item_properties, null)

        setupView(view)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.item_properties)
            .setView(view)
            .setPositiveButton(R.string.close) { _, _ -> dismiss() }
            .create()
    }

    private fun setupView(view: View) {
        view.findViewById<TextView>(R.id.prop_name).text = item.name
        view.findViewById<TextView>(R.id.prop_description).text = 
            item.description.ifEmpty { getString(R.string.no_description) }
        view.findViewById<TextView>(R.id.prop_type).text = item.assetTypeEnum.toString()
        view.findViewById<TextView>(R.id.prop_asset_id).text = item.assetId.toString()
        view.findViewById<TextView>(R.id.prop_item_id).text = item.itemId.toString()
        view.findViewById<TextView>(R.id.prop_parent_id).text = item.parentId.toString()
        
        // Flags
        val flagsText = buildString {
            if (item.flags and 0x01 != 0) append("Shared, ")
            if (item.flags and 0x02 != 0) append("Marketplace, ")
            if (item.flags and 0x04 != 0) append("Folder, ")
            if (item.flags and 0x08 != 0) append("Hidden, ")
            if (item.flags and 0x10 != 0) append("Broken, ")
        }.dropLastWhile { it == ',' || it == ' ' }
        
        view.findViewById<TextView>(R.id.prop_flags).text = 
            flagsText.ifEmpty { "None" }
    }

    companion object {
        fun newInstance(item: InventoryItem): ItemPropertiesDialog {
            return ItemPropertiesDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("item", item)
                }
            }
        }
    }
}