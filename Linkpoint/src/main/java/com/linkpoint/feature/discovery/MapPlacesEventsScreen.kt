package com.linkpoint.feature.discovery

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

sealed interface MapPlacesEventsUiState {
    data object Loading : MapPlacesEventsUiState
    data object Error : MapPlacesEventsUiState
    data object EmptyRegionResults : MapPlacesEventsUiState
    data object EmptySearchResults : MapPlacesEventsUiState
    data object InvalidLocation : MapPlacesEventsUiState
    data object EmptyHistory : MapPlacesEventsUiState
    data object EmptySurroundings : MapPlacesEventsUiState
    data object EmptyNearbyAgents : MapPlacesEventsUiState
    data object Ready : MapPlacesEventsUiState
}

@Composable
fun MapPlacesEventsScreen(state: MapPlacesEventsUiState) {
    Text("MapPlacesEventsScreen stub: $state")
}
