package com.linkpoint.ui.slurl

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.linkpoint.LinkpointApp
import kotlinx.coroutines.launch

/**
 * Compose-first SLURL preview / teleport handoff. Replaces the XML
 * [SLURLActivity] when launched via the in-app navigation graph.
 */
@Composable
fun L2SlurlRoute(
    slurl: String,
    onDismiss: () -> Unit,
    onLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LinkpointApp.getInstanceOrNull()
    val parsed = remember(slurl) { parseSlurlString(slurl) }
    var info by remember(parsed) {
        mutableStateOf(
            parsed?.let {
                SLURLInfo(it.regionName, it.x.toInt(), it.y.toInt(), it.z.toInt())
            } ?: SLURLInfo("", 0, 0, 0, isValid = false, errorMessage = "Invalid SLURL")
        )
    }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val isLoggedIn = app?.isConnected() == true

    SLURLScreen(
        slurl = slurl,
        slurlInfo = info,
        isLoading = loading,
        isLoggedIn = isLoggedIn,
        onTeleport = {
            val target = parsed ?: return@SLURLScreen
            scope.launch {
                loading = true
                app?.protocol?.teleport(target.regionName, target.x, target.y, target.z)
                loading = false
                onDismiss()
            }
        },
        onShowOnMap = onDismiss,
        onLogin = onLogin,
        onDismiss = onDismiss,
        modifier = modifier,
    )
}

private data class ParsedSlurl(val regionName: String, val x: Float, val y: Float, val z: Float)

private fun parseSlurlString(raw: String): ParsedSlurl? {
    if (raw.isBlank()) return null
    val uri = runCatching { Uri.parse(raw) }.getOrNull() ?: return null
    return when (uri.scheme) {
        "secondlife" -> {
            val region = uri.host ?: return null
            val parts = uri.pathSegments
            ParsedSlurl(
                regionName = region,
                x = parts.getOrNull(0)?.toFloatOrNull() ?: 128f,
                y = parts.getOrNull(1)?.toFloatOrNull() ?: 128f,
                z = parts.getOrNull(2)?.toFloatOrNull() ?: 0f,
            )
        }
        "http", "https" -> {
            val parts = uri.pathSegments
            if (parts.size < 2 || parts[0] != "secondlife") return null
            ParsedSlurl(
                regionName = parts[1],
                x = parts.getOrNull(2)?.toFloatOrNull() ?: 128f,
                y = parts.getOrNull(3)?.toFloatOrNull() ?: 128f,
                z = parts.getOrNull(4)?.toFloatOrNull() ?: 0f,
            )
        }
        else -> null
    }
}
