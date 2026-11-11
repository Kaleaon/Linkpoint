package com.linkpoint.feature.inventory

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
