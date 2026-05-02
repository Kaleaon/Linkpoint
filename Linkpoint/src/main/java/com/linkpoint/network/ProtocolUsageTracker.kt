package com.linkpoint.network

/**
 * Encapsulates HTTP protocol usage statistics and categorization.
 */
class ProtocolUsageTracker {
    data class ProtocolStatistics(
        var http2Requests: Int = 0,
        var http11Requests: Int = 0,
        var http10Requests: Int = 0,
        var textureHttp2Count: Int = 0,
        var textureHttp11Count: Int = 0,
        var meshHttp2Count: Int = 0,
        var meshHttp11Count: Int = 0,
        var capabilityHttp2Count: Int = 0,
        var capabilityHttp11Count: Int = 0,
        var lastTextureProtocol: String = "unknown",
        var lastMeshProtocol: String = "unknown",
        var lastCapabilityProtocol: String = "unknown"
    ) {
        fun getHttp2Percentage(): Float {
            val total = http2Requests + http11Requests + http10Requests
            return if (total > 0) (http2Requests.toFloat() / total) * 100 else 0f
        }

        override fun toString(): String {
            return buildString {
                appendLine("HTTP Protocol Statistics:")
                appendLine("  HTTP/2 Requests: $http2Requests (${String.format("%.1f", getHttp2Percentage())}%)")
                appendLine("  HTTP/1.1 Requests: $http11Requests")
                appendLine("  HTTP/1.0 Requests: $http10Requests")
                appendLine()
                appendLine("  By Request Type:")
                appendLine("    Textures: HTTP/2=$textureHttp2Count, HTTP/1.1=$textureHttp11Count")
                appendLine("    Meshes: HTTP/2=$meshHttp2Count, HTTP/1.1=$meshHttp11Count")
                appendLine("    Capabilities: HTTP/2=$capabilityHttp2Count, HTTP/1.1=$capabilityHttp11Count")
                appendLine()
                appendLine("  Last Protocols Used:")
                appendLine("    Texture: $lastTextureProtocol")
                appendLine("    Mesh: $lastMeshProtocol")
                appendLine("    Capability: $lastCapabilityProtocol")
            }
        }
    }

    enum class RequestType {
        TEXTURE,
        MESH,
        CAPABILITY,
        LOGIN,
        EVENT_QUEUE,
        INVENTORY,
        OTHER
    }

    private val protocolStats = ProtocolStatistics()

    fun getProtocolStatistics(): ProtocolStatistics = protocolStats.copy()

    fun trackByType(type: RequestType, protocol: String) {
        val flags = resolveProtocolFlags(protocol)
        updateOverallCounts(flags)

        when (type) {
            RequestType.TEXTURE -> {
                protocolStats.lastTextureProtocol = protocol
                if (flags.isHttp2) protocolStats.textureHttp2Count++ else protocolStats.textureHttp11Count++
            }
            RequestType.MESH -> {
                protocolStats.lastMeshProtocol = protocol
                if (flags.isHttp2) protocolStats.meshHttp2Count++ else protocolStats.meshHttp11Count++
            }
            RequestType.CAPABILITY, RequestType.LOGIN, RequestType.EVENT_QUEUE, RequestType.INVENTORY -> {
                protocolStats.lastCapabilityProtocol = protocol
                if (flags.isHttp2) protocolStats.capabilityHttp2Count++ else protocolStats.capabilityHttp11Count++
            }
            RequestType.OTHER -> Unit
        }
    }

    fun trackByUrl(url: String, protocol: String) {
        val flags = resolveProtocolFlags(protocol)
        updateOverallCounts(flags)

        val urlLower = url.lowercase()
        when {
            urlLower.contains("texture") || urlLower.contains("gettexture") -> {
                protocolStats.lastTextureProtocol = protocol
                if (flags.isHttp2) protocolStats.textureHttp2Count++ else protocolStats.textureHttp11Count++
            }
            urlLower.contains("mesh") || urlLower.contains("getmesh") -> {
                protocolStats.lastMeshProtocol = protocol
                if (flags.isHttp2) protocolStats.meshHttp2Count++ else protocolStats.meshHttp11Count++
            }
            urlLower.contains("cap") || urlLower.contains("simhost") || urlLower.contains("secondlife.com") -> {
                protocolStats.lastCapabilityProtocol = protocol
                if (flags.isHttp2) protocolStats.capabilityHttp2Count++ else protocolStats.capabilityHttp11Count++
            }
        }
    }

    private fun updateOverallCounts(flags: ProtocolFlags) {
        when {
            flags.isHttp2 -> protocolStats.http2Requests++
            flags.isHttp11 -> protocolStats.http11Requests++
            flags.isHttp10 -> protocolStats.http10Requests++
        }
    }

    private fun resolveProtocolFlags(protocol: String): ProtocolFlags {
        return ProtocolFlags(
            isHttp2 = protocol.contains("h2", ignoreCase = true) || protocol.contains("http/2", ignoreCase = true),
            isHttp11 = protocol.contains("1.1"),
            isHttp10 = protocol.contains("1.0")
        )
    }

    private data class ProtocolFlags(
        val isHttp2: Boolean,
        val isHttp11: Boolean,
        val isHttp10: Boolean
    )
}
