package com.linkpoint.feature.communication

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

sealed interface ChatImUiState {
    data object Loading : ChatImUiState
    data object Error : ChatImUiState
    data object EmptyConversations : ChatImUiState
    data object Connected : ChatImUiState
}

@Composable
fun ChatImScreen(state: ChatImUiState) {
    Text("ChatImScreen stub: $state")
}
