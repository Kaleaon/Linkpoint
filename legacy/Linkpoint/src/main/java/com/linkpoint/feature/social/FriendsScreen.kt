package com.linkpoint.feature.social

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.linkpoint.ui.components.state.GuidedEmptyStateView
import com.linkpoint.ui.components.state.SkeletonListLoader

sealed interface FriendsUiState {
    data object Loading : FriendsUiState
    data object Error : FriendsUiState
    data object EmptyFriends : FriendsUiState
    data object Ready : FriendsUiState
}

@Composable
fun FriendsScreen(
    state: FriendsUiState,
    onAction: (() -> Unit)? = null
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            FriendsUiState.Loading -> SkeletonListLoader()

            FriendsUiState.Error -> GuidedEmptyStateView(
                title = "Could not load friends",
                message = "Failed to load friends list.",
                icon = Icons.Default.Warning,
                actionLabel = onAction?.let { "Reload" },
                onAction = onAction
            )

            FriendsUiState.EmptyFriends -> GuidedEmptyStateView(
                title = "No friends online",
                message = "Your friends will appear here.",
                icon = Icons.Default.Person,
                actionLabel = onAction?.let { "Find Friends" },
                onAction = onAction
            )

            FriendsUiState.Ready -> {
                // Friends list renders here once FriendManager is attached.
            }
        }
    }
}
