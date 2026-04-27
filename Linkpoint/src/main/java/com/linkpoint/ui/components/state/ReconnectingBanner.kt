package com.linkpoint.ui.components.state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReconnectingBanner(
    message: String = "Connection unstable. Reconnecting...",
    modifier: Modifier = Modifier
) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onTertiary,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
