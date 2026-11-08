package com.linkpoint.ui.tests

import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.linkpoint.ui.chat.ChatChannel
import com.linkpoint.ui.chat.ChatMessage
import com.linkpoint.ui.chat.ChatScreen
import com.linkpoint.ui.chat.MessageType
import com.linkpoint.ui.friends.FriendData
import com.linkpoint.ui.friends.FriendStatus
import com.linkpoint.ui.friends.FriendsScreen
import com.linkpoint.ui.login.GridDisplayInfo
import com.linkpoint.ui.login.LoginScreen
import com.linkpoint.ui.settings.SettingsScreen
import com.linkpoint.ui.settings.SettingsState
import com.linkpoint.ui.teleport.TeleportHistoryEntry
import com.linkpoint.ui.teleport.TeleportHistoryScreen
import com.linkpoint.ui.theme.BuiltInThemes
import com.linkpoint.ui.theme.LinkpointTheme
import java.security.MessageDigest
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ThemeScreenshotGoldenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun representative_screens_have_distinct_snapshots_across_three_theme_families() {
        val families = mapOf(
            "linkpoint" to BuiltInThemes.LINKPOINT_DEFAULT,
            "viewer" to BuiltInThemes.SL_CLASSIC,
            "cleverferret" to BuiltInThemes.CLEVERFERRET_GOLD
        )

        val screenHashes = linkedMapOf<String, MutableSet<String>>()

        families.forEach { (familyName, theme) ->
            captureScreenHash("login", familyName, theme) {
                LoginScreen(
                    grids = listOf(GridDisplayInfo("sl", "Second Life")),
                    onLogin = {},
                    onOpenSettings = {}
                )
            }.also { addHash(screenHashes, "login", it) }

            captureScreenHash("chat", familyName, theme) {
                ChatScreen(
                    messages = listOf(
                        ChatMessage(
                            id = "1",
                            sender = "Guide",
                            content = "Welcome to Linkpoint",
                            timestamp = 1000L,
                            type = MessageType.NORMAL,
                            channel = ChatChannel.LOCAL
                        )
                    ),
                    onRetry = {},
                    onSendMessage = { _, _ -> },
                    onNavigateBack = {}
                )
            }.also { addHash(screenHashes, "chat", it) }

            captureScreenHash("friends", familyName, theme) {
                FriendsScreen(
                    friends = listOf(
                        FriendData(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Alex", FriendStatus.ONLINE, "Sandbox")
                    ),
                    onRetry = {},
                    onNavigateBack = {},
                    onOpenIM = {},
                    onTeleportTo = {},
                    onViewProfile = {},
                    onRemoveFriend = {},
                    onAddFriend = {}
                )
            }.also { addHash(screenHashes, "friends", it) }

            captureScreenHash("settings", familyName, theme) {
                SettingsScreen(
                    settings = SettingsState(),
                    onSettingsChange = {},
                    onNavigateBack = {},
                    onOpenThemePicker = {},
                    onOpenAccount = {},
                    onOpenPrivacy = {},
                    onOpenAbout = {},
                    onOpenLayoutEditor = {}
                )
            }.also { addHash(screenHashes, "settings", it) }

            captureScreenHash("teleport_history", familyName, theme) {
                TeleportHistoryScreen(
                    history = listOf(
                        TeleportHistoryEntry("1", "Morris", "128,128,25", 1000L, "secondlife://Morris/128/128/25")
                    ),
                    onTeleportTo = {},
                    onDeleteEntry = {},
                    onClearHistory = {},
                    onNavigateBack = {}
                )
            }.also { addHash(screenHashes, "teleport_history", it) }
        }

        assertEquals("Expected 5 representative screens", 5, screenHashes.size)
        screenHashes.forEach { (screen, hashes) ->
            assertTrue(
                "Expected themed visual variation for $screen across 3 families",
                hashes.size >= 3
            )
        }
    }

    private fun captureScreenHash(
        screenName: String,
        familyName: String,
        theme: com.linkpoint.ui.theme.ThemePack,
        content: @androidx.compose.runtime.Composable () -> Unit
    ): String {
        val tag = "golden_${screenName}_$familyName"
        composeRule.setContent {
            LinkpointTheme(themePack = theme) {
                androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.testTag(tag)) {
                    content()
                }
            }
        }

        val image = composeRule.onNodeWithTag(tag).captureToImage()
        val pixelMap = image.toPixelMap()
        val bytes = ByteArray(image.width * image.height * 4)
        var index = 0
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val argb = pixelMap[x, y].toArgb()
                bytes[index++] = ((argb shr 24) and 0xFF).toByte()
                bytes[index++] = ((argb shr 16) and 0xFF).toByte()
                bytes[index++] = ((argb shr 8) and 0xFF).toByte()
                bytes[index++] = (argb and 0xFF).toByte()
            }
        }

        return MessageDigest.getInstance("SHA-256")
            .digest(bytes)
            .joinToString("") { "%02x".format(it) }
    }

    private fun addHash(
        screenHashes: MutableMap<String, MutableSet<String>>,
        screen: String,
        hash: String
    ) {
        screenHashes.getOrPut(screen) { linkedSetOf() }.add(hash)
    }
}
