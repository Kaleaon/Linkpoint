package com.linkpoint.feature.world

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

sealed interface WorldShellUiState {
    data object LoadingRegion : WorldShellUiState
    data object Error : WorldShellUiState
    data object EmptySession : WorldShellUiState
    data object Connected : WorldShellUiState
}

@Composable
fun WorldShellScreen(state: WorldShellUiState) {
    Text("WorldShellScreen stub: $state")
}
