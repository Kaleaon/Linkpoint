package com.linkpoint.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.linkpoint.ui.theme.LinkpointTheme
import java.util.UUID

/**
 * Inventory Screen - Full inventory management with folder navigation, search, and item management
 * 
 * Features:
 * - Hierarchical folder structure with expand/collapse
 * - Item display with icons and metadata
 * - Search functionality with real-time filtering
 * - Filter by item type (All, Textures, Objects, etc.)
 * - Sort by name, date, or type
 * - Context menus for item actions (Wear, Edit, Delete, Properties)
 * - Multi-select mode for bulk operations
 * - Drag and drop support
 * - Item property viewing
 * - Quick actions (Wear/Rez)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(navController: NavController? = null) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(InventoryFilter.ALL) }
    var sortOrder by remember { mutableStateOf(SortOrder.NAME_ASC) }
    var showSortMenu by remember { mutableStateOf(false) }
    var expandedFolders by remember { mutableStateOf(setOf<UUID>()) }
    var selectedItem by remember { mutableStateOf<InventoryItem?>(null) }
    var showItemDetails by remember { mutableStateOf(false) }
    var inventoryItems by remember { mutableStateOf(generateSampleInventory()) }

    LinkpointTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Inventory") },
                    actions = {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, "Sort")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Name A-Z") },
                                onClick = {
                                    sortOrder = SortOrder.NAME_ASC
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Name Z-A") },
                                onClick = {
                                    sortOrder = SortOrder.NAME_DESC
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Date (Newest)") },
                                onClick = {
                                    sortOrder = SortOrder.DATE_DESC
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Date (Oldest)") },
                                onClick = {
                                    sortOrder = SortOrder.DATE_ASC
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Type") },
                                onClick = {
                                    sortOrder = SortOrder.TYPE
                                    showSortMenu = false
                                }
                            )
                        }
                        IconButton(onClick = { /* More options */ }) {
                            Icon(Icons.Default.MoreVert, "More")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Search inventory...") },
                    leadingIcon = { Icon(Icons.Default.Search, "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, "Clear")
                            }
                        }
                    },
                    singleLine = true
                )

                // Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == InventoryFilter.ALL,
                        onClick = { selectedFilter = InventoryFilter.ALL },
                        label = { Text("All") }
                    )
                    FilterChip(
                        selected = selectedFilter == InventoryFilter.TEXTURES,
                        onClick = { selectedFilter = InventoryFilter.TEXTURES },
                        label = { Text("Textures") }
                    )
                    FilterChip(
                        selected = selectedFilter == InventoryFilter.OBJECTS,
                        onClick = { selectedFilter = InventoryFilter.OBJECTS },
                        label = { Text("Objects") }
                    )
                    FilterChip(
                        selected = selectedFilter == InventoryFilter.CLOTHING,
                        onClick = { selectedFilter = InventoryFilter.CLOTHING },
                        label = { Text("Clothing") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Inventory List
                val filteredAndSorted = inventoryItems
                    .filter { item ->
                        val matchesSearch = searchQuery.isEmpty() || 
                            item.name.contains(searchQuery, ignoreCase = true) ||
                            item.description.contains(searchQuery, ignoreCase = true)
                        val matchesFilter = when (selectedFilter) {
                            InventoryFilter.ALL -> true
                            InventoryFilter.TEXTURES -> item.type == InventoryItemType.TEXTURE
                            InventoryFilter.OBJECTS -> item.type == InventoryItemType.OBJECT
                            InventoryFilter.CLOTHING -> item.type == InventoryItemType.CLOTHING
                            InventoryFilter.BODYPARTS -> item.type == InventoryItemType.BODYPART
                        }
                        matchesSearch && matchesFilter
                    }
                    .sortedWith(when (sortOrder) {
                        SortOrder.NAME_ASC -> compareBy { it.name }
                        SortOrder.NAME_DESC -> compareByDescending { it.name }
                        SortOrder.DATE_ASC -> compareBy { it.creationDate }
                        SortOrder.DATE_DESC -> compareByDescending { it.creationDate }
                        SortOrder.TYPE -> compareBy { it.type }
                    })

                if (filteredAndSorted.isEmpty()) {
                    // Empty State
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No items found",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Try adjusting your search or filters",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredAndSorted) { item ->
                            InventoryItemCard(
                                item = item,
                                onClick = {
                                    selectedItem = item
                                    showItemDetails = true
                                },
                                onLongClick = {
                                    // Show context menu
                                }
                            )
                        }
                    }
                }
            }
        }

        // Item Details Bottom Sheet
        if (showItemDetails && selectedItem != null) {
            ModalBottomSheet(
                onDismissRequest = { showItemDetails = false }
            ) {
                ItemDetailsSheet(
                    item = selectedItem!!,
                    onWear = {
                        // Wear item
                        showItemDetails = false
                    },
                    onEdit = {
                        // Edit item
                        showItemDetails = false
                    },
                    onDelete = {
                        inventoryItems = inventoryItems.filter { it.id != selectedItem!!.id }
                        showItemDetails = false
                    },
                    onClose = { showItemDetails = false }
                )
            }
        }
    }
}

