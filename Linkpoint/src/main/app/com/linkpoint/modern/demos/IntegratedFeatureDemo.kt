package com.linkpoint.modern.demos

import com.linkpoint.modern.auth.OAuth2AuthManager
import com.linkpoint.modern.avatar.ModernAvatarManager
import com.linkpoint.modern.chat.ModernChatManager
import com.linkpoint.modern.protocol.WebSocketEventClient
import com.linkpoint.modern.voice.WebRTCManager
import java.util.UUID

data class IntegratedFeatureDemo(
    var authManager: OAuth2AuthManager,
    var voiceManager: WebRTCManager,
    var chatManager: ModernChatManager,
    var avatarManager: ModernAvatarManager,
    var eventClient: WebSocketEventClient,
    var isAuthenticated: Boolean = false,
    var voiceEnabled: Boolean = false,
    var chatActive: Boolean = false,
    var currentAvatarId: UUID,
)
