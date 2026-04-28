package com.linkpoint.ui.profile

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.linkpoint.ui.components.linkpoint2.primitives.L2GlassSurface
import com.linkpoint.ui.components.linkpoint2.primitives.L2TonalButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ProfileData(
    val id: UUID,
    val displayName: String,
    val username: String,
    val aboutText: String = "",
    val firstLifeText: String = "",
    val bornDate: Date? = null,
    val partner: String? = null,
    val groups: List<String> = emptyList(),
    val webUrl: String? = null,
    val isOnline: Boolean = false,
    val location: String? = null,
    val isFriend: Boolean = false,
    val isOwnProfile: Boolean = false,
)

/**
 * Linkpoint 2.0 avatar profile — banner gradient, generative avatar,
 * action row (IM/TP/Pay/Befriend/Mute), about/groups/picks. Matches
 * design/screens-3.jsx → ProfileScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: ProfileData,
    onNavigateBack: () -> Unit,
    onSendIM: () -> Unit,
    onAddFriend: () -> Unit,
    onTeleportTo: () -> Unit,
    onEditProfile: () -> Unit,
    onOpenWeb: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    val tokens = Linkpoint2.tokens
    val cs = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            L2TopBar(
                title = profile.displayName,
                subtitle = profile.username,
                leading = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                trailing = {
                    if (profile.isOwnProfile) {
                        IconButton(onClick = onEditProfile) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit profile")
                        }
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            // Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(cs.primary, cs.tertiary, cs.secondary),
                        ),
                    ),
            )
            // Avatar overlapping banner
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .offset(y = (-48).dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .clip(CircleShape)
                        .background(cs.surface)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    L2Avatar(
                        name = profile.displayName,
                        size = AvatarSize.XL,
                        online = profile.isOnline,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    Text(
                        profile.displayName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(profile.username, color = tokens.onSurfaceDim, style = MaterialTheme.typography.bodySmall)
                    if (profile.isOnline) {
                        L2Chip(
                            label = profile.location ?: "Online",
                            variant = L2ChipVariant.Success,
                        )
                    }
                }
            }
            // Action row (offset compensated)
            if (!profile.isOwnProfile) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-32).dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    L2FilledButton(onClick = onSendIM, modifier = Modifier.weight(1f)) {
                        Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Message")
                    }
                    if (profile.isOnline) {
                        L2TonalButton(onClick = onTeleportTo, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Teleport")
                        }
                    }
                    L2TonalButton(onClick = { /* pay */ }) {
                        Icon(Icons.Default.Payments, contentDescription = "Pay")
                    }
                    if (!profile.isFriend) {
                        L2GhostButton(onClick = onAddFriend) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Befriend")
                        }
                    }
                    L2GhostButton(onClick = { /* mute */ }) {
                        Icon(Icons.AutoMirrored.Filled.VolumeOff, contentDescription = "Mute")
                    }
                }
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                if (profile.aboutText.isNotBlank()) {
                    L2GlassSurface(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(14.dp),
                    ) {
                        Column {
                            Text("About", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(4.dp))
                            Text(profile.aboutText, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
                if (profile.firstLifeText.isNotBlank()) {
                    L2GlassSurface(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(14.dp),
                    ) {
                        Column {
                            Text("First life", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(4.dp))
                            Text(profile.firstLifeText, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
                L2Card(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        profile.bornDate?.let { date ->
                            ProfileInfoRow(
                                icon = Icons.Default.Cake,
                                label = "Born",
                                value = dateFormat.format(date),
                            )
                        }
                        profile.partner?.let { partner ->
                            ProfileInfoRow(
                                icon = Icons.Default.Person,
                                label = "Partner",
                                value = partner,
                            )
                        }
                        profile.webUrl?.let { url ->
                            ProfileInfoRow(
                                icon = Icons.Default.Public,
                                label = "Web",
                                value = url,
                                onClick = { onOpenWeb(url) },
                            )
                        }
                    }
                }
                if (profile.groups.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    L2Card(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(8.dp))
                                Text("Groups (${profile.groups.size})", fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(Modifier.height(8.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                profile.groups.forEach { g ->
                                    Text(g, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onClick: (() -> Unit)? = null,
) {
    val tokens = Linkpoint2.tokens
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = tokens.onSurfaceDim)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = tokens.onSurfaceDim)
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                color = if (onClick != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
