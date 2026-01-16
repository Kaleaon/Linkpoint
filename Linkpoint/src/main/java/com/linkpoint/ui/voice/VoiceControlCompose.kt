package com.linkpoint.ui.voice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.linkpoint.voice.VoiceManager
import kotlinx.coroutines.flow.StateFlow

/**
 * Compose version of the VoiceControlView.
 * 
 * Voice control widget for toggling voice chat.
 * Displays connection status, toggle button, and mute controls.
 * 
 * @param isConnected StateFlow of voice connection status
 * @param isMuted StateFlow of mute status
 * @param onVoiceToggle Callback when voice toggle is clicked (receives desired connection state).
 *                      The callback is responsible for handling async operations internally.
 * @param onMuteToggle Callback when mute toggle is clicked
 * @param connectedColor Color when voice is connected
 * @param disconnectedColor Color when voice is disconnected
 * @param modifier Modifier for the control
 */
@Composable
fun VoiceControl(
    isConnected: StateFlow<Boolean>,
    isMuted: StateFlow<Boolean>,
    onVoiceToggle: (Boolean) -> Unit,
    onMuteToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    connectedColor: Color = Color(0xFF4CAF50),  // Green
    disconnectedColor: Color = Color(0xFF757575)  // Gray
) {
    val connected by isConnected.collectAsState()
    val muted by isMuted.collectAsState()
    
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Voice toggle button
            IconButton(
                onClick = { onVoiceToggle(!connected) }
            ) {
                Icon(
                    imageVector = if (connected) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                    contentDescription = if (connected) "Disconnect voice" else "Connect voice",
                    tint = if (connected) connectedColor else disconnectedColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            // Status text
            Column {
                Text(
                    text = when {
                        connected && muted -> "Voice (Muted)"
                        connected -> "Voice On"
                        else -> "Voice Off"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (connected) connectedColor else disconnectedColor
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Mute button (only visible when connected)
            if (connected) {
                IconButton(
                    onClick = { onMuteToggle(!muted) }
                ) {
                    Icon(
                        imageVector = if (muted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = if (muted) "Unmute" else "Mute",
                        tint = if (muted) disconnectedColor else connectedColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * Simplified VoiceControl that takes a VoiceManager directly.
 * 
 * This is a convenience composable that wraps VoiceControl and connects
 * it to the VoiceManager's state flows.
 */
@Composable
fun VoiceControl(
    voiceManager: VoiceManager,
    modifier: Modifier = Modifier,
    connectedColor: Color = Color(0xFF4CAF50),
    disconnectedColor: Color = Color(0xFF757575)
) {
    VoiceControl(
        isConnected = voiceManager.isConnected,
        isMuted = voiceManager.isMuted,
        onVoiceToggle = { shouldConnect ->
            if (shouldConnect) {
                voiceManager.joinParcelVoice()
            } else {
                voiceManager.leaveVoice()
            }
        },
        onMuteToggle = { shouldMute ->
            voiceManager.setMuted(shouldMute)
        },
        modifier = modifier,
        connectedColor = connectedColor,
        disconnectedColor = disconnectedColor
    )
}

/**
 * Compact voice indicator for use in toolbars or small spaces.
 * Shows just an icon with connection/mute status.
 */
@Composable
fun VoiceIndicator(
    isConnected: Boolean,
    isMuted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    connectedColor: Color = Color(0xFF4CAF50),
    disconnectedColor: Color = Color(0xFF757575)
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = when {
                !isConnected -> Icons.Default.VolumeOff
                isMuted -> Icons.Default.MicOff
                else -> Icons.Default.Mic
            },
            contentDescription = when {
                !isConnected -> "Voice disconnected"
                isMuted -> "Voice muted"
                else -> "Voice active"
            },
            tint = when {
                !isConnected -> disconnectedColor
                isMuted -> Color(0xFFFFC107)  // Yellow/warning
                else -> connectedColor
            }
        )
    }
}
