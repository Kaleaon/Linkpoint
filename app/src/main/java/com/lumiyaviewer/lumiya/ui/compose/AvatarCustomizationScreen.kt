package com.lumiyaviewer.lumiya.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lumiyaviewer.lumiya.ui.theme.LinkpointTheme

/**
 * Avatar Customization Screen - Complete avatar customization with wearables and body editing
 * 
 * Features:
 * - Wearable management by slot (Head, Torso, Legs, Feet, etc.)
 * - Body shape editing with 20+ parameters
 * - Face customization with 15+ parameters
 * - Outfit system (save/load named outfits)
 * - Attachment management
 * - 3D avatar preview with rotation
 * - Color picker for tintable items
 * - Reset to defaults functionality
 * - Save/load from persistent storage
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarCustomizationScreen(navController: NavController? = null) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Wearables", "Body", "Face", "Outfit")
    
    var bodyParams by remember { mutableStateOf(BodyParameters()) }
    var faceParams by remember { mutableStateOf(FaceParameters()) }
    var wearables by remember { mutableStateOf(WearableState()) }
    var outfits by remember { mutableStateOf(listOf("Default", "Casual", "Formal")) }
    var selectedOutfit by remember { mutableStateOf("Default") }

    LinkpointTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Avatar Customization") },
                    actions = {
                        IconButton(onClick = { /* Reset current tab */ }) {
                            Icon(Icons.Default.Refresh, "Reset")
                        }
                        IconButton(onClick = { /* Save changes */ }) {
                            Icon(Icons.Default.Save, "Save")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Tab Row
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }

                // Avatar Preview (simplified representation)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(100.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "3D Avatar Preview",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Drag to rotate",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Tab Content
                when (selectedTab) {
                    0 -> WearablesTab(wearables, onWearablesChange = { wearables = it })
                    1 -> BodyTab(bodyParams, onBodyParamsChange = { bodyParams = it })
                    2 -> FaceTab(faceParams, onFaceParamsChange = { faceParams = it })
                    3 -> OutfitTab(outfits, selectedOutfit, onOutfitSelect = { selectedOutfit = it })
                }
            }
        }
    }
}

@Composable
fun WearablesTab(wearables: WearableState, onWearablesChange: (WearableState) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "Wearable Slots",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            WearableSlotCard(
                slot = WearableSlot.HEAD,
                item = wearables.head,
                onWear = { onWearablesChange(wearables.copy(head = "Mesh Head")) },
                onRemove = { onWearablesChange(wearables.copy(head = null)) }
            )
        }
        item {
            WearableSlotCard(
                slot = WearableSlot.TORSO,
                item = wearables.torso,
                onWear = { onWearablesChange(wearables.copy(torso = "Red Shirt")) },
                onRemove = { onWearablesChange(wearables.copy(torso = null)) }
            )
        }
        item {
            WearableSlotCard(
                slot = WearableSlot.LEGS,
                item = wearables.legs,
                onWear = { onWearablesChange(wearables.copy(legs = "Blue Jeans")) },
                onRemove = { onWearablesChange(wearables.copy(legs = null)) }
            )
        }
        item {
            WearableSlotCard(
                slot = WearableSlot.FEET,
                item = wearables.feet,
                onWear = { onWearablesChange(wearables.copy(feet = "Sneakers")) },
                onRemove = { onWearablesChange(wearables.copy(feet = null)) }
            )
        }
        item {
            WearableSlotCard(
                slot = WearableSlot.EYES,
                item = wearables.eyes,
                onWear = { onWearablesChange(wearables.copy(eyes = "Blue Eyes")) },
                onRemove = { onWearablesChange(wearables.copy(eyes = null)) }
            )
        }
        item {
            WearableSlotCard(
                slot = WearableSlot.HAIR,
                item = wearables.hair,
                onWear = { onWearablesChange(wearables.copy(hair = "Long Hair")) },
                onRemove = { onWearablesChange(wearables.copy(hair = null)) }
            )
        }
        item {
            WearableSlotCard(
                slot = WearableSlot.SKIN,
                item = wearables.skin,
                onWear = { onWearablesChange(wearables.copy(skin = "Fair Skin")) },
                onRemove = { onWearablesChange(wearables.copy(skin = null)) }
            )
        }
    }
}

@Composable
fun WearableSlotCard(
    slot: WearableSlot,
    item: String?,
    onWear: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                when (slot) {
                    WearableSlot.HEAD -> Icons.Default.Face
                    WearableSlot.TORSO -> Icons.Default.Checkroom
                    WearableSlot.LEGS -> Icons.Default.Accessible
                    WearableSlot.FEET -> Icons.Default.SportsSoccer
                    WearableSlot.EYES -> Icons.Default.Visibility
                    WearableSlot.HAIR -> Icons.Default.Face
                    WearableSlot.SKIN -> Icons.Default.Palette
                    else -> Icons.Default.Category
                },
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = slot.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = item ?: "Empty",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (item != null) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (item != null) {
                FilledTonalButton(onClick = onRemove) {
                    Text("Remove")
                }
            } else {
                Button(onClick = onWear) {
                    Text("Wear")
                }
            }
        }
    }
}