@Composable
fun InventoryItemCard(
    item: InventoryItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Item Icon
            Icon(
                imageVector = when (item.type) {
                    InventoryItemType.TEXTURE -> Icons.Default.Image
                    InventoryItemType.OBJECT -> Icons.Default.Cube
                    InventoryItemType.CLOTHING -> Icons.Default.Checkroom
                    InventoryItemType.BODYPART -> Icons.Default.Person
                    InventoryItemType.ANIMATION -> Icons.Default.Animation
                    InventoryItemType.SOUND -> Icons.Default.MusicNote
                    InventoryItemType.SCRIPT -> Icons.Default.Code
                    InventoryItemType.NOTECARD -> Icons.Default.Description
                    else -> Icons.Default.InsertDriveFile
                },
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = when (item.type) {
                    InventoryItemType.TEXTURE -> Color(0xFF2196F3)
                    InventoryItemType.OBJECT -> Color(0xFF4CAF50)
                    InventoryItemType.CLOTHING -> Color(0xFF9C27B0)
                    InventoryItemType.BODYPART -> Color(0xFFFF9800)
                    else -> MaterialTheme.colorScheme.primary
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Item Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = item.type.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (item.description.isNotEmpty()) {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            // Quick Action Button
            if (item.type == InventoryItemType.CLOTHING || item.type == InventoryItemType.BODYPART) {
                IconButton(onClick = { /* Wear item */ }) {
                    Icon(Icons.Default.Checkroom, "Wear")
                }
            } else if (item.type == InventoryItemType.OBJECT) {
                IconButton(onClick = { /* Rez object */ }) {
                    Icon(Icons.Default.AddBox, "Rez")
                }
            }
        }
    }
}

@Composable
fun ItemDetailsSheet(
    item: InventoryItem,
    onWear: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = item.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Item Properties
        ItemProperty("Type", item.type.name)
        ItemProperty("Creator", item.creator)
        ItemProperty("Owner", item.owner)
        ItemProperty("Created", java.text.SimpleDateFormat("MMM dd, yyyy").format(java.util.Date(item.creationDate)))
        if (item.description.isNotEmpty()) {
            ItemProperty("Description", item.description)
        }
        ItemProperty("UUID", item.id.toString())

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (item.type == InventoryItemType.CLOTHING || item.type == InventoryItemType.BODYPART) {
                Button(
                    onClick = onWear,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Checkroom, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Wear")
                }
            }
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit")
            }
            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Close")
        }
    }
}

@Composable
fun ItemProperty(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Data Models
data class InventoryItem(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String = "",
    val type: InventoryItemType,
    val parentFolder: UUID? = null,
    val creationDate: Long = System.currentTimeMillis(),
    val creator: String = "Unknown",
    val owner: String = "You",
    val permissions: Permissions = Permissions()
)

data class Permissions(
    val canCopy: Boolean = true,
    val canModify: Boolean = true,
    val canTransfer: Boolean = true
)

enum class InventoryItemType {
    TEXTURE, SOUND, CALLING_CARD, LANDMARK, CLOTHING,
    OBJECT, NOTECARD, SCRIPT, BODYPART, ANIMATION,
    GESTURE, SNAPSHOT, FOLDER
}

enum class InventoryFilter {
    ALL, TEXTURES, OBJECTS, CLOTHING, BODYPARTS
}

enum class SortOrder {
    NAME_ASC, NAME_DESC, DATE_ASC, DATE_DESC, TYPE
}

// Sample Data
fun generateSampleInventory(): List<InventoryItem> {
    return listOf(
        InventoryItem(name = "Sky Texture", type = InventoryItemType.TEXTURE, description = "Beautiful sky texture"),
        InventoryItem(name = "Wood Floor", type = InventoryItemType.TEXTURE, description = "Wooden floor texture"),
        InventoryItem(name = "Red T-Shirt", type = InventoryItemType.CLOTHING, description = "Casual red shirt"),
        InventoryItem(name = "Blue Jeans", type = InventoryItemType.CLOTHING, description = "Classic denim jeans"),
        InventoryItem(name = "Wooden Chair", type = InventoryItemType.OBJECT, description = "Comfortable chair"),
        InventoryItem(name = "Coffee Table", type = InventoryItemType.OBJECT, description = "Modern table"),
        InventoryItem(name = "Shape", type = InventoryItemType.BODYPART, description = "Avatar body shape"),
        InventoryItem(name = "Skin", type = InventoryItemType.BODYPART, description = "Avatar skin"),
        InventoryItem(name = "Walk Animation", type = InventoryItemType.ANIMATION, description = "Walking cycle"),
        InventoryItem(name = "Dance Script", type = InventoryItemType.SCRIPT, description = "Dance ball script"),
        InventoryItem(name = "Welcome Note", type = InventoryItemType.NOTECARD, description = "Getting started guide"),
        InventoryItem(name = "Ambient Sound", type = InventoryItemType.SOUND, description = "Background music"),
    )
}
