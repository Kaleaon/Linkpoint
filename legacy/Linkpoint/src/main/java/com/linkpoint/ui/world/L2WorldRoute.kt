package com.linkpoint.ui.world

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linkpoint.LinkpointApp

/**
 * Compose-first WORLD destination.
 *
 * The 3D viewport is hosted by [WorldViewActivity] for now (the renderer is
 * tightly bound to the Activity lifecycle). This route auto-launches the
 * viewport when entered after a successful login and presents a clean
 * "resume" surface when the user backs out.
 */
@Composable
fun L2WorldRoute(
    onOpenChat: () -> Unit,
    onOpenMinimap: () -> Unit,
    onOpenInventory: () -> Unit,
    onOpenMap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val app = LinkpointApp.getInstanceOrNull()
    val isConnected = app?.sessionManager?.isConnected() == true
    var hasLaunched by remember { mutableStateOf(false) }

    LaunchedEffect(isConnected) {
        if (isConnected && !hasLaunched) {
            hasLaunched = true
            context.startActivity(Intent(context, WorldViewActivity::class.java))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = if (isConnected) "World session running" else "Not connected",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = if (isConnected) {
                    "Resume the 3D session or jump to another tab."
                } else {
                    "Sign in to enter the world."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (isConnected) {
                Button(onClick = {
                    context.startActivity(Intent(context, WorldViewActivity::class.java))
                }) {
                    Text("Resume world")
                }
            }
        }
    }
}
