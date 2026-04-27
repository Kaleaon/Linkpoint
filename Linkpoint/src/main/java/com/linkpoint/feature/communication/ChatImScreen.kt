package com.linkpoint.feature.communication

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class ChatImUiState {
    Loading,
    Error,
    Empty,
    Connected
}

@Composable
fun ChatImScreen(state: ChatImUiState) {
    Text("ChatImScreen stub: $state")
}
