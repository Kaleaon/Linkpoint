package com.linkpoint.ui.linkpoint2.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.components.linkpoint2.primitives.AvatarSize
import com.linkpoint.ui.components.linkpoint2.primitives.L2Avatar
import com.linkpoint.ui.components.linkpoint2.primitives.L2GhostButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2GlassSurface
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

/**
 * Cluster D — Chat states. See design/screens-states.jsx → ChatStatesScreen.
 *
 * Each composable is reusable inside the chat list to demonstrate
 * typing / send-status / failed-with-retry / reconnecting behavior.
 */
@Composable
fun TypingIndicator(
    name: String,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "typing")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "phase",
    )

    Row(
        modifier = modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        L2Avatar(name = name, size = AvatarSize.SM)
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) { i ->
                    val opacity = ((phase + i * 0.33f) % 1f).let {
                        (1f - kotlin.math.abs(it - 0.5f) * 2f).coerceIn(0.2f, 1f)
                    }
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                Linkpoint2.tokens.onSurfaceDim.copy(alpha = opacity),
                            ),
                    )
                }
            }
        }
    }
}

@Composable
fun FailedSendBubble(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp))
                .background(cs.error.copy(alpha = 0.18f))
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(message, color = cs.onSurface)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = cs.error, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Failed · tap to retry",
                        style = MaterialTheme.typography.labelSmall,
                        color = cs.error,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onRetry() },
                    )
                }
            }
        }
    }
}

@Composable
fun ReconnectingPill(
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "reconnecting")
    val pulse by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )
    L2GlassSurface(
        modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(Linkpoint2.radii.pill),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Linkpoint2.tokens.warning.copy(alpha = pulse.coerceIn(0f, 1f))),
            )
            Spacer(Modifier.width(8.dp))
            Text("Reconnecting…", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.Sync, contentDescription = null, tint = Linkpoint2.tokens.warning)
        }
    }
}

