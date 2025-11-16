package com.linkpoint.chat

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject

/**
 * Thin OkHttp based WebSocket client that speaks a tiny JSON protocol.
 *
 * The public API intentionally exposes a SharedFlow of events so repositories
 * can react/retry without leaking OkHttp specific details into the UI layer.
 */
class LinkpointChatClient(
    private val okHttpClient: OkHttpClient,
    private val endpointUrl: String = DEFAULT_ENDPOINT
) {

    private var username: String = ""
    private var webSocket: WebSocket? = null

    private val _events = MutableSharedFlow<ChatEvent>(
        extraBufferCapacity = 32,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<ChatEvent> = _events

    fun connect(user: String) {
        if (user.isBlank()) {
            throw IllegalArgumentException("Username must not be blank")
        }
        username = user
        webSocket?.close(NORMAL_CLOSURE, "reconnecting")
        val request = Request.Builder()
            .url(endpointUrl)
            .build()
        webSocket = okHttpClient.newWebSocket(request, LinkpointWebSocketListener())
    }

    fun disconnect() {
        webSocket?.close(NORMAL_CLOSURE, "shutdown")
        webSocket = null
    }

    fun sendMessage(body: String) {
        val socket = webSocket ?: throw IllegalStateException("WebSocket not connected")
        val payload = JSONObject()
            .put("author", username)
            .put("message", body)
            .put("timestamp", System.currentTimeMillis())
        socket.send(payload.toString())
    }

    private inner class LinkpointWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            _events.tryEmit(ChatEvent.Connected)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            parseMessage(text)?.let {
                _events.tryEmit(ChatEvent.RemoteMessage(it))
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            // Some echo services send small pings as binary payloads
            parseMessage(bytes.utf8())?.let {
                _events.tryEmit(ChatEvent.RemoteMessage(it))
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            _events.tryEmit(ChatEvent.Disconnected(reason.ifBlank { null }))
            webSocket.close(code, reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            _events.tryEmit(ChatEvent.Error(t))
            _events.tryEmit(ChatEvent.Disconnected(response?.message))
        }

        private fun parseMessage(text: String): ChatPayload? {
            return try {
                val json = JSONObject(text)
                val author = json.optString("author", "Remote")
                val message = json.optString("message", text)
                val timestamp = json.optLong("timestamp", System.currentTimeMillis())
                ChatPayload(author, message, timestamp)
            } catch (_: Exception) {
                if (text.isBlank()) {
                    null
                } else {
                    ChatPayload("System", text, System.currentTimeMillis())
                }
            }
        }
    }

    sealed interface ChatEvent {
        data object Connected : ChatEvent
        data class Disconnected(val reason: String?) : ChatEvent
        data class Error(val throwable: Throwable) : ChatEvent
        data class RemoteMessage(val payload: ChatPayload) : ChatEvent
    }

    data class ChatPayload(
        val author: String,
        val message: String,
        val timestamp: Long
    )

    companion object {
        private const val DEFAULT_ENDPOINT = "wss://echo.websocket.events"
        private const val NORMAL_CLOSURE = 1000
    }
}
