package com.lumiyaviewer.lumiya.modern.demos

import com.lumiyaviewer.lumiya.modern.auth.OAuth2AuthManager
import com.lumiyaviewer.lumiya.modern.avatar.ModernAvatarManager
import com.lumiyaviewer.lumiya.modern.chat.ModernChatManager
import com.lumiyaviewer.lumiya.modern.protocol.WebSocketEventClient
import com.lumiyaviewer.lumiya.modern.voice.WebRTCManager
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
