package com.linkpoint.feature.discovery

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class MapPlacesEventsUiState {
    Loading,
    Error,
    Empty,
    Ready
}

@Composable
fun MapPlacesEventsScreen(state: MapPlacesEventsUiState) {
    Text("MapPlacesEventsScreen stub: $state")
}
