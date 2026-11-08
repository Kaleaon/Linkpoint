package com.linkpoint.voice

import org.webrtc.PeerConnection

/**
 * Unified voice session abstraction interface implemented by WebRtcVoiceSession,
 * MumbleVoiceAdapter, and internal voice sessions.
 */
interface VoiceSession {
    val channelUri: String

    suspend fun connect(iceServers: List<PeerConnection.IceServer> = emptyList()): Boolean
    fun sendJoin(primary: Boolean)
    fun sendPositionUpdate(
        x: Float, y: Float, z: Float,
        lookX: Float = 0f, lookY: Float = 0f, lookZ: Float = 0f
    )
    fun setOutputGain(gain: Float) {}
    fun updateIceServers(iceServers: List<PeerConnection.IceServer>): Boolean = false
    fun isConnected(): Boolean
    fun disconnect() { close() }
    fun close()
}
