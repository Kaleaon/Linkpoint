package com.linkpoint.ui.linkpoint2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linkpoint.ui.components.linkpoint2.fx.AuroraBackdrop
import com.linkpoint.ui.components.linkpoint2.primitives.AvatarSize
import com.linkpoint.ui.components.linkpoint2.primitives.L2Avatar
import com.linkpoint.ui.components.linkpoint2.primitives.L2Card
import com.linkpoint.ui.components.linkpoint2.primitives.L2Chip
import com.linkpoint.ui.components.linkpoint2.primitives.L2ChipVariant
import com.linkpoint.ui.components.linkpoint2.primitives.L2FilledButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2GhostButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2GlassSurface
import com.linkpoint.ui.components.linkpoint2.primitives.L2TonalButton
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

/**
 * Cluster A — Onboarding (3 screens). See design/screens-extra.jsx →
 * OnboardingWelcomeScreen / OnboardingAvatarScreen / OnboardingPermissionsScreen.
 */

@Composable
fun OnboardingWelcomeScreen(
    onBegin: () -> Unit,
    onIHaveAnAccount: () -> Unit,
    modifier: Modifier = Modifier,
    progressIndex: Int = 0,
    totalSteps: Int = 3,
) {
    val tokens = Linkpoint2.tokens
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AuroraBackdrop()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(72.dp))
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary,
                            ),
                        ),
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                L2Avatar(name = "Welcome", size = AvatarSize.XXL)
            }
            Spacer(Modifier.height(40.dp))
            Text(
                text = "Welcome to your\nsecond world",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Linkpoint 2.0 — a beautiful, lightweight viewer\nfor Second Life on the go.",
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.onSurfaceDim,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.weight(1f))
            ProgressDots(progressIndex, totalSteps)
            Spacer(Modifier.height(24.dp))
            L2FilledButton(
                onClick = onBegin,
                modifier = Modifier.fillMaxWidth(),
                height = 52.dp,
            ) {
                Text("Begin", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
            Spacer(Modifier.height(8.dp))
            L2GhostButton(
                onClick = onIHaveAnAccount,
                modifier = Modifier.fillMaxWidth(),
                height = 48.dp,
            ) { Text("I have an account") }
        }
    }
}

data class StarterAvatar(
    val id: String,
    val name: String,
    val description: String,
    val accent: Color,
)

val DefaultStarterAvatars = listOf(
    StarterAvatar("vex", "Vex", "Sci-fi explorer", Color(0xFF6DE8FF)),
    StarterAvatar("aria", "Aria", "Festival-goer", Color(0xFFFF8FB1)),
    StarterAvatar("coil", "Coil", "Cyber operative", Color(0xFFA073FF)),
    StarterAvatar("halo", "Halo", "Pacific hippie", Color(0xFFFFD56D)),
    StarterAvatar("drift", "Drift", "Roaming wanderer", Color(0xFF7CFFD8)),
    StarterAvatar("zen", "Zen", "Forest guardian", Color(0xFF79D87E)),
)

