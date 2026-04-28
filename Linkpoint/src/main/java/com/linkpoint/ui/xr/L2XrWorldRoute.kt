package com.linkpoint.ui.xr

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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

/**
 * Compose entry for the XR / VR viewport. Auto-launches the fullscreen
 * [XRWorldActivity] on first composition and provides a clean re-launch
 * surface afterwards.
 */
@Composable
fun L2XrWorldRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var hasLaunched by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!hasLaunched) {
            hasLaunched = true
            context.startActivity(Intent(context, XRWorldActivity::class.java))
        }
    }

    Box(
        modifier = modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Default.ViewInAr, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text("XR session", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Text(
                "Headset session is launching. Close the headset window to return.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = {
                context.startActivity(Intent(context, XRWorldActivity::class.java))
            }) { Text("Resume XR") }
            OutlinedButton(onClick = onBack) { Text("Back") }
        }
    }
}
