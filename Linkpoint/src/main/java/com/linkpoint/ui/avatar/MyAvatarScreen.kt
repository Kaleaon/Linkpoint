package com.linkpoint.ui.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Avatar appearance data
 */
data class AvatarAppearance(
    val height: Float = 1.0f,
    val bodyFat: Float = 0.5f,
    val torsoMuscle: Float = 0.5f,
    val headSize: Float = 0.5f,
    val currentOutfit: String = "Default"
)

/**
 * Compose version of MyAvatarActivity.
 * 
 * Features:
 * - Avatar preview
 * - Appearance sliders
 * - Outfit management
 * - Rebake textures
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAvatarScreen(
    appearance: AvatarAppearance,
    onAppearanceChange: (AvatarAppearance) -> Unit,
    onRebakeTextures: () -> Unit,
    onOpenAppearanceEditor: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Appearance", "Outfit", "Shape")
    
    Scaffold(
        topBar = {
            com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar(
                title = "My avatar",
                subtitle = "Outfit & wearables",
                leading = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                trailing = {
                    IconButton(onClick = onRebakeTextures) {
                        Icon(Icons.Default.Refresh, contentDescription = "Rebake textures")
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Avatar preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for avatar preview
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            // Tab content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> AppearanceTab(
                        appearance = appearance,
                        onAppearanceChange = onAppearanceChange
                    )
                    1 -> OutfitTab(
                        currentOutfit = appearance.currentOutfit,
                        onOpenEditor = onOpenAppearanceEditor
                    )
                    2 -> ShapeTab(
                        appearance = appearance,
                        onAppearanceChange = onAppearanceChange
                    )
                }
            }
        }
    }
}

/**
 * Appearance tab with quick sliders
 */
@Composable
private fun AppearanceTab(
    appearance: AvatarAppearance,
    onAppearanceChange: (AvatarAppearance) -> Unit,
    modifier: Modifier = Modifier
) {
    var height by remember { mutableFloatStateOf(appearance.height) }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Height",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = height,
                        onValueChange = { 
                            height = it
                            onAppearanceChange(appearance.copy(height = it))
                        },
                        valueRange = 0.5f..1.5f,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${(height * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        
        Button(
            onClick = { /* Open full editor */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Full Appearance")
        }
    }
}

/**
 * Outfit tab
 */
@Composable
private fun OutfitTab(
    currentOutfit: String,
    onOpenEditor: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Current Outfit",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = currentOutfit,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { /* Save current outfit */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Outfit")
            }
            
            Button(
                onClick = onOpenEditor,
                modifier = Modifier.weight(1f)
            ) {
                Text("Edit Outfit")
            }
        }
    }
}

/**
 * Shape tab with body sliders
 */
@Composable
private fun ShapeTab(
    appearance: AvatarAppearance,
    onAppearanceChange: (AvatarAppearance) -> Unit,
    modifier: Modifier = Modifier
) {
    var bodyFat by remember { mutableFloatStateOf(appearance.bodyFat) }
    var torsoMuscle by remember { mutableFloatStateOf(appearance.torsoMuscle) }
    var headSize by remember { mutableFloatStateOf(appearance.headSize) }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Body Fat
        ShapeSlider(
            label = "Body Fat",
            value = bodyFat,
            onValueChange = { 
                bodyFat = it
                onAppearanceChange(appearance.copy(bodyFat = it))
            }
        )
        
        // Torso Muscle
        ShapeSlider(
            label = "Torso Muscle",
            value = torsoMuscle,
            onValueChange = { 
                torsoMuscle = it
                onAppearanceChange(appearance.copy(torsoMuscle = it))
            }
        )
        
        // Head Size
        ShapeSlider(
            label = "Head Size",
            value = headSize,
            onValueChange = { 
                headSize = it
                onAppearanceChange(appearance.copy(headSize = it))
            }
        )
    }
}

/**
 * Shape slider component
 */
@Composable
private fun ShapeSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${(value * 100).toInt()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
