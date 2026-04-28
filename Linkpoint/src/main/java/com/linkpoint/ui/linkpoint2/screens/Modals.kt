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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.components.linkpoint2.primitives.AvatarSize
import com.linkpoint.ui.components.linkpoint2.primitives.L2Avatar
import com.linkpoint.ui.components.linkpoint2.primitives.L2CoordText
import com.linkpoint.ui.components.linkpoint2.primitives.L2FilledButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2GhostButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2GlassSurface
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

/**
 * Cluster D — TP offer modal. See design/screens-states.jsx → TPOfferScreen.
 */
@Composable
fun TPOfferModal(
    senderName: String,
    senderQuote: String,
    destinationName: String,
    coords: String,
    parcelMeta: String,
    secondsRemaining: Int = 60,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = Linkpoint2.tokens
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        L2GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(tokens.radii.xl),
            contentPadding = PaddingValues(20.dp),
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    L2Avatar(name = senderName, size = AvatarSize.MD, online = true)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(senderName, fontWeight = FontWeight.SemiBold)
                        Text("invited you to teleport", style = MaterialTheme.typography.bodySmall, color = tokens.onSurfaceDim)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "“$senderQuote”",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(12.dp))
                // Destination card with mini landscape preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(tokens.radii.lg))
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF1D4060),
                                            Color(0xFF4A7BA8),
                                            Color(0xFFC8B88A),
                                        ),
                                    ),
                                ),
                        )
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(destinationName, fontWeight = FontWeight.SemiBold)
                            L2CoordText(text = "$coords · $parcelMeta")
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    L2GhostButton(text = "Decline", onClick = onDecline, modifier = Modifier.weight(1f))
                    L2FilledButton(text = "Accept", onClick = onAccept, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { (secondsRemaining / 60f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    "Auto-decline in ${secondsRemaining}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.onSurfaceDim,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

/**
 * Cluster H — Permission dialog (debit / animate / attach / teleport-home).
 * See design/screens-extra.jsx → PermissionDialogScreen.
 */
@Composable
fun PermissionDialog(
    objectName: String,
    objectOwner: String,
    requestSummary: String,
    amountSummary: String,
    onPay: () -> Unit,
    onDecline: () -> Unit,
    onMute: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = Linkpoint2.tokens
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        L2GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(tokens.radii.xl),
            contentPadding = PaddingValues(20.dp),
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(tokens.radii.md))
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.18f))
                            .padding(8.dp),
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Permission requested", fontWeight = FontWeight.SemiBold)
                        Text(requestSummary, style = MaterialTheme.typography.bodySmall, color = tokens.onSurfaceDim)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(tokens.radii.md))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(12.dp),
                ) {
                    Column {
                        Text(objectName, fontWeight = FontWeight.SemiBold)
                        Text("Owner: $objectOwner", style = MaterialTheme.typography.bodySmall, color = tokens.onSurfaceDim)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "This will: $amountSummary",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    L2GhostButton(text = "Decline", onClick = onDecline, modifier = Modifier.weight(1f))
                    L2GhostButton(text = "Mute", onClick = onMute, modifier = Modifier.weight(1f))
                    L2FilledButton(text = "Pay", onClick = onPay, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Cluster C — Minimap overlay (130dp circular badge over world).
 * See design/screens-extra.jsx → MinimapOverlayScreen.
 */
@Composable
fun MinimapOverlay(
    youCoord: String,
    friendsCount: Int,
    nearbyCount: Int,
    modifier: Modifier = Modifier,
) {
    val tokens = Linkpoint2.tokens
    L2GlassSurface(
        modifier = modifier.width(150.dp).height(160.dp),
        shape = RoundedCornerShape(tokens.radii.lg),
        contentPadding = PaddingValues(8.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(tokens.radii.md))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF112236), Color(0xFF1F4068)),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary),
                )
                Text(
                    text = "N",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp),
                )
            }
            Spacer(Modifier.height(4.dp))
            L2CoordText(text = youCoord)
            Text(
                text = "$friendsCount friends · $nearbyCount nearby",
                style = MaterialTheme.typography.labelSmall,
                color = tokens.onSurfaceDim,
            )
        }
    }
}

/**
 * Cluster C — Low bandwidth screen. See design/screens-states.jsx →
 * LowBandwidthScreen.
 */
@Composable
fun LowBandwidthBanner(
    regionProgress: Float,
    texturesProgress: Float,
    meshProgress: Float,
    avatarsProgress: Float,
    onPause: () -> Unit,
    onTextOnly: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = Linkpoint2.tokens
    L2GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentPadding = PaddingValues(16.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = tokens.warning)
                Spacer(Modifier.width(8.dp))
                Text("Low bandwidth", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(8.dp))
            ProgressLine("Region", regionProgress)
            ProgressLine("Textures", texturesProgress)
            ProgressLine("Mesh", meshProgress)
            ProgressLine("Avatars", avatarsProgress)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                L2GhostButton(text = "Pause downloads", onClick = onPause, modifier = Modifier.weight(1f))
                L2GhostButton(text = "Text only", onClick = onTextOnly, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ProgressLine(label: String, progress: Float) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.weight(1f))
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall)
        }
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
        )
    }
}
