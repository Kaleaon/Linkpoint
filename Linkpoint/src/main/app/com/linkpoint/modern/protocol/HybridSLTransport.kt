package com.linkpoint.modern.protocol

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.cancellation.CancellationException

/**
 * Unified transport façade that sits above HTTP/2 CAPS, WebSocket event queues,
 * and (future) UDP legacy circuits. The implementation deliberately focuses on
 * correctness and observability rather than raw networking – each transport
 * layer is represented by a lightweight adapter that can later be swapped for
 * a real client.
 */
class HybridSLTransport(
    private val capsClient: CapsClient = CapsClient(),
    private val eventClient: EventClient = EventClient(),
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val router = MessageRouter()
    private val connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    private val eventFlow = MutableSharedFlow<GridEvent>(extraBufferCapacity = 32)
    private val initMutex = Mutex()
    private var authToken: String? = null

    fun state(): StateFlow<ConnectionState> = connectionState.asStateFlow()
    fun events(): SharedFlow<GridEvent> = eventFlow.asSharedFlow()

    suspend fun initialize(eventQueueUrl: String?, seedCapability: String?) {
        initMutex.withLock {
            try {
                connectionState.value = ConnectionState.Initialising
                seedCapability?.let { capsClient.configureCapabilities(parseSeedCapability(it)) }
                eventQueueUrl?.let { eventClient.connect(it, authToken, ::dispatchEvent) }
                connectionState.value = ConnectionState.Ready
            } catch (error: Throwable) {
                if (error is CancellationException) throw error
                Log.e(TAG, "Failed to initialise hybrid transport", error)
                connectionState.value = ConnectionState.Error(error)
            }
        }
    }

    fun setAuthToken(token: String) {
        authToken = token
        capsClient.authToken = token
        eventClient.authToken = token
    }

    suspend fun send(message: ModernMessage): TransportResult {
        return when (val route = router.selectRoute(message)) {
            is TransportRoute.Caps -> capsClient.send(route.capability, message)
            TransportRoute.WebSocket -> eventClient.send(message)
            TransportRoute.NotSupported ->
                TransportResult.Failure(UnsupportedOperationException("UDP transport not available"))
        }
    }

    suspend fun uploadAsset(
        bytes: ByteArray,
        contentType: String,
        progress: suspend (Float) -> Unit
    ): TransportResult {
        return capsClient.upload(bytes, contentType, progress)
    }

    suspend fun shutdown() {
        initMutex.withLock {
            capsClient.shutdown()
            eventClient.shutdown()
            connectionState.value = ConnectionState.Disconnected
        }
    }

    fun subscribe(eventType: String, listener: suspend (GridEvent.Message) -> Unit) {
        eventClient.subscribe(eventType) { payload ->
            scope.launch { listener(payload) }
        }
    }

    private fun dispatchEvent(event: GridEvent) {
        scope.launch { eventFlow.emit(event) }
    }

    private fun parseSeedCapability(seed: String): Map<String, String> {
        val regex = Regex("<key>([^<]+)</key>\\s*<string>([^<]+)</string>", RegexOption.IGNORE_CASE)
        val map = mutableMapOf<String, String>()
        regex.findAll(seed).forEach { match ->
            val (key, value) = match.destructured
            map[key] = value
        }
        return map
    }

    sealed class ConnectionState {
        object Disconnected : ConnectionState()
        object Initialising : ConnectionState()
        object Ready : ConnectionState()
        data class Error(val throwable: Throwable) : ConnectionState()
    }

    sealed class TransportResult {
        data class Success(val response: TransportPayload) : TransportResult()
        data class Failure(val throwable: Throwable) : TransportResult()
    }

    data class TransportPayload(val type: String, val data: String)

    sealed class GridEvent {
        data class Message(val eventType: String, val payload: String) : GridEvent()
        data class Status(val description: String) : GridEvent()
    }

    abstract class ModernMessage(val type: String) {
        val id: UUID = UUID.randomUUID()
        val timestamp: Long = System.currentTimeMillis()

        abstract fun toLlsdXml(): String
        open fun toJson(): String = """{"type":"$type","id":"$id","timestamp":$timestamp}"""
    }

    private class MessageRouter {
        fun selectRoute(message: ModernMessage): TransportRoute {
            val name = message.type.lowercase()
            return when {
                "asset" in name || "upload" in name || "inventory" in name -> TransportRoute.Caps("UploadBakedTexture")
                "chat" in name || "event" in name || "object" in name -> TransportRoute.WebSocket
                else -> TransportRoute.Caps("GenericMessage")
            }
        }
    }

    private sealed class TransportRoute {
        data class Caps(val capability: String) : TransportRoute()
        object WebSocket : TransportRoute()
        object NotSupported : TransportRoute()
    }

    class CapsClient {
        private val capabilities = ConcurrentHashMap<String, String>()
        var authToken: String? = null

        suspend fun configureCapabilities(map: Map<String, String>) {
            capabilities.clear()
            capabilities.putAll(map)
        }

        suspend fun send(capability: String, message: ModernMessage): TransportResult {
            val url = capabilities[capability] ?: "https://caps.example.com/$capability"
            delay(SIMULATED_LATENCY_MS)
            val payload = TransportPayload(
                type = "caps_response",
                data = "Delivered ${message.type} via $url (token=${authToken?.take(6) ?: "none"})"
            )
            return TransportResult.Success(payload)
        }

        suspend fun upload(
            bytes: ByteArray,
            contentType: String,
            progress: suspend (Float) -> Unit
        ): TransportResult {
            val total = bytes.size.coerceAtLeast(1)
            val chunk = (total / 4).coerceAtLeast(1)
            var uploaded = 0
            repeat(4) {
                delay(SIMULATED_LATENCY_MS / 2)
                uploaded = (uploaded + chunk).coerceAtMost(total)
                progress(uploaded / total.toFloat())
            }
            val payload = TransportPayload(
                type = "upload_complete",
                data = "Stored ${bytes.size} bytes ($contentType)"
            )
            return TransportResult.Success(payload)
        }

        fun getCapability(name: String): String? = capabilities[name]
        fun shutdown() {
            capabilities.clear()
        }
    }

    class EventClient {
        private val listeners = ConcurrentHashMap<String, MutableList<suspend (GridEvent.Message) -> Unit>>()
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        private var heartbeatJob: kotlinx.coroutines.Job? = null
        var authToken: String? = null
            internal set

        suspend fun connect(url: String, token: String?, callback: (GridEvent) -> Unit) {
            delay(SIMULATED_LATENCY_MS)
            callback(GridEvent.Status("Connected to $url"))
            heartbeatJob?.cancel()
            heartbeatJob = scope.launch {
                while (true) {
                    delay(SIMULATED_EVENT_TICK_MS)
                    emit("heartbeat", """{"token":"${token ?: "none"}"}""", callback)
                }
            }
        }

        suspend fun send(message: ModernMessage): TransportResult {
            delay(SIMULATED_LATENCY_MS / 2)
            return TransportResult.Success(
                TransportPayload(
                    type = "websocket_ack",
                    data = "Delivered ${message.type} via WebSocket"
                )
            )
        }

        fun subscribe(eventType: String, listener: suspend (GridEvent.Message) -> Unit) {
            listeners.computeIfAbsent(eventType) { mutableListOf() }.add(listener)
        }

        fun shutdown() {
            heartbeatJob?.cancel()
            heartbeatJob = null
            listeners.clear()
        }

        private fun emit(eventType: String, payload: String, callback: (GridEvent) -> Unit) {
            val event = GridEvent.Message(eventType, payload)
            callback(event)
            listeners[eventType]?.forEach { handler ->
                scope.launch { handler(event) }
            }
        }
    }

    companion object {
        private const val TAG = "HybridSLTransport"
        private const val SIMULATED_LATENCY_MS = 150L
        private const val SIMULATED_EVENT_TICK_MS = 5_000L
    }
}
