package com.linkpoint.ui.components.state

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File

@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(sdk = [34])
class StateComponentsSnapshotTest(
    private val deviceName: String,
    private val qualifiers: String,
    private val themeName: String,
    private val isDark: Boolean
) {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        RuntimeEnvironment.setQualifiers(qualifiers)
    }

    @Test
    fun loadingState() = snapshot("loadingState") {
        LoadingState(message = "Loading nearby avatars…")
    }

    @Test
    fun errorState() = snapshot("errorState") {
        ErrorState(
            title = "Unable to load chat",
            message = "Your connection dropped. Try again.",
            retryLabel = "Retry",
            onRetry = {}
        )
    }

    @Test
    fun emptyState() = snapshot("emptyState") {
        EmptyState(
            title = "No friends yet",
            message = "Add friends to see them here.",
            actionLabel = "Refresh",
            onAction = {}
        )
    }

    @Test
    fun reconnectingBanner() = snapshot("reconnectingBanner") {
        ReconnectingBanner(message = "Reconnecting to simulator…")
    }

    @Test
    fun lowBandwidthOverlay() = snapshot("lowBandwidthOverlay") {
        Box(modifier = Modifier.fillMaxSize()) {
            LowBandwidthOverlay(
                message = "Packet loss is high. You may see delayed updates.",
                onRetry = {}
            )
        }
    }

    private fun snapshot(componentName: String, content: @Composable () -> Unit) {
        composeTestRule.setContent {
            MaterialTheme(
                colorScheme = if (isDark) darkColorScheme() else lightColorScheme()
            ) {
                content()
            }
        }

        val snapshotName = "${componentName}_${deviceName}_${themeName}"
        val outputDir = File("src/test/snapshots")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        val file = File(outputDir, "$snapshotName.png")

        composeTestRule.onRoot().captureRoboImage(
            filePath = file.path,
            roborazziOptions = RoborazziOptions(
                compareOptions = RoborazziOptions.CompareOptions(
                    changeThreshold = 0.005f
                )
            )
        )
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}_{2}")
        fun data(): Collection<Array<Any>> {
            val viewports = listOf(
                Triple("phone", "w360dp-h800dp-mdpi", "Phone (360x800dp)"),
                Triple("tablet", "w800dp-h1280dp-mdpi", "Tablet (800x1280dp)"),
                Triple("foldable", "w670dp-h840dp-mdpi", "Foldable (670x840dp)")
            )
            val themes = listOf(
                Pair("light", false),
                Pair("dark", true)
            )

            val params = mutableListOf<Array<Any>>()
            for ((deviceName, qualifiers, _) in viewports) {
                for ((themeName, isDark) in themes) {
                    params.add(arrayOf(deviceName, qualifiers, themeName, isDark))
                }
            }
            return params
        }
    }
}
