package com.lumiyaviewer.lumiya.voice.common.model

import android.net.Uri
import android.os.Bundle
import com.google.common.base.Objects
import com.google.common.base.Strings
import com.google.common.primitives.Bytes
import com.google.common.primitives.Longs
import com.lumiyaviewer.lumiya.base64.Base64
import java.util.Arrays
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class VoiceChannelInfo(
    val voiceChannelURI: String?,
    val isSpatial: Boolean,
    val isConference: Boolean
) {
    constructor(uri: Uri) : this(
        uri.getQueryParameter("voiceChannelURI"),
        Objects.equal(uri.getQueryParameter("isSpatial"), "true"),
        Objects.equal(uri.getQueryParameter("isConference"), "true")
    )

    constructor(bundle: Bundle) : this(
        bundle.getString("voiceChannelURI"),
        bundle.getBoolean("isSpatial"),
        bundle.getBoolean("isConference")
    )

    constructor(agentId: UUID, domain: String) : this(
        buildVoiceChannelURI(agentId, domain),
        false,
        false
    )

    fun appendToUri(builder: Uri.Builder) {
        builder.appendQueryParameter("voiceChannelURI", voiceChannelURI)
        val spatialStr = if (isSpatial) "true" else "false"
        builder.appendQueryParameter("isSpatial", spatialStr)
        val conferenceStr = if (isConference) "true" else "false"
        builder.appendQueryParameter("isConference", conferenceStr)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        
        val that = other as VoiceChannelInfo
        if (isSpatial != that.isSpatial) return false
        if (isConference != that.isConference) return false
        return if (voiceChannelURI != null) {
            voiceChannelURI == that.voiceChannelURI
        } else {
            that.voiceChannelURI == null
        }
    }

    fun getAgentUUID(): UUID? {
        return agentUUIDFromURI(voiceChannelURI)
    }

    override fun hashCode(): Int {
        var result = voiceChannelURI?.hashCode() ?: 0
        result = 31 * result + if (isSpatial) 1 else 0
        result = 31 * result + if (isConference) 1 else 0
        return result
    }

    fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString("voiceChannelURI", voiceChannelURI)
        bundle.putBoolean("isSpatial", isSpatial)
        bundle.putBoolean("isConference", isConference)
        return bundle
    }

    companion object {
        @JvmStatic
        fun agentUUIDFromURI(uriStr: String?): UUID? {
            val uri = Strings.nullToEmpty(uriStr)
            
            // Remove "sip:" prefix
            var processedUri = uri
            val colonIndex = processedUri.indexOf(':')
            if (colonIndex != -1) {
                processedUri = processedUri.substring(colonIndex + 1)
            }
            
            // Remove domain part after '@'
            val atIndex = processedUri.indexOf('@')
            if (atIndex != -1) {
                processedUri = processedUri.substring(0, atIndex)
            }
            
            // Remove 'x' prefix
            if (processedUri.startsWith("x")) {
                processedUri = processedUri.substring(1)
            }
            
            // Decode base64
            val bytes = Base64.decode(processedUri.replace("-", "+").replace("_", "/"))
            if (bytes == null || bytes.size != 16) return null
            
            val mostSigBits = Longs.fromByteArray(Arrays.copyOfRange(bytes, 0, 8))
            val leastSigBits = Longs.fromByteArray(Arrays.copyOfRange(bytes, 8, 16))
            return UUID(mostSigBits, leastSigBits)
        }

        private fun buildVoiceChannelURI(agentId: UUID, domain: String): String {
            val bytes = Bytes.concat(
                Longs.toByteArray(agentId.mostSignificantBits),
                Longs.toByteArray(agentId.leastSignificantBits)
            )
            val encoded = "x" + Base64.encodeToString(bytes, false)
                .replace('+', '-')
                .replace('/', '_')
            return "sip:$encoded@$domain"
        }
    }
}