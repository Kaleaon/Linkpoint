package com.linkpoint.chat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.random.Random

private const val MAX_MESSAGE_HISTORY = 200
private const val RECONNECT_DELAY_MS = 4_000L

class ChatRepository(
    private val chatClient: LinkpointChatClient,
    private val scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _username = MutableStateFlow<String?>(null)

    init {
        scope.launch {
            chatClient.events.collect { event ->
                when (event) {
                    is LinkpointChatClient.ChatEvent.Connected -> {
                        _connectionState.value = ConnectionState.CONNECTED
                        addSystemMessage("Connected to Linkpoint relay")
                    }
                    is LinkpointChatClient.ChatEvent.Disconnected -> {
                        _connectionState.value = ConnectionState.DISCONNECTED
                        addSystemMessage(
                            event.reason ?: "Connection closed, will retry shortly."
                        )
                        scheduleReconnect()
                    }
                    is LinkpointChatClient.ChatEvent.Error -> {
                        _connectionState.value = ConnectionState.DISCONNECTED
                        addSystemMessage("Error: ${event.throwable.message}")
                        scheduleReconnect()
                    }
                    is LinkpointChatClient.ChatEvent.RemoteMessage -> {
                        val payload = event.payload
                        appendMessage(
                            ChatMessage(
                                author = payload.author,
                                body = payload.message,
                                timestamp = payload.timestamp,
                                isLocal = payload.author.equals(_username.value, ignoreCase = true)
                            )
                        )
                    }
                }
            }
        }
    }

    fun start() {
        if (_username.value != null) return
        val generatedName = buildString {
            append("Explorer-")
            append(Random.nextInt(1000, 9999))
        }
        _username.value = generatedName
        addSystemMessage("Signing in as $generatedName")
        scope.launch { connectInternal() }
    }

    suspend fun sendMessage(rawBody: String) {
        val message = rawBody.trim()
        if (message.isEmpty()) return
        val author = _username.value ?: "You"
        appendMessage(
            ChatMessage(
                author = author,
                body = message,
                isLocal = true
            )
        )
        withContext(ioDispatcher) {
            chatClient.sendMessage(message)
        }
    }

    fun reconnectNow() {
        scope.launch {
            _connectionState.value = ConnectionState.CONNECTING
            connectInternal()
        }
    }

    private suspend fun connectInternal() {
        val user = _username.value ?: return
        _connectionState.value = ConnectionState.CONNECTING
        withContext(ioDispatcher) {
            chatClient.connect(user)
        }
    }

    private fun scheduleReconnect() {
        scope.launch {
            delay(RECONNECT_DELAY_MS)
            if (_connectionState.value != ConnectionState.CONNECTED) {
                connectInternal()
            }
        }
    }

    private fun appendMessage(message: ChatMessage) {
        _messages.update { current ->
            (current + message).takeLast(MAX_MESSAGE_HISTORY)
        }
    }

    private fun addSystemMessage(text: String) {
        val prettyText = text.trim().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
        appendMessage(
            ChatMessage(
                author = "System",
                body = prettyText,
                isLocal = false
            )
        )
    }
}
