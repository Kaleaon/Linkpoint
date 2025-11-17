package com.linkpoint.modern.protocol

import android.util.Log
import com.linkpoint.slproto.auth.SLAuth
import com.linkpoint.slproto.auth.SLAuthParams
import com.linkpoint.slproto.auth.SLAuthReply
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lindenlab.llsd.LLSD
import lindenlab.llsd.LLSDException
import lindenlab.llsd.LLSDParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayInputStream
import java.io.IOException
import java.security.SecureRandom
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.xml.parsers.ParserConfigurationException

/**
 * Real transport layer that wires Linkpoint into the Second Life protocol stack.
 * Handles XML-RPC authentication, CAPS fetching and the EventQueue long poll.
 */
class HybridSLTransport(
    private val auth: SLAuth = SLAuth(),
    private val httpClient: OkHttpClient = defaultHttpClient(),
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val sessionRef = AtomicReference<SessionInfo?>()
    private val capabilities = ConcurrentHashMap<String, String>()
    private val connectionState = MutableStateFlow<State>(State.Idle)
    private val eventFlow = MutableSharedFlow<GridEvent>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private var eventJob = scope.launch { }
    private var eventAck: Long? = null
    private val parser: LLSDParser = try {
        LLSDParser()
    } catch (e: ParserConfigurationException) {
        throw IllegalStateException("Unable to create LLSD parser", e)
    }

    fun state(): StateFlow<State> = connectionState.asStateFlow()
    fun events(): SharedFlow<GridEvent> = eventFlow.asSharedFlow()
    fun currentSession(): SessionInfo? = sessionRef.get()

    suspend fun login(
        grid: Grid = Grid.SecondLifeMain,
        loginName: String,
        password: String,
        startLocation: String = "last"
    ): SessionInfo = withContext(scope.coroutineContext) {
        connectionState.value = State.Connecting
        val params = SLAuthParams(
            loginUri = grid.loginUrl,
            loginName = loginName,
            passwordHash = SLAuth.md5Hash(password),
            startLocation = startLocation,
            channel = "Linkpoint Android",
            version = "1.0.0",
            macAddress = randomMacAddress(),
            id0 = randomId0()
        )
        val reply = auth.sendLoginRequest(params)
        if (!reply.success) {
            val error = IllegalStateException(reply.message ?: "Login failed")
            connectionState.value = State.Error(error)
            throw error
        }

        val session = SessionInfo(grid, reply)
        sessionRef.set(session)
        connectionState.value = State.Connected(session)

        reply.seedCapability?.let { seed ->
            try {
                fetchCapabilities(seed)
                capabilities["EventQueueGet"]?.let { startEventQueue(it) }
            } catch (capError: Exception) {
                Log.w(TAG, "Capability bootstrap failed: ${capError.message}", capError)
                eventFlow.emit(GridEvent.Error(capError))
            }
        }

        session
    }

    suspend fun postCapability(
        name: String,
        body: LLSD,
        contentType: String = LLSD_MIME
    ): String {
        val url = capabilities[name]
            ?: throw IllegalStateException("Capability $name not available")
        val writer = java.io.StringWriter()
        body.serialise(writer)
        val request = Request.Builder()
            .url(url)
            .post(writer.toString().toRequestBody(contentType.toMediaType()))
            .addHeader("Accept", LLSD_MIME)
            .build()
        return withContext(scope.coroutineContext) {
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Capability request failed (${response.code})")
                }
                response.body?.string() ?: ""
            }
        }
    }

    suspend fun shutdown() {
        eventJob.cancel()
        eventJob.join()
        sessionRef.set(null)
        capabilities.clear()
        connectionState.value = State.Idle
    }

    private suspend fun fetchCapabilities(seedUrl: String) {
        val payload = buildCapabilityRequest(DEFAULT_CAPABILITIES)
        val request = Request.Builder()
            .url(seedUrl)
            .post(payload.toRequestBody(LLSD_MIME.toMediaType()))
            .addHeader("Accept", LLSD_MIME)
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Seed capability fetch failed (${response.code})")
            }
            response.body?.byteStream()?.use { stream ->
                val parsed = parser.parse(stream).content as? Map<*, *>
                    ?: throw LLSDException("Seed capability returned unexpected payload")
                capabilities.clear()
                parsed.entries.forEach { entry ->
                    val key = entry.key as? String ?: return@forEach
                    val value = entry.value as? String ?: return@forEach
                    capabilities[key] = value
                }
                eventFlow.emit(GridEvent.Capabilities(capabilities.toMap()))
            } ?: throw IOException("Seed capability empty response")
        }
    }

    private fun startEventQueue(eventQueueUrl: String) {
        eventJob.cancel()
        eventJob = scope.launch {
            eventAck = null
            while (true) {
                try {
                    val body = buildEventQueueRequest(eventAck)
                    val request = Request.Builder()
                        .url(eventQueueUrl)
                        .post(body.toRequestBody(LLSD_MIME.toMediaType()))
                        .addHeader("Accept", LLSD_MIME)
                        .build()
                    httpClient.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            throw IOException("Event queue HTTP ${response.code}")
                        }
                        response.body?.byteStream()?.use { stream ->
                            handleEventQueueResponse(stream)
                        } ?: throw IOException("Event queue empty response")
                    }
                } catch (error: Exception) {
                    eventFlow.emit(GridEvent.Error(error))
                    delay(EVENT_QUEUE_RETRY_MS)
                }
            }
        }
    }

    private suspend fun handleEventQueueResponse(stream: java.io.InputStream) {
        val parsed = parser.parse(stream).content as? Map<*, *> ?: return
        val events = parsed["events"] as? List<*>
        val ack = parsed["id"]
        if (ack is Number) {
            eventAck = ack.toLong()
        }
        if (!events.isNullOrEmpty()) {
            events.forEach { payload ->
                eventFlow.emit(GridEvent.EventQueue(payload))
            }
        } else {
            eventFlow.emit(GridEvent.Status("Event queue heartbeat"))
        }
    }

    private fun buildCapabilityRequest(names: List<String>): String = buildString {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        append("<llsd><array>")
        names.forEach { append("<string>${LLSD.encodeXML(it)}</string>") }
        append("</array></llsd>")
    }

    private fun buildEventQueueRequest(ack: Long?): String = buildString {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        append("<llsd><map>")
        if (ack != null) {
            append("<key>ack</key><integer>$ack</integer>")
        }
        append("<key>done</key><boolean>false</boolean>")
        append("</map></llsd>")
    }

    sealed class State {
        object Idle : State()
        object Connecting : State()
        data class Connected(val session: SessionInfo) : State()
        data class Error(val throwable: Throwable) : State()
    }

    data class SessionInfo(
        val grid: Grid,
        val reply: SLAuthReply
    ) {
        val avatarName: String = listOfNotNull(reply.firstName, reply.lastName)
            .joinToString(" ")
        val sessionId: String? = reply.sessionId
    }

    sealed class GridEvent {
        data class Capabilities(val urls: Map<String, String>) : GridEvent()
        data class EventQueue(val payload: Any?) : GridEvent()
        data class Status(val description: String) : GridEvent()
        data class Error(val throwable: Throwable) : GridEvent()
    }

    enum class Grid(val id: String, val displayName: String, val loginUrl: String) {
        SecondLifeMain("secondlife_agni", "Second Life (Agni)", "https://login.agni.lindenlab.com/cgi-bin/login.cgi"),
        SecondLifeBeta("secondlife_aditi", "Second Life (Aditi)", "https://login.aditi.lindenlab.com/cgi-bin/login.cgi"),
        OsGrid("osgrid", "OSGrid", "http://login.osgrid.org/");

        companion object {
            fun fromId(id: String?): Grid =
                values().firstOrNull { it.id == id } ?: SecondLifeMain
        }
    }

    companion object {
        private const val TAG = "HybridSLTransport"
        private const val LLSD_MIME = "application/llsd+xml"
        private const val EVENT_QUEUE_RETRY_MS = 5_000L
        private val DEFAULT_CAPABILITIES = listOf(
            "EventQueueGet",
            "ChatSessionRequest",
            "SendUserReport",
            "ViewerStats",
            "UploadBakedTexture",
            "FetchInventoryDescendents2",
            "FetchInventory2",
            "GetDisplayNames",
            "AgentState",
            "AgentPreferences",
            "UpdateAgentLanguage",
            "EnvironmentSettings",
            "ExtEnvironment"
        )

        private fun defaultHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        private fun randomMacAddress(): String {
            val random = SecureRandom()
            return (0 until 6).joinToString(":") { "%02x".format(random.nextInt(256)) }
        }

        private fun randomId0(): String = UUID.randomUUID().toString().replace("-", "")
    }
}
