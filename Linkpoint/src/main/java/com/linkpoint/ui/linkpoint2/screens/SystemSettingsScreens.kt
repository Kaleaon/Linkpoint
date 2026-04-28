package com.linkpoint.ui.linkpoint2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.components.linkpoint2.primitives.L2Chip
import com.linkpoint.ui.components.linkpoint2.primitives.L2ChipVariant
import com.linkpoint.ui.components.linkpoint2.primitives.L2FilledButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2GhostButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2GlassSurface
import com.linkpoint.ui.components.linkpoint2.primitives.L2Row
import com.linkpoint.ui.components.linkpoint2.primitives.L2SectionHeader
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

enum class GraphicsPreset { Battery, Balanced, High, Ultra }

data class GraphicsState(
    val preset: GraphicsPreset = GraphicsPreset.Balanced,
    val drawDistanceM: Float = 128f,
    val lod: Float = 0.6f,
    val textures: Float = 0.7f,
    val particles: Float = 0.5f,
    val shadows: Boolean = false,
)

/**
 * Cluster H — Graphics settings. See design/screens-5.jsx → GraphicsScreen.
 */
@Composable
fun GraphicsSettingsScreen(
    initial: GraphicsState = GraphicsState(),
    onBack: () -> Unit,
    onChange: (GraphicsState) -> Unit,
    modifier: Modifier = Modifier,
) {
    var state by remember { mutableStateOf(initial) }
    val tokens = Linkpoint2.tokens

    Scaffold(
        topBar = {
            L2TopBar(
                title = "Graphics",
                subtitle = "Quality & performance",
                leading = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                L2SectionHeader("Preset")
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GraphicsPreset.entries.forEach { p ->
                        L2Chip(
                            label = p.name,
                            variant = if (p == state.preset) L2ChipVariant.Primary else L2ChipVariant.Neutral,
                            onClick = {
                                state = state.copy(preset = p)
                                onChange(state)
                            },
                        )
                    }
                }
            }
            item {
                SliderRow("Draw distance", "${state.drawDistanceM.toInt()} m", state.drawDistanceM / 256f) {
                    state = state.copy(drawDistanceM = it * 256f)
                    onChange(state)
                }
            }
            item {
                SliderRow("Level of detail", "${(state.lod * 100).toInt()}%", state.lod) {
                    state = state.copy(lod = it)
                    onChange(state)
                }
            }
            item {
                SliderRow("Textures", "${(state.textures * 100).toInt()}%", state.textures) {
                    state = state.copy(textures = it)
                    onChange(state)
                }
            }
            item {
                SliderRow("Particles", "${(state.particles * 100).toInt()}%", state.particles) {
                    state = state.copy(particles = it)
                    onChange(state)
                }
            }
            item {
                L2Row(
                    headline = "Real-time shadows",
                    supporting = "Adds depth — uses GPU.",
                    leading = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(tokens.radii.md))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Default.HighQuality, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    trailing = {
                        Switch(
                            checked = state.shadows,
                            onCheckedChange = {
                                state = state.copy(shadows = it)
                                onChange(state)
                            },
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun SliderRow(
    label: String,
    valueLabel: String,
    progress: Float,
    onProgress: (Float) -> Unit,
) {
    var p by remember { mutableFloatStateOf(progress) }
    val tokens = Linkpoint2.tokens
    L2GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp),
    ) {
        Column {
            Row {
                Text(label, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Text(valueLabel, color = tokens.onSurfaceDim, style = MaterialTheme.typography.labelSmall)
            }
            Slider(
                value = p,
                onValueChange = {
                    p = it
                    onProgress(it)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

data class PrivacyState(
    val showOnline: Boolean = true,
    val showInSearch: Boolean = true,
    val voiceScopeNearby: Boolean = true,
    val showInProfile: Boolean = true,
)

/**
 * Cluster H — Privacy settings. See design/screens-5.jsx → PrivacyScreen.
 */
@Composable
fun PrivacySettingsScreen(
    initial: PrivacyState = PrivacyState(),
    blockedUsers: List<String> = emptyList(),
    onBack: () -> Unit,
    onUnblock: (String) -> Unit,
    onChange: (PrivacyState) -> Unit,
    modifier: Modifier = Modifier,
) {
    var s by remember { mutableStateOf(initial) }
    Scaffold(
        topBar = {
            L2TopBar(
                title = "Privacy",
                subtitle = "Visibility & blocking",
                leading = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(padding),
        ) {
            item { L2SectionHeader("Visibility") }
            item {
                ToggleRow("Show online status", "Friends can see you're online.", Icons.Default.Public, s.showOnline) {
                    s = s.copy(showOnline = it); onChange(s)
                }
            }
            item {
                ToggleRow("Appear in search", "Other residents can find you by name.", Icons.Default.Tune, s.showInSearch) {
                    s = s.copy(showInSearch = it); onChange(s)
                }
            }
            item {
                ToggleRow("Voice — nearby only", "Limit voice to spatial scope.", Icons.Default.Lock, s.voiceScopeNearby) {
                    s = s.copy(voiceScopeNearby = it); onChange(s)
                }
            }
            item { L2SectionHeader("Blocked · ${blockedUsers.size}") }
            items(blockedUsers, key = { it }) { name ->
                L2Row(
                    headline = name,
                    supporting = "Blocked",
                    trailing = {
                        IconButton(onClick = { onUnblock(name) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Unblock")
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    sub: String,
    icon: ImageVector,
    checked: Boolean,
    onChecked: (Boolean) -> Unit,
) {
    val tokens = Linkpoint2.tokens
    L2Row(
        headline = title,
        supporting = sub,
        leading = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(tokens.radii.md))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        },
        trailing = {
            Switch(checked = checked, onCheckedChange = onChecked)
        },
    )
}

data class GridEntry(
    val id: String,
    val name: String,
    val loginUrl: String,
    val builtIn: Boolean,
    val online: Boolean,
)

/**
 * Cluster H — Manage grids. See design/screens-5.jsx → GridManagementScreen.
 */
@Composable
fun GridManagementScreen(
    grids: List<GridEntry>,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (GridEntry) -> Unit,
    onDelete: (GridEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = Linkpoint2.tokens
    Scaffold(
        topBar = {
            L2TopBar(
                title = "Grids",
                subtitle = "${grids.size} configured",
                leading = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                trailing = {
                    IconButton(onClick = onAdd) {
                        Icon(Icons.Default.Add, contentDescription = "Add grid")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(vertical = 4.dp),
        ) {
            items(grids, key = { it.id }) { g ->
                L2Row(
                    headline = g.name,
                    supporting = g.loginUrl,
                    leading = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(tokens.radii.md))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Default.Public, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    trailing = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            L2Chip(
                                label = if (g.online) "Online" else "Offline",
                                variant = if (g.online) L2ChipVariant.Success else L2ChipVariant.Neutral,
                            )
                            if (!g.builtIn) {
                                IconButton(onClick = { onDelete(g) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete grid")
                                }
                            }
                            IconButton(onClick = { onEdit(g) }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Edit grid")
                            }
                        }
                    },
                )
            }
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        L2FilledButton(text = "Add custom grid", onClick = onAdd)
                        L2GhostButton(text = "Reset", onClick = { /* reset */ })
                    }
                }
            }
        }
    }
}
