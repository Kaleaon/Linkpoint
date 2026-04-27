package com.linkpoint.feature.world

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class WorldShellUiState {
    Loading,
    Error,
    Empty,
    Connected
}

@Composable
fun WorldShellScreen(state: WorldShellUiState) {
    Text("WorldShellScreen stub: $state")
}
