package com.linkpoint.ui.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.common.UiLoadState
import com.linkpoint.ui.common.UiTelemetryEvents
import com.linkpoint.ui.common.logUiTelemetry
import com.linkpoint.ui.components.linkpoint2.primitives.L2Chip
import com.linkpoint.ui.components.linkpoint2.primitives.L2ChipVariant
import com.linkpoint.ui.components.linkpoint2.primitives.L2Row
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2
import com.linkpoint.ui.components.state.EmptyState
import com.linkpoint.ui.components.state.ErrorState
import com.linkpoint.ui.components.state.LoadingState
import com.linkpoint.ui.components.state.ReconnectingBanner
import java.util.UUID

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
    UNKNOWN("Unknown", Icons.Default.InsertDriveFile),
}

data class InventoryItemData(
    val id: UUID,
    val name: String,
    val type: InventoryItemType,
    val parentId: UUID? = null,
    val assetId: UUID? = null,
    val creatorId: UUID? = null,
)

/**
 * Linkpoint 2.0 inventory screen — type filter chips along the top, list of
 * folders/items with appropriate icon tinting, breadcrumb header that doubles
 * as a back-up affordance. Matches design/screens-3.jsx → InventoryScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    items: List<InventoryItemData>,
    breadcrumb: List<String>,
    uiLoadState: UiLoadState = UiLoadState.Content,
    onRetry: () -> Unit,
    onItemClick: (InventoryItemData) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    var typeFilter by remember { mutableStateOf<InventoryItemType?>(null) }
    val tokens = Linkpoint2.tokens

    val filtered = remember(items, query, typeFilter) {
        items
            .filter { typeFilter == null || it.type == typeFilter || it.type == InventoryItemType.FOLDER }
            .filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            L2TopBar(
                title = "Inventory",
                subtitle = breadcrumb.takeIf { it.isNotEmpty() }?.joinToString(" / ") ?: "Library",
                leading = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                trailing = {
                    if (breadcrumb.size > 1) {
                        IconButton(onClick = onNavigateUp) {
                            Icon(Icons.Default.FolderOpen, contentDescription = "Up")
                        }
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (uiLoadState is UiLoadState.Reconnecting) {
                ReconnectingBanner(message = uiLoadState.message)
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search inventory…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(tokens.radii.pill),
            )

            // Type filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                L2Chip(
                    label = "All",
                    variant = if (typeFilter == null) L2ChipVariant.Primary else L2ChipVariant.Neutral,
                    onClick = { typeFilter = null },
                )
                listOf(
                    InventoryItemType.CLOTHING to "Wearable",
                    InventoryItemType.OBJECT to "Object",
                    InventoryItemType.TEXTURE to "Texture",
                    InventoryItemType.SCRIPT to "Script",
                    InventoryItemType.LANDMARK to "Landmark",
                ).forEach { (type, label) ->
                    L2Chip(
                        label = label,
                        variant = if (typeFilter == type) L2ChipVariant.Primary else L2ChipVariant.Neutral,
                        onClick = { typeFilter = type },
                    )
                }
            }

            when (uiLoadState) {
                is UiLoadState.Loading -> LoadingState(message = uiLoadState.message)
                is UiLoadState.Error -> ErrorState(
                    title = uiLoadState.title,
                    message = uiLoadState.message,
                    retryLabel = uiLoadState.retryLabel,
                    onRetry = {
                        logUiTelemetry(UiTelemetryEvents.RETRY_TAPPED, "inventory", uiLoadState)
                        onRetry()
                    },
                )
                is UiLoadState.Empty -> EmptyState(
                    title = uiLoadState.title,
                    message = uiLoadState.message,
                    actionLabel = uiLoadState.actionLabel,
                    onAction = onRetry,
                )
                UiLoadState.Content, is UiLoadState.Reconnecting, is UiLoadState.LowBandwidth -> {
                    if (filtered.isEmpty()) {
                        EmptyState(
                            title = if (query.isBlank()) "This folder is empty" else "No matches",
                            message = if (query.isBlank()) "Try navigating up or refreshing this folder."
                            else "Try a different name or filter.",
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 4.dp),
                        ) {
                            items(filtered, key = { it.id }) { item ->
                                L2Row(
                                    headline = item.name,
                                    supporting = if (item.type == InventoryItemType.FOLDER) "Folder"
                                    else item.type.displayName,
                                    leading = {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(tokens.radii.md))
                                                .background(MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Icon(
                                                imageVector = if (item.type == InventoryItemType.FOLDER) Icons.Default.FolderOpen
                                                else item.type.icon,
                                                contentDescription = item.type.displayName,
                                                tint = if (item.type == InventoryItemType.FOLDER) MaterialTheme.colorScheme.primary
                                                else tokens.onSurfaceDim,
                                                modifier = Modifier.size(20.dp),
                                            )
                                        }
                                    },
                                    onClick = { onItemClick(item) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Backwards-compatible alias for old call sites.
 */
@Composable
fun InventoryItemCard(
    item: InventoryItemData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = Linkpoint2.tokens
    L2Row(
        modifier = modifier,
        headline = item.name,
        supporting = if (item.type == InventoryItemType.FOLDER) "Folder" else item.type.displayName,
        leading = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(tokens.radii.md))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (item.type == InventoryItemType.FOLDER) Icons.Default.FolderOpen
                    else item.type.icon,
                    contentDescription = item.type.displayName,
                    tint = if (item.type == InventoryItemType.FOLDER) MaterialTheme.colorScheme.primary
                    else tokens.onSurfaceDim,
                    modifier = Modifier.size(20.dp),
                )
            }
        },
        onClick = onClick,
    )
}
