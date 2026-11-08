package com.linkpoint.groups.provider

import okhttp3.OkHttpClient
import java.net.URI
import java.util.UUID

/**
 * Factory for instantiating the appropriate [GroupServiceProvider] based on session configuration.
 */
object GroupServiceProviderFactory {

    /**
     * Validates whether a GroupServerURI string is a valid HTTP or HTTPS endpoint URL.
     */
    fun isValidGroupServerUri(uri: String?): Boolean {
        if (uri.isNullOrBlank()) return false
        return try {
            val parsed = URI(uri.trim())
            val scheme = parsed.scheme?.lowercase()
            (scheme == "http" || scheme == "https") && !parsed.host.isNullOrEmpty()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Creates and returns a [GroupServiceProvider] matching the provided endpoint URI.
     * Falls back to [DefaultLlsdUdpGroupProvider] if the URI is blank or invalid.
     */
    fun createProvider(
        groupServerUri: String?,
        agentId: UUID,
        sessionId: UUID = UUID(0L, 0L),
        httpClient: OkHttpClient? = null
    ): GroupServiceProvider {
        val trimmed = groupServerUri?.trim().orEmpty()
        if (!isValidGroupServerUri(trimmed)) {
            return DefaultLlsdUdpGroupProvider()
        }

        return when {
            trimmed.contains("simian", ignoreCase = true) -> {
                SimianRestGroupClient(
                    groupServerUri = trimmed,
                    agentId = agentId,
                    sessionId = sessionId,
                    client = httpClient
                )
            }
            else -> {
                FlotsamXmlRpcGroupClient(
                    groupServerUri = trimmed,
                    agentId = agentId,
                    sessionId = sessionId,
                    client = httpClient
                )
            }
        }
    }
}
