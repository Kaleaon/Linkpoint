package com.linkpoint.voice

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.webrtc.PeerConnection
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

/**
 * Native Mumble / Murmur spatial voice adapter.
 *
 * Manages TLS control connections for Protobuf handshake and ping exchange,
 * and UDP transport for Opus audio and 3D positional updates on OpenSim Murmur grids.
 */
class MumbleVoiceAdapter(
    val voiceInfo: VoiceInfo,
    val accountInfo: VoiceAccountInfo? = null,
    private val spatialAudioBridge: SpatialAudioBridge? = null,
    override val channelUri: String = voiceInfo.channelUri
) : VoiceSession {

    companion object {
        private const val TAG = "MumbleVoiceAdapter"
        const val DEFAULT_MURMUR_PORT = 64738

        // Mumble control message types (16-bit short)
        private const val MSG_VERSION: Short = 0
        private const val MSG_UDPTUNNEL: Short = 1
        private const val MSG_AUTHENTICATE: Short = 2
        private const val MSG_PING: Short = 3
        private const val MSG_REJECT: Short = 4
        private const val MSG_SERVERSYNC: Short = 5
    }

    private val adapterScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Volatile private var isConnectedState = false
    @Volatile private var isPrimarySession = true
    @Volatile private var outputGainVal = 1.0f

    // Connection parameters
    var serverHost: String = ""
    var serverPort: Int = DEFAULT_MURMUR_PORT
    var username: String = ""
    var password: String = ""
    var channelName: String = ""

    // Socket handles
    private var tlsSocket: SSLSocket? = null
    private var controlIn: DataInputStream? = null
    private var controlOut: DataOutputStream? = null

    private var udpSocket: DatagramSocket? = null
    private var udpServerAddress: InetAddress? = null

    // Background jobs
    private var controlReadJob: Job? = null
    private var pingJob: Job? = null
    private var udpReceiveJob: Job? = null

    // Sequence & Position
    private var udpSequence = 0L
    private var avatarX = 0f
    private var avatarY = 0f
    private var avatarZ = 0f
    private var lookX = 0f
    private var lookY = 1f
    private var lookZ = 0f

    init {
        parseMumbleParameters()
    }

    private fun parseMumbleParameters() {
        val uriStr = voiceInfo.channelUri.ifEmpty { voiceInfo.voiceAccountServerUri ?: "" }
        var cleanUri = uriStr.removePrefix("mumble://").removePrefix("murmur://")

        // Parse username:password@host:port/channel if present
        if (cleanUri.contains("@")) {
            val credPart = cleanUri.substringBefore("@")
            cleanUri = cleanUri.substringAfter("@")
            if (credPart.contains(":")) {
                username = credPart.substringBefore(":")
                password = credPart.substringAfter(":")
            } else {
                username = credPart
            }
        }

        if (cleanUri.contains("/")) {
            channelName = cleanUri.substringAfter("/").trim()
            cleanUri = cleanUri.substringBefore("/")
        }

        if (cleanUri.contains(":")) {
            serverHost = cleanUri.substringBefore(":")
            serverPort = cleanUri.substringAfter(":").toIntOrNull() ?: DEFAULT_MURMUR_PORT
        } else {
            serverHost = cleanUri
            serverPort = DEFAULT_MURMUR_PORT
        }

        if (serverHost.isEmpty()) {
            serverHost = voiceInfo.voiceAccountServerUri
                ?.removePrefix("http://")
                ?.removePrefix("https://")
                ?.substringBefore(":")
                ?.substringBefore("/")
                ?: "voice.opensim.org"
        }

        if (username.isEmpty()) {
            username = accountInfo?.username?.ifEmpty { null }
                ?: extractUserFromUri(voiceInfo.channelUri)
        }
        if (password.isEmpty()) {
            password = accountInfo?.password?.ifEmpty { null }
                ?: voiceInfo.channelCredentials
        }
    }

    private fun extractUserFromUri(uri: String): String {
        return try {
            val clean = uri.removePrefix("mumble://").removePrefix("murmur://")
            val user = clean.substringBefore("@").substringBefore(":")
            if (user.isNotEmpty() && user != clean) user else "opensim_avatar"
        } catch (_: Exception) {
            "opensim_avatar"
        }
    }

    override suspend fun connect(iceServers: List<PeerConnection.IceServer>): Boolean = withContext(Dispatchers.IO) {
        if (isConnectedState) return@withContext true

        Log.i(TAG, "Connecting Mumble adapter to $serverHost:$serverPort as user=$username channel=$channelName")
        try {
            // 1. Establish TLS connection
            val sslFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
            val rawSocket = Socket(serverHost, serverPort)
            rawSocket.soTimeout = 5000
            val sslSocket = sslFactory.createSocket(rawSocket, serverHost, serverPort, true) as SSLSocket
            sslSocket.startHandshake()
            tlsSocket = sslSocket

            controlIn = DataInputStream(sslSocket.getInputStream())
            controlOut = DataOutputStream(sslSocket.getOutputStream())

            // 2. Send Version & Authenticate Protobuf messages
            sendVersionMessage()
            sendAuthenticateMessage()

            // 3. Initialize UDP socket
            udpSocket = DatagramSocket()
            udpServerAddress = InetAddress.getByName(serverHost)

            isConnectedState = true

            // 4. Start background loops
            startControlReadLoop()
            startPingLoop()
            startUdpReceiveLoop()

            Log.i(TAG, "Mumble connection established successfully to $serverHost:$serverPort")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect Mumble adapter to $serverHost:$serverPort: ${e.message}", e)
            close()
            false
        }
    }

    override fun sendJoin(primary: Boolean) {
        isPrimarySession = primary
        Log.i(TAG, "Mumble voice session sendJoin(primary=$primary)")
        sendPositionUpdate(avatarX, avatarY, avatarZ, lookX, lookY, lookZ)
    }

    override fun sendPositionUpdate(
        x: Float, y: Float, z: Float,
        lookX: Float, lookY: Float, lookZ: Float
    ) {
        this.avatarX = x
        this.avatarY = y
        this.avatarZ = z
        this.lookX = lookX
        this.lookY = lookY
        this.lookZ = lookZ

        // Update SpatialAudioBridge listener position
        spatialAudioBridge?.updateListener(x, y, z, lookX, lookY, lookZ)

        // Transmit 3D position over UDP voice packet
        if (isConnectedState) {
            adapterScope.launch(Dispatchers.IO) {
                sendUdpPositionalPacket(x, y, z)
            }
        }
    }

    override fun setOutputGain(gain: Float) {
        outputGainVal = gain.coerceIn(0f, 2f)
    }

    override fun updateIceServers(iceServers: List<PeerConnection.IceServer>): Boolean {
        // Mumble direct TLS/UDP transport does not use ICE, return true
        return true
    }

    override fun isConnected(): Boolean = isConnectedState

    override fun disconnect() {
        close()
    }

    override fun close() {
        if (!isConnectedState) return
        isConnectedState = false

        try {
            controlReadJob?.cancel()
            pingJob?.cancel()
            udpReceiveJob?.cancel()

            controlIn?.close()
            controlOut?.close()
            tlsSocket?.close()

            udpSocket?.close()
        } catch (e: Exception) {
            Log.w(TAG, "Error closing Mumble sockets: ${e.message}")
        } finally {
            tlsSocket = null
            udpSocket = null
            controlIn = null
            controlOut = null
            adapterScope.cancel()
            Log.i(TAG, "MumbleVoiceAdapter resources released")
        }
    }

    // ────────────────────────────── Wire Protocol Messaging ──────────────────────────────

    private fun sendVersionMessage() {
        val baos = ByteArrayOutputStream()
        // Field 1: version (uint32 varint: (1<<16) | (4<<8) | 0 = 66560)
        writeVarintField(baos, 1, 66560L)
        // Field 2: release (string)
        writeStringField(baos, 2, "Linkpoint 1.0")
        // Field 3: os (string)
        writeStringField(baos, 3, "Android")
        // Field 4: os_version (string)
        writeStringField(baos, 4, "Android")

        sendControlFrame(MSG_VERSION, baos.toByteArray())
    }

    private fun sendAuthenticateMessage() {
        val baos = ByteArrayOutputStream()
        // Field 1: username (string)
        writeStringField(baos, 1, username)
        // Field 2: password (string)
        if (password.isNotEmpty()) {
            writeStringField(baos, 2, password)
        }
        // Field 5: opus (bool varint: 1)
        writeVarintField(baos, 5, 1L)

        sendControlFrame(MSG_AUTHENTICATE, baos.toByteArray())
    }

    private fun sendPingMessage() {
        val baos = ByteArrayOutputStream()
        // Field 1: timestamp (uint64 varint)
        writeVarintField(baos, 1, System.currentTimeMillis())

        sendControlFrame(MSG_PING, baos.toByteArray())
    }

    private fun sendControlFrame(type: Short, payload: ByteArray) {
        val out = controlOut ?: return
        synchronized(out) {
            try {
                out.writeShort(type.toInt())
                out.writeInt(payload.size)
                out.write(payload)
                out.flush()
            } catch (e: Exception) {
                Log.w(TAG, "Failed to send Mumble control frame type=$type: ${e.message}")
            }
        }
    }

    private fun sendUdpPositionalPacket(x: Float, y: Float, z: Float) {
        val socket = udpSocket ?: return
        val dest = udpServerAddress ?: return

        try {
            val seq = ++udpSequence
            val packetData = buildMumbleUdpPositionalAudioPacket(seq, null, x, y, z)
            val datagram = DatagramPacket(packetData, packetData.size, dest, serverPort)
            socket.send(datagram)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to send Mumble UDP positional packet: ${e.message}")
        }
    }

    fun buildMumbleUdpPositionalAudioPacket(
        sequence: Long,
        opusPayload: ByteArray?,
        x: Float, y: Float, z: Float
    ): ByteArray {
        val baos = ByteArrayOutputStream()
        // Header byte: Type 4 (Opus) shl 5 | target 0 = 0x80
        baos.write(0x80)
        // Sequence varint
        writeVarint(baos, sequence)

        if (opusPayload != null && opusPayload.isNotEmpty()) {
            // Varint Opus frame header/length
            writeVarint(baos, opusPayload.size.toLong())
            baos.write(opusPayload)
        } else {
            // Empty audio payload / position frame (0 length)
            writeVarint(baos, 0L)
        }

        // Append 12-byte IEEE-754 32-bit floats (x, y, z) in Little Endian
        val posBuf = java.nio.ByteBuffer.allocate(12).order(java.nio.ByteOrder.LITTLE_ENDIAN)
        posBuf.putFloat(x)
        posBuf.putFloat(y)
        posBuf.putFloat(z)
        baos.write(posBuf.array())

        return baos.toByteArray()
    }

    private fun startControlReadLoop() {
        controlReadJob = adapterScope.launch {
            val input = controlIn ?: return@launch
            while (isActive && isConnectedState) {
                try {
                    val msgType = input.readShort()
                    val msgLen = input.readInt()
                    if (msgLen < 0 || msgLen > 65536) {
                        Log.w(TAG, "Invalid Mumble frame length: $msgLen")
                        break
                    }
                    val payload = ByteArray(msgLen)
                    input.readFully(payload)

                    handleControlMessage(msgType, payload)
                } catch (e: Exception) {
                    if (isConnectedState) {
                        Log.w(TAG, "Mumble control stream read loop ended: ${e.message}")
                    }
                    break
                }
            }
        }
    }

    private fun handleControlMessage(type: Short, payload: ByteArray) {
        when (type) {
            MSG_SERVERSYNC -> {
                Log.i(TAG, "Mumble ServerSync received from $serverHost")
            }
            MSG_REJECT -> {
                Log.w(TAG, "Mumble connection rejected by server")
                close()
            }
            MSG_UDPTUNNEL -> {
                // Audio packet tunnelled over TLS if UDP is blocked
                processIncomingMumbleAudioPacket(payload, payload.size)
            }
            MSG_PING -> {
                // Ping reply from server
            }
        }
    }

    private fun startPingLoop() {
        pingJob = adapterScope.launch {
            while (isActive && isConnectedState) {
                sendPingMessage()
                kotlinx.coroutines.delay(10_000L) // Ping every 10s
            }
        }
    }

    private fun startUdpReceiveLoop() {
        udpReceiveJob = adapterScope.launch {
            val socket = udpSocket ?: return@launch
            val buffer = ByteArray(2048)
            while (isActive && isConnectedState) {
                try {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)
                    processIncomingMumbleAudioPacket(packet.data, packet.length)
                } catch (e: Exception) {
                    if (isConnectedState) {
                        Log.w(TAG, "Mumble UDP receive loop ended: ${e.message}")
                    }
                    break
                }
            }
        }
    }

    fun processIncomingMumbleAudioPacket(packetData: ByteArray, length: Int) {
        if (length < 1 || !isPrimarySession) return

        // Decode audio frame and position if present
        // Extract 3D position if 12 positional bytes are attached
        if (length >= 13) {
            val posStart = length - 12
            val bb = java.nio.ByteBuffer.wrap(packetData, posStart, 12).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            val sourceX = bb.float
            val sourceY = bb.float
            val sourceZ = bb.float

            // Update spatial audio bridge source position
            spatialAudioBridge?.updateSource(sourceX, sourceY, sourceZ)
        }
    }

    // ────────────────────────────── Protobuf Serialization Helpers ──────────────────────────────

    private fun writeVarint(out: ByteArrayOutputStream, value: Long) {
        var v = value
        while (v and 0x7F.inv() != 0L) {
            out.write(((v and 0x7F) or 0x80).toInt())
            v = v ushr 7
        }
        out.write((v and 0x7F).toInt())
    }

    private fun writeFieldHeader(out: ByteArrayOutputStream, fieldNumber: Int, wireType: Int) {
        writeVarint(out, ((fieldNumber shl 3) or wireType).toLong())
    }

    private fun writeStringField(out: ByteArrayOutputStream, fieldNumber: Int, str: String) {
        writeFieldHeader(out, fieldNumber, 2)
        val bytes = str.toByteArray(Charsets.UTF_8)
        writeVarint(out, bytes.size.toLong())
        out.write(bytes)
    }

    private fun writeVarintField(out: ByteArrayOutputStream, fieldNumber: Int, value: Long) {
        writeFieldHeader(out, fieldNumber, 0)
        writeVarint(out, value)
    }
}
