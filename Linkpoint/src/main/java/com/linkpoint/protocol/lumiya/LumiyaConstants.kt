package com.linkpoint.protocol.lumiya

/**
 * Constants from Lumiya's decompiled source code.
 * These are the EXACT values that made Lumiya work flawlessly on mobile for 10+ years.
 * 
 * Source: Exhaustive analysis of 804 Lumiya slproto Java files
 * Reference: docs/COMPLETE_FIX_PLAN.md
 */
object LumiyaConstants {
    
    // ==================== CORE NETWORK TIMING (SLCircuit.java) ====================
    // These timing constants are CRITICAL for mobile network stability
    
    /** Idle check interval - Lumiya checks connection health every 1 second */
    const val DEFAULT_IDLE_INTERVAL_MS = 1000L
    
    /** Fast idle interval when actively processing - 100ms for responsive feel */
    const val FAST_IDLE_INTERVAL_MS = 100L
    
    /** Maximum message retry attempts before giving up */
    const val MESSAGE_MAX_RETRIES = 3
    
    /** Message timeout - 5 seconds matches Lumiya exactly */
    const val MESSAGE_TIMEOUT_MS = 5000L
    
    /** Time without packets before sending a ping to keep connection alive */
    const val NEED_PING_TIMEOUT_MS = 10000L
    
    /** Interval between ping checks */
    const val PING_INTERVAL_MS = 5000L
    
    /** Number of handled packets to track for duplicate detection */
    const val TRACK_HANDLED_PACKETS = 1024
    
    /** Number of unanswered pings before declaring connection dead */
    const val UNANSWERED_PINGS_DISCONNECT = 3
    
    // ==================== HTTP CONNECTION SETTINGS (SLHTTPSConnection.java) ====================
    
    /** HTTP connect timeout - 60 seconds like Lumiya */
    const val HTTP_CONNECT_TIMEOUT_SECONDS = 60L
    
    /** HTTP read timeout - 60 seconds like Lumiya */
    const val HTTP_READ_TIMEOUT_SECONDS = 60L
    
    /** OkHttp connection pool: max idle connections */
    const val HTTP_MAX_IDLE_CONNECTIONS = 8
    
    /** OkHttp connection pool: keep-alive duration in minutes */
    const val HTTP_KEEP_ALIVE_MINUTES = 5L
    
    // ==================== PACKET FLAGS (SLMessage.java) ====================
    
    /** Packet has ACKs appended at the end */
    const val FLAG_ACK = 0x10
    
    /** Packet is reliable and needs to be ACKed by receiver */
    const val FLAG_RELIABLE = 0x40
    
    /** This is a resent packet (after timeout) */
    const val FLAG_RESENT = 0x20
    
    /** Packet payload is zero-coded (compressed) */
    const val FLAG_ZEROCODED = 0x80
    
    // ==================== PACKET SIZE LIMITS (SLMessage.java) ====================
    
    /** Maximum message size including all data */
    const val MAX_MESSAGE_SIZE = 65536
    
    /** Maximum payload size (leaves room for header and ACKs) */
    const val MAX_PAYLOAD_SIZE = 1018
    
    /** Actual UDP packet transmission size */
    const val MAX_TRANSMIT_SIZE = 1024
    
    /** Packet header size: flags (1) + sequence (4) + extra (1) = 6 bytes */
    const val PACKET_HEADER_SIZE = 6
    
    // ==================== MESSAGE IDS (from PackPayload methods) ====================
    // High frequency messages (single byte after header)
    
    /** StartPingCheck - server checking if client is alive */
    const val MSG_START_PING_CHECK = 0x01
    
    /** CompletePingCheck - client response to StartPingCheck */
    const val MSG_COMPLETE_PING_CHECK = 0x02
    
    /** PacketAck - acknowledging reliable packets (high freq, -5 as signed) */
    const val MSG_PACKET_ACK = 0xFB // -5 as unsigned byte
    
    // Low frequency messages (0xFFFF prefix + 2-byte ID)
    
    /** UseCircuitCode - first message to establish circuit */
    const val MSG_USE_CIRCUIT_CODE = 0x0003
    
    /** CompleteAgentMovement - request full world state */
    const val MSG_COMPLETE_AGENT_MOVEMENT = 0x00F9 // 249 decimal
    
    /** RegionHandshake - server sends region info */
    const val MSG_REGION_HANDSHAKE = 0x0094 // 148 decimal
    
    /** RegionHandshakeReply - client must reply to receive world data */
    const val MSG_REGION_HANDSHAKE_REPLY = 0x0095 // 149 decimal
    
    // ==================== AGENT UPDATE TIMING (SLAvatarControl.java) ====================
    
    /** Idle agent update interval - 2 seconds when not moving */
    const val IDLE_AGENT_UPDATE_INTERVAL_MS = 2000L
    
    /** Minimum agent update interval - 200ms when moving */
    const val MIN_AGENT_UPDATE_INTERVAL_MS = 200L
    
    /** Number of initial fast updates after connection */
    const val INITIAL_FAST_UPDATES = 10
    
    // ==================== RECONNECTION SETTINGS (SLGridConnection.java) ====================
    
    /** Default: auto-reconnect is enabled */
    const val AUTO_RECONNECT_DEFAULT = true
    
    /** Maximum reconnection attempts before giving up */
    const val MAX_RECONNECT_ATTEMPTS = 10
    
    /** Delay between reconnection attempts */
    const val RECONNECT_DELAY_MS = 3000L
    
    // ==================== TEXTURE FETCHER LIMITS (SLTextureFetcher.java) ====================
    
    /** Maximum concurrent UDP texture transfers */
    const val MAX_UDP_TEXTURE_TRANSFERS = 2
    
    /** Interval for checking stalled transfers */
    const val TEXTURE_STALL_CHECK_INTERVAL_MS = 1000L
    
    // ==================== DRAW DISTANCE (SLDrawDistance.java) ====================
    
    /** Minimum draw distance for chat visibility */
    const val CHAT_RANGE = 20.0f
    
    /** Absolute minimum draw distance */
    const val MIN_DRAW_RANGE = 10.5f
    
    /** Timeout before reducing draw distance */
    const val DRAW_RANGE_TIMEOUT_MS = 10000L
    
    // ==================== DNS FALLBACK IPS (SLHTTPSConnection.java) ====================
    // These are Lumiya's hardcoded fallback IPs for when DNS fails
    
    /** Fallback IP for login.agni.lindenlab.com (main grid) */
    const val FALLBACK_IP_LOGIN_AGNI = "216.82.57.58"
    
    /** Google DNS IPs for DNS-over-HTTPS fallback */
    val FALLBACK_IPS_GOOGLE_DNS = listOf(
        "64.233.164.101",
        "64.233.164.113",
        "64.233.164.139",
        "64.233.164.138",
        "64.233.164.100",
        "64.233.164.102"
    )
    
    // ==================== PASSWORD HASH (SLAuth.java) ====================
    
    /** Maximum password length before truncation (Lumiya truncates to 16 chars) */
    const val PASSWORD_MAX_LENGTH = 16
    
    /** Password hash prefix for SL authentication */
    const val PASSWORD_HASH_PREFIX = "\$1\$"
}
