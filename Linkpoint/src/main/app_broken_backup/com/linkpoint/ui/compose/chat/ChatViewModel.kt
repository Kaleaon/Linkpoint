package com.linkpoint.ui.compose.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linkpoint.database.entities.ChatMessageEntity
import com.linkpoint.repository.ChatRepository
import com.linkpoint.slproto.auth.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * ViewModel for Chat Screen
 * 
 * Handles:
 * - Loading messages
 * - Sending messages
 * - Real-time updates
 * - User name lookups
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    // Cache for chatter names to avoid repeated lookups
    private val chatterNameCache = mutableMapOf<Long, String>()

    private val _messages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val messages: StateFlow<List<ChatMessageEntity>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Get current user ID from session, fallback to 1L if no session
    private val _currentChatterId = MutableStateFlow<Long>(
        SessionManager.current()?.reply?.agentID?.toLongOrNull() ?: 1L
    )
    val currentChatterId: StateFlow<Long> = _currentChatterId.asStateFlow()

    init {
        loadMessages()
    }

    /**
     * Load messages for current chatter
     */
    private fun loadMessages() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Get recent messages
                val recentMessages = chatRepository.getRecentMessages(_currentChatterId.value, 50)
                _messages.value = recentMessages.reversed() // Show newest first
            } catch (e: Exception) {
                // Handle error
                android.util.Log.e("ChatViewModel", "Error loading messages", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Send a new message
     */
    fun sendMessage(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            try {
                val message = ChatMessageEntity(
                    id = 0, // Room will auto-generate
                    chatterID = _currentChatterId.value,
                    sessionID = UUID.randomUUID().toString(),
                    message = text.trim(),
                    timestamp = System.currentTimeMillis(),
                    fromLocal = true, // This is a message we're sending
                    type = "chat" // Regular chat message
                )

                val messageId = chatRepository.sendMessage(message)
                
                // Add to local list immediately for instant feedback
                _messages.value = _messages.value + message.copy(id = messageId)
                
            } catch (e: Exception) {
                android.util.Log.e("ChatViewModel", "Error sending message", e)
            }
        }
    }

    /**
     * Switch to a different chatter
     */
    fun switchChatter(chatterId: Long) {
        if (_currentChatterId.value != chatterId) {
            _currentChatterId.value = chatterId
            loadMessages()
        }
    }

    /**
     * Refresh messages
     */
    fun refresh() {
        loadMessages()
    }

    /**
     * Get display name for a chatter ID
     * Returns cached name if available, otherwise fetches from repository
     */
    fun getChatterName(chatterId: Long): String {
        // Return cached name if available
        chatterNameCache[chatterId]?.let { return it }

        // Fetch from repository asynchronously
        viewModelScope.launch {
            try {
                val chatter = chatRepository.getChatterById(chatterId)
                val name = chatter?.uuid?.toString()?.take(8) ?: "User $chatterId"
                chatterNameCache[chatterId] = name
            } catch (e: Exception) {
                android.util.Log.e("ChatViewModel", "Error fetching chatter name", e)
                chatterNameCache[chatterId] = "User $chatterId"
            }
        }

        // Return temporary name while fetching
        return "User $chatterId"
    }

    /**
     * Clear the chatter name cache
     */
    fun clearNameCache() {
        chatterNameCache.clear()
    }
}