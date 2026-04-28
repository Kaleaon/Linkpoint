package com.linkpoint.ui.linkpoint2.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.components.linkpoint2.primitives.AvatarSize
import com.linkpoint.ui.components.linkpoint2.primitives.L2Avatar
import com.linkpoint.ui.components.linkpoint2.primitives.L2Chip
import com.linkpoint.ui.components.linkpoint2.primitives.L2ChipVariant
import com.linkpoint.ui.components.linkpoint2.primitives.L2GlassSurface
import com.linkpoint.ui.components.linkpoint2.primitives.L2Row
import com.linkpoint.ui.components.linkpoint2.primitives.L2SectionHeader
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

data class VoiceParticipant(
    val id: String,
    val name: String,
    val volume: Float,
    val muted: Boolean,
    val distanceMeters: Int,
    val speaking: Boolean,
)

enum class VoiceChannel(val label: String) { Spatial("Spatial"), Group("Group"), Private("Private") }

/**
 * Cluster E — Voice (spatial). See design/screens-extra.jsx → VoiceDeepScreen.
 */
@Composable
fun VoiceDeepScreen(
    selfName: String,
    participants: List<VoiceParticipant>,
    onBack: () -> Unit,
    onTogglePtt: (Boolean) -> Unit,
    onMuteParticipant: (VoiceParticipant) -> Unit,
    modifier: Modifier = Modifier,
) {
    var channel by remember { mutableStateOf(VoiceChannel.Spatial) }
    var pttPressed by remember { mutableStateOf(false) }
    val tokens = Linkpoint2.tokens

    Scaffold(
        topBar = {
            L2TopBar(
                title = "Voice",
                subtitle = "${channel.label} channel",
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
            // Channel chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                VoiceChannel.entries.forEach { ch ->
                    L2Chip(
                        label = ch.label,
                        variant = if (ch == channel) L2ChipVariant.Primary else L2ChipVariant.Neutral,
                        onClick = { channel = ch },
                    )
                }
            }

            // PTT button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                PttButton(
                    pressed = pttPressed,
                    onPressed = {
                        pttPressed = it
                        onTogglePtt(it)
                    },
                )
            }

            // Waveform placeholder
            L2GlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    LinearProgressIndicator(
                        progress = { if (pttPressed) 0.85f else 0.05f },
                        modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)),
                    )
                }
            }

            L2SectionHeader("In range · ${participants.size}")
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(participants, key = { it.id }) { p ->
                    L2Row(
                        headline = p.name,
                        supporting = "${p.distanceMeters}m away",
                        leading = {
                            L2Avatar(
                                name = p.name,
                                size = AvatarSize.MD,
                                online = p.speaking,
                            )
                        },
                        trailing = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .height(6.dp)
                                        .width(60.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .height(6.dp)
                                            .width((60f * p.volume).dp.coerceAtLeast(0.dp))
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(
                                                if (p.muted) tokens.onSurfaceDim else tokens.success,
                                            ),
                                    )
                                }
                                IconButton(onClick = { onMuteParticipant(p) }) {
                                    Icon(
                                        if (p.muted) Icons.Default.MicOff else Icons.Default.Mic,
                                        contentDescription = if (p.muted) "Unmute" else "Mute",
                                        tint = if (p.muted) MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun PttButton(
    pressed: Boolean,
    onPressed: (Boolean) -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "ptt-rings")
    val ring by transition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ring",
    )
    Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
        if (pressed) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(ring)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = (1.4f - ring).coerceIn(0f, 1f)), CircleShape),
            )
        }
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    if (pressed) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                )
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(
                onClick = { onPressed(!pressed) },
                modifier = Modifier.size(120.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (pressed) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = if (pressed) "Talking" else "Push to talk",
                        tint = if (pressed) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp),
                    )
                    Text(
                        text = if (pressed) "Talking" else "Push to talk",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (pressed) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
    @Suppress("UNUSED_VARIABLE")
    val unused: Color = Color.Transparent
}
