package com.linkpoint.feature.system

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

sealed interface WalletBuildSettingsNotificationsUiState {
    data object Loading : WalletBuildSettingsNotificationsUiState
    data object Error : WalletBuildSettingsNotificationsUiState
    data object EmptySettings : WalletBuildSettingsNotificationsUiState
    data object EmptySelection : WalletBuildSettingsNotificationsUiState
    data object EmptyTransactions : WalletBuildSettingsNotificationsUiState
    data object EmptyNotifications : WalletBuildSettingsNotificationsUiState
    data object EmptyDocument : WalletBuildSettingsNotificationsUiState
    data object UnsupportedDevice : WalletBuildSettingsNotificationsUiState
    data object Ready : WalletBuildSettingsNotificationsUiState
    data object Accepted : WalletBuildSettingsNotificationsUiState
}

@Composable
fun WalletBuildSettingsNotificationsScreen(state: WalletBuildSettingsNotificationsUiState) {
    Text("WalletBuildSettingsNotificationsScreen stub: $state")
}