@Composable
fun OnboardingAvatarScreen(
    avatars: List<StarterAvatar> = DefaultStarterAvatars,
    onContinue: (StarterAvatar) -> Unit,
    onUploadPhoto: () -> Unit,
    modifier: Modifier = Modifier,
    progressIndex: Int = 1,
    totalSteps: Int = 3,
) {
    var selected by remember { mutableStateOf(avatars.first()) }
    val tokens = Linkpoint2.tokens
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AuroraBackdrop()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 32.dp),
        ) {
            Spacer(Modifier.height(48.dp))
            Text(
                text = "Pick a starter avatar",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "You can change anytime in Inventory.",
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.onSurfaceDim,
                modifier = Modifier.padding(top = 4.dp),
            )
            Spacer(Modifier.height(20.dp))

            // Selected card
            L2GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    L2Avatar(name = selected.name, size = AvatarSize.LG)
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selected.name,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = selected.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.onSurfaceDim,
                        )
                    }
                    L2Chip(label = "Selected", variant = L2ChipVariant.Primary)
                }
            }

            Spacer(Modifier.height(16.dp))
            // Grid of starters
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(avatars, key = { it.id }) { avatar ->
                    StarterAvatarCard(
                        avatar = avatar,
                        selected = selected.id == avatar.id,
                        onClick = { selected = avatar },
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            L2GhostButton(
                onClick = onUploadPhoto,
                modifier = Modifier.fillMaxWidth(),
                height = 48.dp,
            ) {
                Icon(Icons.Default.AddAPhoto, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Or upload a photo")
            }

            Spacer(Modifier.height(16.dp))
            ProgressDots(progressIndex, totalSteps)
            Spacer(Modifier.height(12.dp))
            L2FilledButton(
                onClick = { onContinue(selected) },
                modifier = Modifier.fillMaxWidth(),
                height = 52.dp,
            ) {
                Text("Continue", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
private fun StarterAvatarCard(
    avatar: StarterAvatar,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val tokens = Linkpoint2.tokens
    val shape = RoundedCornerShape(tokens.radii.lg)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                if (selected) 2.dp else 1.dp,
                if (selected) avatar.accent else MaterialTheme.colorScheme.outlineVariant,
                shape,
            )
            .clickable(onClick = onClick),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(avatar.accent, avatar.accent.copy(alpha = 0.6f)),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                L2Avatar(name = avatar.name, size = AvatarSize.LG)
            }
            Spacer(Modifier.height(8.dp))
            Text(avatar.name, fontWeight = FontWeight.SemiBold)
            Text(
                avatar.description,
                style = MaterialTheme.typography.labelSmall,
                color = Linkpoint2.tokens.onSurfaceDim,
            )
        }
    }
}


data class PermissionRequest(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val required: Boolean,
)

val DefaultPermissions = listOf(
    PermissionRequest("mic", "Microphone", "For voice chat with friends and at events.", Icons.Default.Mic, true),
    PermissionRequest("loc", "Location", "Required to suggest nearby regions and events.", Icons.Default.LocationOn, false),
    PermissionRequest("notif", "Notifications", "Receive IM and group notifications when minimised.", Icons.Default.Notifications, false),
    PermissionRequest("cam", "Camera", "For avatar photos and inworld snapshots.", Icons.Default.PhotoCamera, false),
    PermissionRequest("storage", "Storage", "To cache textures and asset data.", Icons.Default.Storage, true),
)

@Composable
fun OnboardingPermissionsScreen(
    permissions: List<PermissionRequest> = DefaultPermissions,
    onAllow: (PermissionRequest) -> Unit,
    onSkip: (PermissionRequest) -> Unit,
    onEnterWorld: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = Linkpoint2.tokens
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AuroraBackdrop(enabled = false)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
        ) {
            Spacer(Modifier.height(40.dp))
            Text(
                text = "A few permissions",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Linkpoint 2.0 needs these to fully connect you.",
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.onSurfaceDim,
                modifier = Modifier.padding(top = 4.dp),
            )
            Spacer(Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f),
            ) {
                permissions.forEach { p ->
                    PermissionRow(p, onAllow = { onAllow(p) }, onSkip = { onSkip(p) })
                }
            }

            Spacer(Modifier.height(16.dp))
            L2FilledButton(
                onClick = onEnterWorld,
                modifier = Modifier.fillMaxWidth(),
                height = 52.dp,
            ) {
                Text("Enter world", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Default.Brightness5, contentDescription = null)
            }
        }
    }
}

@Composable
private fun PermissionRow(
    p: PermissionRequest,
    onAllow: () -> Unit,
    onSkip: () -> Unit,
) {
    L2Card(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(p.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(p.title, fontWeight = FontWeight.SemiBold)
                    if (p.required) {
                        Spacer(Modifier.width(6.dp))
                        L2Chip(label = "Required", variant = L2ChipVariant.Warn)
                    }
                }
                Text(
                    p.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Linkpoint2.tokens.onSurfaceDim,
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                L2TonalButton(text = "Allow", onClick = onAllow)
                Spacer(Modifier.height(4.dp))
                L2GhostButton(text = if (p.required) "—" else "Skip", onClick = onSkip, enabled = !p.required)
            }
        }
    }
}

@Composable
private fun ProgressDots(active: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(total) { i ->
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(if (i == active) 24.dp else 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (i == active) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    ),
            )
        }
    }
}