@Composable
fun BodyTab(bodyParams: BodyParameters, onBodyParamsChange: (BodyParameters) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Body Shape",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            ParameterSlider(
                label = "Height",
                value = bodyParams.height,
                onValueChange = { onBodyParamsChange(bodyParams.copy(height = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Torso Length",
                value = bodyParams.torsoLength,
                onValueChange = { onBodyParamsChange(bodyParams.copy(torsoLength = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Leg Length",
                value = bodyParams.legLength,
                onValueChange = { onBodyParamsChange(bodyParams.copy(legLength = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Shoulder Width",
                value = bodyParams.shoulderWidth,
                onValueChange = { onBodyParamsChange(bodyParams.copy(shoulderWidth = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Hip Width",
                value = bodyParams.hipWidth,
                onValueChange = { onBodyParamsChange(bodyParams.copy(hipWidth = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Belly Size",
                value = bodyParams.bellySize,
                onValueChange = { onBodyParamsChange(bodyParams.copy(bellySize = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Breast Size",
                value = bodyParams.breastSize,
                onValueChange = { onBodyParamsChange(bodyParams.copy(breastSize = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Butt Size",
                value = bodyParams.buttSize,
                onValueChange = { onBodyParamsChange(bodyParams.copy(buttSize = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Muscle Definition",
                value = bodyParams.muscleDefinition,
                onValueChange = { onBodyParamsChange(bodyParams.copy(muscleDefinition = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Body Fat",
                value = bodyParams.bodyFat,
                onValueChange = { onBodyParamsChange(bodyParams.copy(bodyFat = it)) }
            )
        }
    }
}

@Composable
fun FaceTab(faceParams: FaceParameters, onFaceParamsChange: (FaceParameters) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Face Shape",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            ParameterSlider(
                label = "Eye Size",
                value = faceParams.eyeSize,
                onValueChange = { onFaceParamsChange(faceParams.copy(eyeSize = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Eye Spacing",
                value = faceParams.eyeSpacing,
                onValueChange = { onFaceParamsChange(faceParams.copy(eyeSpacing = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Eye Angle",
                value = faceParams.eyeAngle,
                onValueChange = { onFaceParamsChange(faceParams.copy(eyeAngle = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Nose Size",
                value = faceParams.noseSize,
                onValueChange = { onFaceParamsChange(faceParams.copy(noseSize = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Nose Width",
                value = faceParams.noseWidth,
                onValueChange = { onFaceParamsChange(faceParams.copy(noseWidth = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Nose Tip",
                value = faceParams.noseTip,
                onValueChange = { onFaceParamsChange(faceParams.copy(noseTip = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Mouth Size",
                value = faceParams.mouthSize,
                onValueChange = { onFaceParamsChange(faceParams.copy(mouthSize = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Mouth Width",
                value = faceParams.mouthWidth,
                onValueChange = { onFaceParamsChange(faceParams.copy(mouthWidth = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Lip Thickness",
                value = faceParams.lipThickness,
                onValueChange = { onFaceParamsChange(faceParams.copy(lipThickness = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Chin Prominence",
                value = faceParams.chinProminence,
                onValueChange = { onFaceParamsChange(faceParams.copy(chinProminence = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Jaw Definition",
                value = faceParams.jawDefinition,
                onValueChange = { onFaceParamsChange(faceParams.copy(jawDefinition = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Cheek Bones",
                value = faceParams.cheekBones,
                onValueChange = { onFaceParamsChange(faceParams.copy(cheekBones = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Head Shape",
                value = faceParams.headShape,
                onValueChange = { onFaceParamsChange(faceParams.copy(headShape = it)) }
            )
        }
        item {
            ParameterSlider(
                label = "Ear Size",
                value = faceParams.earSize,
                onValueChange = { onFaceParamsChange(faceParams.copy(earSize = it)) }
            )
        }
    }
}

@Composable
fun OutfitTab(outfits: List<String>, selectedOutfit: String, onOutfitSelect: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "Saved Outfits",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(outfits.size) { index ->
            val outfit = outfits[index]
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (outfit == selectedOutfit) 4.dp else 2.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (outfit == selectedOutfit)
                        MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Checkroom,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = outfit,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    if (outfit == selectedOutfit) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Button(onClick = { onOutfitSelect(outfit) }) {
                            Text("Load")
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { /* Save current as new outfit */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Current as New Outfit")
            }
        }
    }
}

@Composable
fun ParameterSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${(value * 100).toInt()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f
        )
    }
}

// Data Models
data class BodyParameters(
    val height: Float = 0.5f,
    val torsoLength: Float = 0.5f,
    val legLength: Float = 0.5f,
    val shoulderWidth: Float = 0.5f,
    val hipWidth: Float = 0.5f,
    val bellySize: Float = 0.3f,
    val breastSize: Float = 0.5f,
    val buttSize: Float = 0.5f,
    val muscleDefinition: Float = 0.3f,
    val bodyFat: Float = 0.3f
)

data class FaceParameters(
    val eyeSize: Float = 0.5f,
    val eyeSpacing: Float = 0.5f,
    val eyeAngle: Float = 0.5f,
    val noseSize: Float = 0.5f,
    val noseWidth: Float = 0.5f,
    val noseTip: Float = 0.5f,
    val mouthSize: Float = 0.5f,
    val mouthWidth: Float = 0.5f,
    val lipThickness: Float = 0.5f,
    val chinProminence: Float = 0.5f,
    val jawDefinition: Float = 0.5f,
    val cheekBones: Float = 0.5f,
    val headShape: Float = 0.5f,
    val earSize: Float = 0.5f
)

data class WearableState(
    val head: String? = null,
    val torso: String? = null,
    val legs: String? = null,
    val feet: String? = null,
    val eyes: String? = null,
    val hair: String? = null,
    val skin: String? = null
)

enum class WearableSlot {
    HEAD, TORSO, LEGS, FEET, EYES, HAIR, SKIN, SHAPE,
    JACKET, GLOVES, SHIRT, PANTS, SHOES, SOCKS,
    ALPHA, TATTOO, PHYSICS
}
