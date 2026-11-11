package com.linkpoint.feature.inventory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.linkpoint.ui.components.state.GuidedEmptyStateView
import com.linkpoint.ui.components.state.SkeletonListLoader

sealed interface InventoryUiState {
    data object Loading : InventoryUiState
    data object Error : InventoryUiState
    data object EmptyInventory : InventoryUiState
    data object Ready : InventoryUiState
}

@Composable
fun InventoryScreen(
    state: InventoryUiState,
    onAction: (() -> Unit)? = null
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            InventoryUiState.Loading -> SkeletonListLoader()

            InventoryUiState.Error -> GuidedEmptyStateView(
                title = "Could not load inventory",
                message = "Failed to load inventory folders from grid.",
                icon = Icons.Default.Warning,
                actionLabel = onAction?.let { "Reload" },
                onAction = onAction
            )

            InventoryUiState.EmptyInventory -> GuidedEmptyStateView(
                title = "Inventory is empty",
                message = "Items you acquire in-world will appear here.",
                icon = Icons.Default.Checkroom,
                actionLabel = onAction?.let { "Refresh Inventory" },
                onAction = onAction
            )

            InventoryUiState.Ready -> {
                // Inventory tree renders here once InventoryManager is attached.
            }
        }
    }
}
