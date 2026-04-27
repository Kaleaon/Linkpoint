package com.linkpoint.feature.system

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class WalletBuildSettingsNotificationsUiState {
    Loading,
    Error,
    Empty,
    Ready
}

@Composable
fun WalletBuildSettingsNotificationsScreen(state: WalletBuildSettingsNotificationsUiState) {
    Text("WalletBuildSettingsNotificationsScreen stub: $state")
}
