package com.linkpoint.ui.tos

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

/**
 * Compose-first TOS destination wired into the Linkpoint 2.0 nav graph.
 * Persists acceptance through [TosActivity.markAccepted] / declined.
 */
@Composable
fun L2TosRoute(
    onAccepted: () -> Unit,
    onDeclined: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    TosScreen(
        requireAcceptance = !TosActivity.hasAcceptedTos(context),
        onAccept = {
            TosActivity.recordTosAcceptance(context)
            onAccepted()
        },
        onDecline = onDeclined,
        modifier = modifier,
    )
}
