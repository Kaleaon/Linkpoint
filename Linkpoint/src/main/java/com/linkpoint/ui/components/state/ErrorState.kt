package com.linkpoint.ui.components.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ErrorState(
    message: String,
    onRetry: (() -> Unit)? = null,
    telemetryContext: String = "unknown",
    onTelemetry: (eventName: String, metadata: Map<String, String>) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    LaunchedEffect(message, telemetryContext) {
        onTelemetry(
            "error_state_shown",
            mapOf(
                "context" to telemetryContext,
                "message" to message
            )
        )
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (onRetry != null) {
            Button(
                onClick = {
                    onTelemetry(
                        "error_state_retry_tapped",
                        mapOf(
                            "context" to telemetryContext,
                            "message" to message
                        )
                    )
                    onRetry()
                }
            ) {
                Text("Retry")
            }
        }
    }
}
