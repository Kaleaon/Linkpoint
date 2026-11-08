package com.linkpoint.ui.linkpoint2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.components.linkpoint2.primitives.AvatarSize
import com.linkpoint.ui.components.linkpoint2.primitives.L2Avatar
import com.linkpoint.ui.components.linkpoint2.primitives.L2Card
import com.linkpoint.ui.components.linkpoint2.primitives.L2Chip
import com.linkpoint.ui.components.linkpoint2.primitives.L2ChipVariant
import com.linkpoint.ui.components.linkpoint2.primitives.L2FilledButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2GhostButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2Row
import com.linkpoint.ui.components.linkpoint2.primitives.L2SectionHeader
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

data class WearableSlot(
    val slot: String,
    val itemName: String?,
    val layer: Int,
    val group: String,
)

data class SavedOutfit(
    val id: String,
    val name: String,
    val avatarSeed: String,
    val accent: androidx.compose.ui.graphics.Color,
)

/**
 * Cluster F — Outfit picker. See design/screens-3.jsx → OutfitPickerScreen.
 */
@Composable
fun OutfitPickerScreen(
    outfits: List<SavedOutfit>,
    onBack: () -> Unit,
    onWear: (SavedOutfit) -> Unit,
    onEdit: (SavedOutfit) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = Linkpoint2.tokens
    Scaffold(
        topBar = {
            L2TopBar(
                title = "Outfits",
                subtitle = "${outfits.size} saved",
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
            items(outfits, key = { it.id }) { o ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(tokens.radii.lg))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(14.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(120.dp)
                                .clip(RoundedCornerShape(tokens.radii.md))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(o.accent, o.accent.copy(alpha = 0.5f)),
                                    ),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            L2Avatar(name = o.avatarSeed, size = AvatarSize.LG)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(o.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Outfit · 12 items",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.onSurfaceDim,
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                L2FilledButton(text = "Wear", onClick = { onWear(o) })
                                L2GhostButton(text = "Edit", onClick = { onEdit(o) })
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Cluster F — Outfit composer. See design/screens-extra.jsx → OutfitComposerScreen.
 */
@Composable
fun OutfitComposerScreen(
    avatarName: String,
    slots: List<WearableSlot>,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onWear: () -> Unit,
    onTap: (WearableSlot) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = Linkpoint2.tokens
    Scaffold(
        topBar = {
            L2TopBar(
                title = "Outfit composer",
                subtitle = avatarName,
                leading = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                trailing = {
                    IconButton(onClick = onSave) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                },
            )
        },
    ) { padding ->
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Avatar preview
            L2Card(
                modifier = Modifier
                    .width(150.dp)
                    .fillMaxHeight()
                    .padding(12.dp),
                contentPadding = PaddingValues(12.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    L2Avatar(name = avatarName, size = AvatarSize.XL)
                    Spacer(Modifier.height(8.dp))
                    L2Chip(label = "Front", variant = L2ChipVariant.Primary)
                    L2Chip(label = "Side")
                    L2Chip(label = "Back")
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { /* cam */ }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                    }
                }
            }

            // Slot list
            val grouped = slots.groupBy { it.group }
            LazyColumn(modifier = Modifier.weight(1f).padding(end = 12.dp, top = 12.dp)) {
                grouped.forEach { (group, list) ->
                    item("hdr_$group") { L2SectionHeader(group) }
                    items(list, key = { "${group}_${it.slot}" }) { slot ->
                        L2Row(
                            headline = slot.slot,
                            supporting = slot.itemName ?: "Empty",
                            leading = {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(tokens.radii.sm))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                ) {
                                    Text(
                                        text = "L${slot.layer}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = tokens.onSurfaceDim,
                                    )
                                }
                            },
                            onClick = { onTap(slot) },
                        )
                    }
                }
                item("actions") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        L2FilledButton(text = "Wear all", onClick = onWear, modifier = Modifier.weight(1f))
                        L2GhostButton(text = "Save outfit", onClick = onSave, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
