package com.linkpoint.feature.inventory

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class InventoryOutfitsUiState {
    Loading,
    Error,
    Empty,
    Ready
}

@Composable
fun InventoryOutfitsScreen(state: InventoryOutfitsUiState) {
    Text("InventoryOutfitsScreen stub: $state")
}
