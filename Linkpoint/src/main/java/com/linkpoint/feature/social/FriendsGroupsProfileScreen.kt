package com.linkpoint.feature.social

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class FriendsGroupsProfileUiState {
    Loading,
    Error,
    Empty,
    Ready
}

@Composable
fun FriendsGroupsProfileScreen(state: FriendsGroupsProfileUiState) {
    Text("FriendsGroupsProfileScreen stub: $state")
}
