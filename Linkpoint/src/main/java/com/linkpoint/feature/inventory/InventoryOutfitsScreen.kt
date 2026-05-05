package com.linkpoint.feature.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

sealed interface InventoryOutfitsUiState {
    data object Loading : InventoryOutfitsUiState
    data object Error : InventoryOutfitsUiState
    data object EmptyInventory : InventoryOutfitsUiState
    data object EmptyWearables : InventoryOutfitsUiState
    data object EmptyDocument : InventoryOutfitsUiState
    data object EmptyScript : InventoryOutfitsUiState
    data object Ready : InventoryOutfitsUiState
    data object Edited : InventoryOutfitsUiState
}

@Composable
fun InventoryOutfitsScreen(state: InventoryOutfitsUiState) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            InventoryOutfitsUiState.Loading -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Loading inventory…", style = MaterialTheme.typography.bodyMedium)
            }

            InventoryOutfitsUiState.Error -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(Modifier.height(12.dp))
                Text("Could not load inventory", style = MaterialTheme.typography.titleMedium)
            }

            InventoryOutfitsUiState.EmptyInventory,
            InventoryOutfitsUiState.EmptyWearables,
            InventoryOutfitsUiState.EmptyDocument,
            InventoryOutfitsUiState.EmptyScript -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Checkroom,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(Modifier.height(12.dp))
                Text("Inventory is empty", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Items you acquire in-world will appear here.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            InventoryOutfitsUiState.Ready,
            InventoryOutfitsUiState.Edited -> {
                // Inventory list renders here once the inventory manager is attached.
            }
        }
    }
}
