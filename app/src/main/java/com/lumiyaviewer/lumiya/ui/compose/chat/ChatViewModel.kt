package com.lumiyaviewer.lumiya.ui.compose.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumiyaviewer.lumiya.database.entities.ChatMessageEntity
import com.lumiyaviewer.lumiya.repository.ChatRepository
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
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val messages: StateFlow<List<ChatMessageEntity>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentChatterId = MutableStateFlow<Long>(1L) // TODO: Get from user session
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
}