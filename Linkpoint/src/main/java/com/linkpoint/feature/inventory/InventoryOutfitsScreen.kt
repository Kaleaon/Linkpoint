package com.linkpoint.feature.inventory

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

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
    Text("InventoryOutfitsScreen stub: $state")
}
