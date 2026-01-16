package com.linkpoint.ui.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linkpoint.R
import com.linkpoint.inventory.InventoryItem
import com.linkpoint.inventory.InventoryFolder
import com.bumptech.glide.Glide
import java.util.UUID

/**
 * Adapter for displaying inventory items in a RecyclerView
 */
class InventoryAdapter(
    private val onItemClick: (InventoryItem) -> Unit,
    private val onFolderClick: (InventoryFolder) -> Unit,
    private val onItemLongClick: (InventoryItem) -> Unit = {}
) : ListAdapter<InventoryAdapter.InventoryListItem, RecyclerView.ViewHolder>(DiffCallback()) {

    sealed class InventoryListItem {
        data class FolderItem(val folder: InventoryFolder) : InventoryListItem()
        data class ItemItem(val item: InventoryItem) : InventoryListItem()
    }

    companion object {
        private const val VIEW_TYPE_FOLDER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is InventoryListItem.FolderItem -> VIEW_TYPE_FOLDER
            is InventoryListItem.ItemItem -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FOLDER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_inventory_folder, parent, false)
                FolderViewHolder(view, onFolderClick)
            }
            VIEW_TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_inventory_item, parent, false)
                ItemViewHolder(view, onItemClick, onItemLongClick)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is InventoryListItem.FolderItem -> {
                (holder as FolderViewHolder).bind(item.folder)
            }
            is InventoryListItem.ItemItem -> {
                (holder as ItemViewHolder).bind(item.item)
            }
        }
    }

    /**
     * ViewHolder for inventory folders
     */
    class FolderViewHolder(
        itemView: View,
        private val onClick: (InventoryFolder) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val folderIcon: ImageView = itemView.findViewById(R.id.folder_icon)
        private val folderName: TextView = itemView.findViewById(R.id.folder_name)
        private val itemCount: TextView = itemView.findViewById(R.id.item_count)

        fun bind(folder: InventoryFolder) {
            folderName.text = folder.name
            itemCount.text = "${folder.version} items"
            
            // Set folder icon based on type
            val iconRes = when (folder.type) {
                0 -> R.drawable.ic_folder_texture
                1 -> R.drawable.ic_folder_sound
                2 -> R.drawable.ic_folder_calling_card
                3 -> R.drawable.ic_folder_landmark
                5 -> R.drawable.ic_folder_clothing
                6 -> R.drawable.ic_folder_object
                7 -> R.drawable.ic_folder_notecard
                10 -> R.drawable.ic_folder_script
                13 -> R.drawable.ic_folder_bodypart
                14 -> R.drawable.ic_folder_trash
                15 -> R.drawable.ic_folder_snapshot
                16 -> R.drawable.ic_folder_lost_found
                20 -> R.drawable.ic_folder_animation
                21 -> R.drawable.ic_folder_gesture
                23 -> R.drawable.ic_folder_favorites
                54 -> R.drawable.ic_folder_outfit
                55 -> R.drawable.ic_folder_my_outfits
                else -> R.drawable.ic_folder_default
            }
            folderIcon.setImageResource(iconRes)
            
            itemView.setOnClickListener { onClick(folder) }
        }
    }

    /**
     * ViewHolder for inventory items
     */
    class ItemViewHolder(
        itemView: View,
        private val onClick: (InventoryItem) -> Unit,
        private val onLongClick: (InventoryItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val itemIcon: ImageView = itemView.findViewById(R.id.item_icon)
        private val itemName: TextView = itemView.findViewById(R.id.item_name)
        private val itemDescription: TextView = itemView.findViewById(R.id.item_description)
        private val permissionsIcon: ImageView = itemView.findViewById(R.id.permissions_icon)

        fun bind(item: InventoryItem) {
            itemName.text = item.name
            itemDescription.text = item.description.ifEmpty { "No description" }
            
            // Set item icon based on asset type
            val iconRes = when (item.assetType) {
                0 -> R.drawable.ic_item_texture
                1 -> R.drawable.ic_item_sound
                2 -> R.drawable.ic_item_calling_card
                3 -> R.drawable.ic_item_landmark
                5 -> R.drawable.ic_item_clothing
                6 -> R.drawable.ic_item_object
                7 -> R.drawable.ic_item_notecard
                10 -> R.drawable.ic_item_script
                13 -> R.drawable.ic_item_bodypart
                15 -> R.drawable.ic_item_snapshot
                20 -> R.drawable.ic_item_animation
                21 -> R.drawable.ic_item_gesture
                else -> R.drawable.ic_item_unknown
            }
            itemIcon.setImageResource(iconRes)
            
            // Set permissions icon
            val permissionsRes = when {
                item.permissions.everyoneMask and 0x00000020 != 0 -> R.drawable.ic_permission_copy
                item.permissions.everyoneMask and 0x00000004 != 0 -> R.drawable.ic_permission_modify
                item.permissions.everyoneMask and 0x00000008 != 0 -> R.drawable.ic_permission_transfer
                else -> R.drawable.ic_permission_none
            }
            permissionsIcon.setImageResource(permissionsRes)
            
            itemView.setOnClickListener { onClick(item) }
            itemView.setOnLongClickListener {
                onLongClick(item)
                true
            }
        }
    }

    /**
     * DiffUtil callback for efficient updates
     */
    class DiffCallback : DiffUtil.ItemCallback<InventoryListItem>() {
        override fun areItemsTheSame(
            oldItem: InventoryListItem,
            newItem: InventoryListItem
        ): Boolean {
            return when {
                oldItem is InventoryListItem.FolderItem && newItem is InventoryListItem.FolderItem -> {
                    oldItem.folder.folderId == newItem.folder.folderId
                }
                oldItem is InventoryListItem.ItemItem && newItem is InventoryListItem.ItemItem -> {
                    oldItem.item.itemId == newItem.item.itemId
                }
                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: InventoryListItem,
            newItem: InventoryListItem
        ): Boolean {
            return when {
                oldItem is InventoryListItem.FolderItem && newItem is InventoryListItem.FolderItem -> {
                    oldItem.folder == newItem.folder
                }
                oldItem is InventoryListItem.ItemItem && newItem is InventoryListItem.ItemItem -> {
                    oldItem.item == newItem.item
                }
                else -> false
            }
        }
    }
}