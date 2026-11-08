package com.linkpoint.ui.world

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

data class WorldShellScreenState(
    val title: String = "World",
    val description: String = "Loading world…"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldShellDestination(
    state: WorldShellScreenState = WorldShellScreenState(),
    modifier: Modifier = Modifier
) {
    DestinationScaffold(state.title, state.description, modifier)
}

data class MapScreenState(
    val title: String = "Map",
    val description: String = "Loading map…"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapDestination(
    state: MapScreenState = MapScreenState(),
    modifier: Modifier = Modifier
) {
    DestinationScaffold(state.title, state.description, modifier)
}

data class MinimapScreenState(
    val title: String = "Minimap",
    val description: String = "Loading minimap…"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinimapDestination(
    state: MinimapScreenState = MinimapScreenState(),
    modifier: Modifier = Modifier
) {
    DestinationScaffold(state.title, state.description, modifier)
}

data class XrWorldScreenState(
    val title: String = "XR World",
    val description: String = "Loading XR world…"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XrWorldDestination(
    state: XrWorldScreenState = XrWorldScreenState(),
    modifier: Modifier = Modifier
) {
    DestinationScaffold(state.title, state.description, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DestinationScaffold(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(title) })
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(text = description)
        }
    }
}
