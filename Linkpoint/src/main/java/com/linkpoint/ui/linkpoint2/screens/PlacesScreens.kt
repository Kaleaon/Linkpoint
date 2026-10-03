package com.linkpoint.ui.linkpoint2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.components.linkpoint2.primitives.L2Chip
import com.linkpoint.ui.components.linkpoint2.primitives.L2ChipVariant
import com.linkpoint.ui.components.linkpoint2.primitives.L2CoordText
import com.linkpoint.ui.components.linkpoint2.primitives.L2FilledButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2GhostButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2GlassSurface
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

data class PlaceCard(
    val id: String,
    val name: String,
    val rating: String,
    val traffic: Int,
    val parcel: String,
    val coverGradient: List<Color>,
)

data class EventCard(
    val id: String,
    val title: String,
    val region: String,
    val time: String,
    val category: String,
)

/**
 * Cluster G — Places (search). See design/screens-4.jsx → PlacesScreen.
 */
@Composable
fun PlacesScreen(
    places: List<PlaceCard>,
    onBack: () -> Unit,
    onSelect: (PlaceCard) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    val tokens = Linkpoint2.tokens

    Scaffold(
        topBar = {
            L2TopBar(
                title = "Places",
                subtitle = "Discover regions",
                leading = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search places…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(tokens.radii.pill),
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(places.filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }, key = { it.id }) { place ->
                    PlaceListCard(place = place, onClick = { onSelect(place) })
                }
            }
        }
    }
}

@Composable
private fun PlaceListCard(
    place: PlaceCard,
    onClick: () -> Unit,
) {
    val tokens = Linkpoint2.tokens
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(tokens.radii.lg))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Brush.linearGradient(place.coverGradient)),
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    L2Chip(
                        label = place.rating,
                        variant = L2ChipVariant.Primary,
                        leading = {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(12.dp))
                        },
                    )
                    L2Chip(label = "${place.traffic} here")
                }
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Text(place.name, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                L2CoordText(text = place.parcel)
            }
        }
    }
}

@Composable
fun PlaceDetailScreen(
    place: PlaceCard,
    description: String,
    whoIsHere: List<String>,
    onBack: () -> Unit,
    onTeleport: () -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = Linkpoint2.tokens
    Scaffold(
        topBar = {
            L2TopBar(
                title = place.name,
                subtitle = place.rating,
                leading = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                trailing = {
                    IconButton(onClick = onShare) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Brush.linearGradient(place.coverGradient)),
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(place.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                L2CoordText(text = place.parcel)
                Spacer(Modifier.height(12.dp))
                L2GlassSurface(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(14.dp),
                ) {
                    Column {
                        Text("About", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            description,
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.onSurfaceDim,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text("Who's here", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Row(modifier = Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    whoIsHere.take(6).forEach { name ->
                        L2Chip(label = name)
                    }
                }
                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    L2FilledButton(
                        onClick = onTeleport,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Teleport")
                    }
                    L2GhostButton(text = "Save", onClick = onSave, modifier = Modifier.weight(1f))
                    L2GhostButton(text = "Share", onClick = onShare, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun EventsScreen(
    events: List<EventCard>,
    onBack: () -> Unit,
    onSelect: (EventCard) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = Linkpoint2.tokens
    Scaffold(
        topBar = {
            L2TopBar(
                title = "Events",
                subtitle = "Marketplace & happenings",
                leading = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(events, key = { it.id }) { e ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(tokens.radii.lg))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { onSelect(e) }
                        .padding(14.dp),
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            L2Chip(label = e.category, variant = L2ChipVariant.Primary)
                            Spacer(Modifier.weight(1f))
                            L2CoordText(text = e.time)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            e.title,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        L2CoordText(text = e.region)
                    }
                }
            }
        }
    }
}

