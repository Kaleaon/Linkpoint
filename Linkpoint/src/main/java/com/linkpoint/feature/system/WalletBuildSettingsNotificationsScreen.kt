package com.linkpoint.feature.system

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            WalletBuildSettingsNotificationsUiState.Loading -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Loading…", style = MaterialTheme.typography.bodyMedium)
            }

            WalletBuildSettingsNotificationsUiState.Error -> Column(
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
                Text("Something went wrong", style = MaterialTheme.typography.titleMedium)
            }

            WalletBuildSettingsNotificationsUiState.EmptyTransactions -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(Modifier.height(12.dp))
                Text("No transactions yet", style = MaterialTheme.typography.titleMedium)
            }

            WalletBuildSettingsNotificationsUiState.EmptyNotifications -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(Modifier.height(12.dp))
                Text("No notifications", style = MaterialTheme.typography.titleMedium)
            }

            WalletBuildSettingsNotificationsUiState.EmptySettings,
            WalletBuildSettingsNotificationsUiState.EmptySelection,
            WalletBuildSettingsNotificationsUiState.EmptyDocument -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(Modifier.height(12.dp))
                Text("Nothing to show", style = MaterialTheme.typography.titleMedium)
            }

            WalletBuildSettingsNotificationsUiState.UnsupportedDevice -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(Modifier.height(12.dp))
                Text("Not supported on this device", style = MaterialTheme.typography.titleMedium)
            }

            WalletBuildSettingsNotificationsUiState.Ready,
            WalletBuildSettingsNotificationsUiState.Accepted -> {
                // Content renders here once the relevant managers are attached.
            }
        }
    }
}
