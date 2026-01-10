package com.linkpoint.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import java.util.UUID

/**
 * Inventory Activity - Browse and manage inventory
 * Based on Lumiya's InventoryActivity
 */
class InventoryActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "InventoryActivity"
    }
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var breadcrumbText: TextView
    private lateinit var emptyText: TextView
    
    private val inventoryStack = mutableListOf<InventoryFolder>()
    private val currentItems = mutableListOf<InventoryItem>()
    private lateinit var adapter: InventoryAdapter
    
    private val app by lazy { LinkpointApp.getInstance() }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)
        
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Inventory"
        }
        
        initViews()
        loadRootInventory()
    }
    
    private fun initViews() {
        recyclerView = findViewById(R.id.inventoryRecyclerView)
        breadcrumbText = findViewById(R.id.breadcrumbText)
        emptyText = findViewById(R.id.emptyText)
        
        adapter = InventoryAdapter(currentItems) { item ->
            onItemClicked(item)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
    
    private fun loadRootInventory() {
        // Load root inventory folders (simulated)
        val rootFolder = InventoryFolder(
            id = UUID.randomUUID(),
            name = "My Inventory",
            parentId = null
        )
        inventoryStack.clear()
        inventoryStack.add(rootFolder)
        
        loadFolderContents(rootFolder)
    }
    
    private fun loadFolderContents(folder: InventoryFolder) {
        updateBreadcrumb()
        
        currentItems.clear()
        
        // Simulated inventory structure
        if (folder.name == "My Inventory") {
            currentItems.addAll(listOf(
                InventoryItem.folder(UUID.randomUUID(), "Animations", folder.id),
                InventoryItem.folder(UUID.randomUUID(), "Body Parts", folder.id),
                InventoryItem.folder(UUID.randomUUID(), "Calling Cards", folder.id),
                InventoryItem.folder(UUID.randomUUID(), "Clothing", folder.id),
                InventoryItem.folder(UUID.randomUUID(), "Gestures", folder.id),
                InventoryItem.folder(UUID.randomUUID(), "Landmarks", folder.id),
                InventoryItem.folder(UUID.randomUUID(), "Lost and Found", folder.id),
                InventoryItem.folder(UUID.randomUUID(), "Notecards", folder.id),
                InventoryItem.folder(UUID.randomUUID(), "Objects", folder.id),
                InventoryItem.folder(UUID.randomUUID(), "Photo Album", folder.id),
                InventoryItem.folder(UUID.randomUUID(), "Scripts", folder.id),
                InventoryItem.folder(UUID.randomUUID(), "Sounds", folder.id),
                InventoryItem.folder(UUID.randomUUID(), "Textures", folder.id),
                InventoryItem.folder(UUID.randomUUID(), "Trash", folder.id)
            ))
        }
        
        emptyText.visibility = if (currentItems.isEmpty()) View.VISIBLE else View.GONE
        adapter.notifyDataSetChanged()
    }
    
    private fun updateBreadcrumb() {
        breadcrumbText.text = inventoryStack.joinToString(" > ") { it.name }
    }
    
    private fun onItemClicked(item: InventoryItem) {
        when (item.type) {
            InventoryType.FOLDER -> {
                val folder = InventoryFolder(item.id, item.name, item.parentId)
                inventoryStack.add(folder)
                loadFolderContents(folder)
            }
            InventoryType.NOTECARD -> {
                // Open notecard editor
                openNotecard(item)
            }
            InventoryType.LANDMARK -> {
                // Offer to teleport
                offerTeleport(item)
            }
            else -> {
                // Show item details
                showItemDetails(item)
            }
        }
    }
    
    private fun openNotecard(item: InventoryItem) {
        // TODO: Open notecard editor activity
    }
    
    private fun offerTeleport(item: InventoryItem) {
        // TODO: Show teleport dialog
    }
    
    private fun showItemDetails(item: InventoryItem) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(item.name)
            .setMessage("Type: ${item.type.displayName}\nCreator: ${item.creatorId ?: "Unknown"}")
            .setPositiveButton("OK", null)
            .show()
    }
    
    override fun onBackPressed() {
        if (inventoryStack.size > 1) {
            inventoryStack.removeLast()
            loadFolderContents(inventoryStack.last())
        } else {
            super.onBackPressed()
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

enum class InventoryType(val displayName: String, val icon: Int) {
    FOLDER("Folder", R.drawable.ic_folder),
    TEXTURE("Texture", R.drawable.ic_texture),
    SOUND("Sound", R.drawable.ic_sound),
    ANIMATION("Animation", R.drawable.ic_animation),
    GESTURE("Gesture", R.drawable.ic_gesture),
    LANDMARK("Landmark", R.drawable.ic_landmark),
    CLOTHING("Clothing", R.drawable.ic_clothing),
    BODYPART("Body Part", R.drawable.ic_bodypart),
    OBJECT("Object", R.drawable.ic_object),
    NOTECARD("Notecard", R.drawable.ic_notecard),
    SCRIPT("Script", R.drawable.ic_script),
    CALLING_CARD("Calling Card", R.drawable.ic_calling_card),
    LINK("Link", R.drawable.ic_link),
    UNKNOWN("Unknown", R.drawable.ic_unknown)
}

data class InventoryFolder(
    val id: UUID,
    val name: String,
    val parentId: UUID?
)

data class InventoryItem(
    val id: UUID,
    val name: String,
    val parentId: UUID?,
    val type: InventoryType,
    val assetId: UUID? = null,
    val creatorId: UUID? = null,
    val permissions: Int = 0
) {
    companion object {
        fun folder(id: UUID, name: String, parentId: UUID?): InventoryItem {
            return InventoryItem(id, name, parentId, InventoryType.FOLDER)
        }
    }
}

class InventoryAdapter(
    private val items: List<InventoryItem>,
    private val onItemClick: (InventoryItem) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconView: ImageView = view.findViewById(R.id.itemIcon)
        val nameText: TextView = view.findViewById(R.id.itemName)
        val infoText: TextView = view.findViewById(R.id.itemInfo)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        
        holder.nameText.text = item.name
        holder.infoText.text = if (item.type == InventoryType.FOLDER) "" else item.type.displayName
        holder.iconView.setImageResource(item.type.icon)
        
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }
    
    override fun getItemCount() = items.size
}
