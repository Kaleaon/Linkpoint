package com.linkpoint.ui.components.state

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.linkpoint.ui.components.state.EmptyState
import com.linkpoint.ui.components.state.ErrorState
import com.linkpoint.ui.components.state.LoadingState
import com.linkpoint.ui.components.state.LowBandwidthOverlay
import com.linkpoint.ui.components.state.ReconnectingBanner
import org.junit.Rule
import org.junit.Test

class StateComponentsSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test
    fun loadingState_light() = snapshot("loading_light", dark = false) {
        LoadingState(message = "Loading nearby avatars…")
    }

    @Test
    fun loadingState_dark() = snapshot("loading_dark", dark = true) {
        LoadingState(message = "Loading nearby avatars…")
    }

    @Test
    fun errorState_light() = snapshot("error_light", dark = false) {
        ErrorState(
            title = "Unable to load chat",
            message = "Your connection dropped. Try again.",
            retryLabel = "Retry",
            onRetry = {}
        )
    }

    @Test
    fun errorState_dark() = snapshot("error_dark", dark = true) {
        ErrorState(
            title = "Unable to load chat",
            message = "Your connection dropped. Try again.",
            retryLabel = "Retry",
            onRetry = {}
        )
    }

    @Test
    fun emptyState_light() = snapshot("empty_light", dark = false) {
        EmptyState(
            title = "No friends yet",
            message = "Add friends to see them here.",
            actionLabel = "Refresh",
            onAction = {}
        )
    }

    @Test
    fun emptyState_dark() = snapshot("empty_dark", dark = true) {
        EmptyState(
            title = "No friends yet",
            message = "Add friends to see them here.",
            actionLabel = "Refresh",
            onAction = {}
        )
    }

    @Test
    fun reconnectingBanner_light() = snapshot("reconnecting_banner_light", dark = false) {
        ReconnectingBanner(message = "Reconnecting to simulator…")
    }

    @Test
    fun reconnectingBanner_dark() = snapshot("reconnecting_banner_dark", dark = true) {
        ReconnectingBanner(message = "Reconnecting to simulator…")
    }

    @Test
    fun lowBandwidthOverlay_light() = snapshot("low_bandwidth_overlay_light", dark = false) {
        Box(modifier = Modifier.fillMaxSize()) {
            LowBandwidthOverlay(message = "Packet loss is high. You may see delayed updates.", onRetry = {})
        }
    }

    @Test
    fun lowBandwidthOverlay_dark() = snapshot("low_bandwidth_overlay_dark", dark = true) {
        Box(modifier = Modifier.fillMaxSize()) {
            LowBandwidthOverlay(message = "Packet loss is high. You may see delayed updates.", onRetry = {})
        }
    }

    private fun snapshot(name: String, dark: Boolean, content: @Composable () -> Unit) {
        paparazzi.snapshot(name = name) {
            MaterialTheme(
                colorScheme = if (dark) darkColorScheme() else lightColorScheme()
            ) {
                content()
            }
        }
    }
}
