package com.linkpoint.feature.social

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

sealed interface FriendsGroupsProfileUiState {
    data object Loading : FriendsGroupsProfileUiState
    data object Error : FriendsGroupsProfileUiState
    data object EmptyFriends : FriendsGroupsProfileUiState
    data object EmptyGroups : FriendsGroupsProfileUiState
    data object EmptyProfile : FriendsGroupsProfileUiState
    data object EmptyNearby : FriendsGroupsProfileUiState
    data object Ready : FriendsGroupsProfileUiState
}

@Composable
fun FriendsGroupsProfileScreen(state: FriendsGroupsProfileUiState) {
    Text("FriendsGroupsProfileScreen stub: $state")
}
