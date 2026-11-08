package com.linkpoint.feature.system

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.linkpoint.protocol.types.LLVector3

sealed interface BuildToolsUiState {
    data object Loading : BuildToolsUiState
    data object Error : BuildToolsUiState
    data object EmptySelection : BuildToolsUiState
    data class Ready(
        val selectionName: String = "Selected object",
        val selectionMeta: String = "Single prim · 0.50 m³",
        val primaryObjectLocalId: Int = 0,
        val position: LLVector3 = LLVector3.zero(),
        val isPhantom: Boolean = false,
        val isPhysical: Boolean = false,
        val selectedCount: Int = 1
    ) : BuildToolsUiState
}

@Composable
fun BuildToolsScreen(
    state: BuildToolsUiState,
    onClose: () -> Unit = {},
    onPositionChange: ((LLVector3) -> Unit)? = null,
    onPhantomChange: ((Boolean) -> Unit)? = null,
    onPhysicalChange: ((Boolean) -> Unit)? = null,
    onToolChange: ((com.linkpoint.ui.linkpoint2.screens.BuildTool) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            BuildToolsUiState.Loading -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Loading build tools…", style = MaterialTheme.typography.bodyMedium)
            }

            BuildToolsUiState.Error -> Column(
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
                Text("Something went wrong", style = MaterialTheme.typography.titleMedium)
            }

            BuildToolsUiState.EmptySelection -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(Modifier.height(12.dp))
                Text("No object selected", style = MaterialTheme.typography.titleMedium)
            }

            is BuildToolsUiState.Ready -> {
                com.linkpoint.ui.linkpoint2.screens.BuildToolsScreen(
                    selectionName = state.selectionName,
                    selectionMeta = state.selectionMeta,
                    initialPosition = Triple(state.position.x, state.position.y, state.position.z),
                    initialPhantom = state.isPhantom,
                    initialPhysical = state.isPhysical,
                    onClose = onClose,
                    onPositionChange = { triple ->
                        onPositionChange?.invoke(LLVector3(triple.first, triple.second, triple.third))
                    },
                    onPhantomChange = onPhantomChange,
                    onPhysicalChange = onPhysicalChange,
                    onToolChange = onToolChange,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
fun BuildToolsScreen(
    viewModel: BuildToolsViewModel,
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    BuildToolsScreen(
        state = state,
        onClose = onClose,
        onPositionChange = { newPos ->
            val ready = state as? BuildToolsUiState.Ready
            if (ready != null && ready.primaryObjectLocalId != 0) {
                viewModel.updatePosition(ready.primaryObjectLocalId, newPos)
            }
        },
        onPhantomChange = { phantom ->
            val ready = state as? BuildToolsUiState.Ready
            if (ready != null && ready.primaryObjectLocalId != 0) {
                viewModel.setFlags(ready.primaryObjectLocalId, ready.isPhysical, phantom)
            }
        },
        onPhysicalChange = { physical ->
            val ready = state as? BuildToolsUiState.Ready
            if (ready != null && ready.primaryObjectLocalId != 0) {
                viewModel.setFlags(ready.primaryObjectLocalId, physical, ready.isPhantom)
            }
        },
        modifier = modifier
    )
}
