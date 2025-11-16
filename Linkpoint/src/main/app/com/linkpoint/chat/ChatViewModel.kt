package com.linkpoint.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    val messages: StateFlow<List<ChatMessage>> = repository.messages
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val connectionState: StateFlow<ConnectionState> = repository.connectionState
        .stateIn(viewModelScope, SharingStarted.Eagerly, ConnectionState.DISCONNECTED)

    private var startJob: Job? = null

    fun start() {
        if (startJob != null) return
        startJob = viewModelScope.launch {
            repository.start()
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            repository.sendMessage(message)
        }
    }

    fun reconnect() {
        repository.reconnectNow()
    }

    class Factory(
        private val repository: ChatRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                return ChatViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        }
    }
}
