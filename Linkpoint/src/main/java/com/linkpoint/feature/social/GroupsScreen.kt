package com.linkpoint.feature.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

sealed interface GroupsUiState {
    data object Loading : GroupsUiState
    data object Error : GroupsUiState
    data object EmptyGroups : GroupsUiState
    data object Ready : GroupsUiState
}

@Composable
fun GroupsScreen(state: GroupsUiState) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            GroupsUiState.Loading -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Loading groups…", style = MaterialTheme.typography.bodyMedium)
            }

            GroupsUiState.Error -> Column(
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
                Text("Could not load groups", style = MaterialTheme.typography.titleMedium)
            }

            GroupsUiState.EmptyGroups -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(Modifier.height(12.dp))
                Text("No groups joined", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Join a group in-world to see it here.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            GroupsUiState.Ready -> {
                // Group list renders here once GroupManager is attached.
            }
        }
    }
}
