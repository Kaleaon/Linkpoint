package com.lumiyaviewer.lumiya.ui.inventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryFolder
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryItem
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import javax.annotation.Nullable
import java.util.UUID

/**
 * Converted from Java to Kotlin
 * InventoryFragment - Displays user inventory in a tree structure
 */
class InventoryFragment : Fragment() {

    @BindView(R.id.inventoryRecyclerView) lateinit var inventoryRecyclerView: RecyclerView
    @BindView(R.id.inventoryProgressBar) lateinit var inventoryProgressBar: ProgressBar
    @BindView(R.id.inventoryEmptyText) lateinit var inventoryEmptyText: TextView

    private var userManager: UserManager? = null
    private var inventoryAdapter: InventoryAdapter? = null
    private val currentFolderStack = ArrayDeque<UUID>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inventory, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadInventory()
    }

    private fun setupRecyclerView() {
        inventoryRecyclerView.layoutManager = LinearLayoutManager(context)
        inventoryAdapter = InventoryAdapter(
            onItemClick = { item -> onInventoryItemClick(item) },
            onFolderClick = { folder -> onInventoryFolderClick(folder) }
        )
        inventoryRecyclerView.adapter = inventoryAdapter
    }

    private fun loadInventory() {
        inventoryProgressBar.visibility = View.VISIBLE
        inventoryEmptyText.visibility = View.GONE
        
        // Load root inventory folder
        userManager?.let { manager ->
            manager.getInventory()?.let { inventory ->
                inventory.getRootFolder()?.let { rootFolder ->
                    currentFolderStack.clear()
                    currentFolderStack.addLast(rootFolder.id)
                    displayFolder(rootFolder)
                }
            }
        }
        
        inventoryProgressBar.visibility = View.GONE
    }

    private fun displayFolder(folder: SLInventoryFolder) {
        val items = folder.getChildItems()
        val folders = folder.getChildFolders()
        
        if (items.isEmpty() && folders.isEmpty()) {
            inventoryEmptyText.visibility = View.VISIBLE
            inventoryRecyclerView.visibility = View.GONE
        } else {
            inventoryEmptyText.visibility = View.GONE
            inventoryRecyclerView.visibility = View.VISIBLE
            inventoryAdapter?.updateData(folders, items)
        }
    }

    private fun onInventoryItemClick(item: SLInventoryItem) {
        // Handle inventory item click
        Toast.makeText(context, "Selected: ${item.name}", Toast.LENGTH_SHORT).show()
    }

    private fun onInventoryFolderClick(folder: SLInventoryFolder) {
        // Navigate into folder
        currentFolderStack.addLast(folder.id)
        displayFolder(folder)
    }

    fun navigateBack(): Boolean {
        if (currentFolderStack.size > 1) {
            currentFolderStack.removeLast()
            val parentFolderId = currentFolderStack.last()
            userManager?.getInventory()?.getFolder(parentFolderId)?.let { folder ->
                displayFolder(folder)
                return true
            }
        }
        return false
    }

    fun setUserManager(manager: UserManager) {
        this.userManager = manager
    }

    inner class InventoryAdapter(
        private val onItemClick: (SLInventoryItem) -> Unit,
        private val onFolderClick: (SLInventoryFolder) -> Unit
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        
        private val folders = mutableListOf<SLInventoryFolder>()
        private val items = mutableListOf<SLInventoryItem>()

        fun updateData(newFolders: List<SLInventoryFolder>, newItems: List<SLInventoryItem>) {
            folders.clear()
            folders.addAll(newFolders)
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = folders.size + items.size

        override fun getItemViewType(position: Int): Int {
            return if (position < folders.size) VIEW_TYPE_FOLDER else VIEW_TYPE_ITEM
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return when (viewType) {
                VIEW_TYPE_FOLDER -> {
                    val view = inflater.inflate(R.layout.item_inventory_folder, parent, false)
                    FolderViewHolder(view)
                }
                else -> {
                    val view = inflater.inflate(R.layout.item_inventory_item, parent, false)
                    ItemViewHolder(view)
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is FolderViewHolder -> {
                    val folder = folders[position]
                    holder.bind(folder)
                }
                is ItemViewHolder -> {
                    val item = items[position - folders.size]
                    holder.bind(item)
                }
            }
        }

        inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val nameText: TextView = itemView.findViewById(R.id.folderNameText)

            fun bind(folder: SLInventoryFolder) {
                nameText.text = folder.name
                itemView.setOnClickListener { onFolderClick(folder) }
            }
        }

        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val nameText: TextView = itemView.findViewById(R.id.itemNameText)
            private val typeText: TextView = itemView.findViewById(R.id.itemTypeText)

            fun bind(item: SLInventoryItem) {
                nameText.text = item.name
                typeText.text = item.type.toString()
                itemView.setOnClickListener { onItemClick(item) }
            }
        }

        companion object {
            private const val VIEW_TYPE_FOLDER = 0
            private const val VIEW_TYPE_ITEM = 1
        }
    }
}
