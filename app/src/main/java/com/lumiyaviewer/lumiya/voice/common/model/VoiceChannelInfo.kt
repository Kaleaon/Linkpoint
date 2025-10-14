package com.lumiyaviewer.lumiya.voice.common.model

import android.net.Uri
import android.os.Bundle
import com.google.common.primitives.Longs
import com.lumiyaviewer.lumiya.base64.Base64
import java.util.*

/**
 * Modern Kotlin VoiceChannelInfo data class
 * Represents voice channel information for Second Life voice chat
 */
data class VoiceChannelInfo(
    val voiceChannelURI: String,
    val isSpatial: Boolean,
    val isConference: Boolean,
) {
    /**
     * Creates VoiceChannelInfo from a Uri
     */
    constructor(uri: Uri) : this(
        voiceChannelURI = uri.getQueryParameter("voiceChannelURI") ?: "",
        isSpatial = uri.getQueryParameter("isSpatial") == "true",
        isConference = uri.getQueryParameter("isConference") == "true",
    )

    /**
     * Creates VoiceChannelInfo from a Bundle
     */
    constructor(bundle: Bundle) : this(
        voiceChannelURI = bundle.getString("voiceChannelURI") ?: "",
        isSpatial = bundle.getBoolean("isSpatial"),
        isConference = bundle.getBoolean("isConference"),
    )

    /**
     * Creates VoiceChannelInfo from agent UUID and SIP server
     */
    constructor(agentUUID: UUID, sipServer: String) : this(
        voiceChannelURI =
            run {
                val uuidBytes = ByteArray(16)
                val msb = agentUUID.mostSignificantBits
                val lsb = agentUUID.leastSignificantBits

                // Convert UUID to bytes
                for (i in 0..7) {
                    uuidBytes[i] = (msb shr (56 - i * 8)).toByte()
                    uuidBytes[i + 8] = (lsb shr (56 - i * 8)).toByte()
                }

                // Encode to base64 and format for SIP
                val encoded =
                    Base64.encode(uuidBytes)
                        .replace('+', '-')
                        .replace('/', '_')
                        .replace("=", "")

                "sip:x$encoded@$sipServer"
            },
        isSpatial = false,
        isConference = false,
    )

    companion object {
        /**
         * Extracts agent UUID from a voice channel URI
         */
        @JvmStatic
        fun agentUUIDFromURI(uri: String?): UUID? {
            if (uri.isNullOrEmpty()) return null

            try {
                // Extract the part between "sip:" and "@"
                var working = uri
                val colonIndex = working.indexOf(':')
                if (colonIndex != -1) {
                    working = working.substring(colonIndex + 1)
                }

                val atIndex = working.indexOf('@')
                if (atIndex != -1) {
                    working = working.substring(0, atIndex)
                }

                // Remove 'x' prefix if present
                if (working.startsWith("x")) {
                    working = working.substring(1)
                }

                // Decode from base64
                val decoded =
                    Base64.decode(
                        working.replace("-", "+").replace("_", "/"),
                    )

                if (decoded.size != 16) return null

                // Convert bytes to UUID
                val msb = Longs.fromByteArray(decoded.copyOfRange(0, 8))
                val lsb = Longs.fromByteArray(decoded.copyOfRange(8, 16))

                return UUID(msb, lsb)
            } catch (e: Exception) {
                return null
            }
        }
    }

    /**
     * Appends this channel info to a Uri.Builder
     */
    fun appendToUri(builder: Uri.Builder) {
        builder.appendQueryParameter("voiceChannelURI", voiceChannelURI)
        builder.appendQueryParameter("isSpatial", if (isSpatial) "true" else "false")
        builder.appendQueryParameter("isConference", if (isConference) "true" else "false")
    }

    /**
     * Gets the agent UUID from this channel's URI
     */
    fun getAgentUUID(): UUID? = agentUUIDFromURI(voiceChannelURI)

    /**
     * Converts this channel info to a Bundle
     */
    fun toBundle(): Bundle =
        Bundle().apply {
            putString("voiceChannelURI", voiceChannelURI)
            putBoolean("isSpatial", isSpatial)
            putBoolean("isConference", isConference)
        }
}
