package com.linkpoint.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linkpoint.R

/**
 * Inventory activity for browsing Second Life inventory
 */
class InventoryActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InventoryAdapter
    private lateinit var pathText: TextView
    
    private var currentFolder: InventoryFolder? = null
    private val folderStack = mutableListOf<InventoryFolder>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)
        
        supportActionBar?.title = "Inventory"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        initViews()
        loadRootInventory()
    }
    
    private fun initViews() {
        recyclerView = findViewById(R.id.inventoryRecyclerView)
        pathText = findViewById(R.id.pathText)
        
        adapter = InventoryAdapter { item ->
            onItemClicked(item)
        }
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
    
    private fun loadRootInventory() {
        // Create mock inventory structure
        val root = InventoryFolder(
            id = "root",
            name = "My Inventory",
            items = listOf(
                InventoryItem("1", "Animations", InventoryItemType.FOLDER),
                InventoryItem("2", "Body Parts", InventoryItemType.FOLDER),
                InventoryItem("3", "Clothing", InventoryItemType.FOLDER),
                InventoryItem("4", "Gestures", InventoryItemType.FOLDER),
                InventoryItem("5", "Landmarks", InventoryItemType.FOLDER),
                InventoryItem("6", "Lost And Found", InventoryItemType.FOLDER),
                InventoryItem("7", "Notecards", InventoryItemType.FOLDER),
                InventoryItem("8", "Objects", InventoryItemType.FOLDER),
                InventoryItem("9", "Scripts", InventoryItemType.FOLDER),
                InventoryItem("10", "Sounds", InventoryItemType.FOLDER),
                InventoryItem("11", "Textures", InventoryItemType.FOLDER),
                InventoryItem("12", "Trash", InventoryItemType.FOLDER),
            )
        )
        
        currentFolder = root
        displayFolder(root)
    }
    
    private fun displayFolder(folder: InventoryFolder) {
        currentFolder = folder
        pathText.text = buildPath()
        adapter.submitList(folder.items)
    }
    
    private fun buildPath(): String {
        // Build path from folder stack (excluding root) plus current folder
        val pathParts = mutableListOf<String>()
        
        // Add all folders from stack except root
        for (folder in folderStack) {
            if (folder.name != "My Inventory") {
                pathParts.add(folder.name)
            }
        }
        
        // Add current folder if not root
        currentFolder?.let {
            if (it.name != "My Inventory") {
                pathParts.add(it.name)
            }
        }
        
        // Build final path string
        return if (pathParts.isEmpty()) {
            "My Inventory"
        } else {
            "My Inventory > ${pathParts.joinToString(" > ")}"
        }
    }
    
    private fun onItemClicked(item: InventoryItem) {
        when (item.type) {
            InventoryItemType.FOLDER -> {
                // Navigate into folder
                currentFolder?.let { folderStack.add(it) }
                val subFolder = InventoryFolder(
                    id = item.id,
                    name = item.name,
                    items = generateMockItems(item.name)
                )
                displayFolder(subFolder)
            }
            else -> {
                // Show item details
                showItemDetails(item)
            }
        }
    }
    
    private fun generateMockItems(folderName: String): List<InventoryItem> {
        return when (folderName) {
            "Clothing" -> listOf(
                InventoryItem("c1", "Shirt", InventoryItemType.CLOTHING),
                InventoryItem("c2", "Pants", InventoryItemType.CLOTHING),
                InventoryItem("c3", "Shoes", InventoryItemType.CLOTHING),
            )
            "Objects" -> listOf(
                InventoryItem("o1", "Chair", InventoryItemType.OBJECT),
                InventoryItem("o2", "Table", InventoryItemType.OBJECT),
            )
            "Notecards" -> listOf(
                InventoryItem("n1", "Welcome Note", InventoryItemType.NOTECARD),
                InventoryItem("n2", "Instructions", InventoryItemType.NOTECARD),
            )
            "Scripts" -> listOf(
                InventoryItem("s1", "Door Script", InventoryItemType.SCRIPT),
                InventoryItem("s2", "Animation Script", InventoryItemType.SCRIPT),
            )
            else -> emptyList()
        }
    }
    
    private fun showItemDetails(item: InventoryItem) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(item.name)
            .setMessage("Type: ${item.type.name}\nID: ${item.id}")
            .setPositiveButton("OK", null)
            .show()
    }
    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (folderStack.isNotEmpty()) {
            val parent = folderStack.removeAt(folderStack.size - 1)
            displayFolder(parent)
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

enum class InventoryItemType {
    FOLDER, TEXTURE, SOUND, LANDMARK, CLOTHING, OBJECT, NOTECARD, SCRIPT, ANIMATION, GESTURE, BODYPART
}

data class InventoryItem(
    val id: String,
    val name: String,
    val type: InventoryItemType
)

data class InventoryFolder(
    val id: String,
    val name: String,
    val items: List<InventoryItem>
)

class InventoryAdapter(
    private val onItemClick: (InventoryItem) -> Unit
) : androidx.recyclerview.widget.ListAdapter<InventoryItem, InventoryAdapter.ViewHolder>(InventoryDiffCallback()) {
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconView: ImageView = view.findViewById(R.id.itemIcon)
        val nameText: TextView = view.findViewById(R.id.itemName)
        val typeText: TextView = view.findViewById(R.id.itemType)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        
        holder.nameText.text = item.name
        holder.typeText.text = item.type.name.lowercase().replaceFirstChar { it.uppercase() }
        
        // Set icon based on type
        val iconRes = when (item.type) {
            InventoryItemType.FOLDER -> android.R.drawable.ic_menu_agenda
            InventoryItemType.TEXTURE -> android.R.drawable.ic_menu_gallery
            InventoryItemType.SOUND -> android.R.drawable.ic_lock_silent_mode_off
            InventoryItemType.NOTECARD -> android.R.drawable.ic_menu_edit
            InventoryItemType.SCRIPT -> android.R.drawable.ic_menu_manage
            InventoryItemType.OBJECT -> android.R.drawable.ic_menu_view
            InventoryItemType.CLOTHING -> android.R.drawable.ic_menu_info_details
            else -> android.R.drawable.ic_menu_help
        }
        holder.iconView.setImageResource(iconRes)
        
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }
}

/**
 * DiffUtil callback for efficient RecyclerView updates
 */
class InventoryDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<InventoryItem>() {
    override fun areItemsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
        return oldItem == newItem
    }
}
