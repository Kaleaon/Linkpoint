package com.linkpoint.feature.social

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.linkpoint.ui.components.state.GuidedEmptyStateView
import com.linkpoint.ui.components.state.SkeletonListLoader

sealed interface GroupsUiState {
    data object Loading : GroupsUiState
    data object Error : GroupsUiState
    data object EmptyGroups : GroupsUiState
    data object Ready : GroupsUiState
}

@Composable
fun GroupsScreen(
    state: GroupsUiState,
    onAction: (() -> Unit)? = null
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            GroupsUiState.Loading -> SkeletonListLoader()

            GroupsUiState.Error -> GuidedEmptyStateView(
                title = "Could not load groups",
                message = "Failed to load groups list.",
                icon = Icons.Default.Warning,
                actionLabel = onAction?.let { "Reload" },
                onAction = onAction
            )

            GroupsUiState.EmptyGroups -> GuidedEmptyStateView(
                title = "No groups joined",
                message = "Join a group in-world to see it here.",
                icon = Icons.Default.Group,
                actionLabel = onAction?.let { "Find Groups" },
                onAction = onAction
            )

            GroupsUiState.Ready -> {
                // Group list renders here once GroupManager is attached.
            }
        }
    }
}
