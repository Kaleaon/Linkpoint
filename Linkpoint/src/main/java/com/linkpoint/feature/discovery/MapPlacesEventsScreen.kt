package com.linkpoint.feature.discovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            MapPlacesEventsUiState.Loading -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Loading map…", style = MaterialTheme.typography.bodyMedium)
            }

            MapPlacesEventsUiState.Error -> Column(
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
                Text("Could not load map data", style = MaterialTheme.typography.titleMedium)
            }

            MapPlacesEventsUiState.EmptyRegionResults,
            MapPlacesEventsUiState.EmptySearchResults,
            MapPlacesEventsUiState.InvalidLocation -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.SearchOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(Modifier.height(12.dp))
                Text("No results found", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Try a different search or location.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            MapPlacesEventsUiState.EmptyHistory,
            MapPlacesEventsUiState.EmptySurroundings,
            MapPlacesEventsUiState.EmptyNearbyAgents -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(Modifier.height(12.dp))
                Text("Nothing here yet", style = MaterialTheme.typography.titleMedium)
            }

            MapPlacesEventsUiState.Ready -> {
                // Map content renders here once the map manager is attached.
            }
        }
    }
}
