package com.linkpoint.ui.discovery

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

data class SearchScreenState(
    val title: String = "Search",
    val description: String = "Search destination placeholder."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDestination(
    state: SearchScreenState = SearchScreenState(),
    modifier: Modifier = Modifier
) {
    DestinationScaffold(state.title, state.description, modifier)
}

data class TeleportHistoryScreenState(
    val title: String = "Teleport History",
    val description: String = "Teleport history destination placeholder."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeleportHistoryDestination(
    state: TeleportHistoryScreenState = TeleportHistoryScreenState(),
    modifier: Modifier = Modifier
) {
    DestinationScaffold(state.title, state.description, modifier)
}

data class SlurlScreenState(
    val title: String = "SLURL",
    val description: String = "SLURL destination placeholder."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlurlDestination(
    state: SlurlScreenState = SlurlScreenState(),
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
