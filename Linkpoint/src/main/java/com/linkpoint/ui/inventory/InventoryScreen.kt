package com.linkpoint.ui.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import java.util.UUID

/**
 * Inventory item types with icons
 */
enum class InventoryItemType(val displayName: String, val icon: ImageVector) {
    FOLDER("Folder", Icons.Default.Folder),
    TEXTURE("Texture", Icons.Default.Image),
    SOUND("Sound", Icons.Default.MusicNote),
    ANIMATION("Animation", Icons.Default.Person),
    GESTURE("Gesture", Icons.Default.Person),
    LANDMARK("Landmark", Icons.Default.LocationOn),
    CLOTHING("Clothing", Icons.Default.Person),
    BODYPART("Body Part", Icons.Default.Person),
    OBJECT("Object", Icons.Default.InsertDriveFile),
    NOTECARD("Notecard", Icons.Default.StickyNote2),
    SCRIPT("Script", Icons.Default.InsertDriveFile),
    CALLING_CARD("Calling Card", Icons.Default.Person),
    UNKNOWN("Unknown", Icons.Default.InsertDriveFile)
}

/**
 * Inventory item data
 */
data class InventoryItemData(
    val id: UUID,
    val name: String,
    val type: InventoryItemType,
    val parentId: UUID? = null,
    val assetId: UUID? = null,
    val creatorId: UUID? = null
)

/**
 * Compose version of InventoryActivity.
 * 
 * Features:
 * - Hierarchical folder navigation
 * - Breadcrumb display
 * - Item type icons
 * - Click to open folders or view items
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    items: List<InventoryItemData>,
    breadcrumb: List<String>,
    onItemClick: (InventoryItemData) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Breadcrumb
            if (breadcrumb.isNotEmpty()) {
                Text(
                    text = breadcrumb.joinToString(" > "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = breadcrumb.size > 1) { onNavigateUp() }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Items list
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "This folder is empty",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        InventoryItemCard(
                            item = item,
                            onClick = { onItemClick(item) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual inventory item card
 */
@Composable
fun InventoryItemCard(
    item: InventoryItemData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (item.type == InventoryItemType.FOLDER) {
                    Icons.Default.FolderOpen
                } else {
                    item.type.icon
                },
                contentDescription = item.type.displayName,
                tint = if (item.type == InventoryItemType.FOLDER) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (item.type != InventoryItemType.FOLDER) {
                    Text(
                        text = item.type.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
