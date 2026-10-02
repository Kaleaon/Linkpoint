package com.linkpoint.protocol.capabilities

import android.util.Log
import com.linkpoint.protocol.llsd.*
import com.linkpoint.protocol.translation.LinkpointTranslationLayer
import org.json.JSONObject
import java.net.URL

/**
 * Strategy interface for parsing capability responses, validating capability URLs,
 * and transforming headers based on grid protocol requirements.
 */
interface CapabilityParserStrategy {
    /** The grid type associated with this strategy */
    val gridType: LinkpointTranslationLayer.GridType

    /**
     * Parse raw capability response bytes into a map of capability names to URLs.
     *
     * @param bodyBytes Raw response payload
     * @param contentType Response Content-Type header value
     * @return Map of capability name to capability URL, or null if parsing fails
     */
    fun parseCapabilityResponse(bodyBytes: ByteArray, contentType: String?): Map<String, String>?

    /**
     * Validate and repair a capability URL for the active grid protocol.
     *
     * @param capName The capability name
     * @param url The raw capability URL
     * @param loginUrl Optional active login URL for domain inspection
     * @return Validated/repaired capability URL
     */
    fun validateCapabilityUrl(capName: String, url: String, loginUrl: String? = null): String

    /**
     * Transform HTTP headers for capability requests according to grid requirements.
     *
     * @param capName The target capability name
     * @param headers Existing request headers
     * @return Transformed headers map
     */
    fun transformHeaders(capName: String, headers: Map<String, String> = emptyMap()): Map<String, String>
}

/**
 * Dedicated strategy for Linden Lab / Second Life grids (Agni, Aditi).
 * Encapsulates LLSD parsing, Agni simulator URL repair, and Linden S3 headers.
 */
class LindenS3CapHandler(
    override val gridType: LinkpointTranslationLayer.GridType = LinkpointTranslationLayer.GridType.AGNI
) : CapabilityParserStrategy {

    companion object {
        private const val TAG = "LindenS3CapHandler"
    }

    override fun parseCapabilityResponse(bodyBytes: ByteArray, contentType: String?): Map<String, String>? {
        if (bodyBytes.isEmpty()) return null
        return try {
            val llsd = LLSDParser.parseAuto(bodyBytes, contentType)
            if (llsd is LLSDMap) {
                val result = llsd.value.keys.mapNotNull { key ->
                    llsd.getString(key)?.takeIf { it.isNotEmpty() }?.let { key to it }
                }.toMap()
                result.ifEmpty { null }
            } else {
                Log.w(TAG, "Linden S3 capability response was not an LLSD map: ${llsd?.javaClass?.simpleName}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse Linden S3 capability response", e)
            null
        }
    }

    override fun validateCapabilityUrl(capName: String, url: String, loginUrl: String?): String {
        if (url.isBlank()) return url
        val isAgni = gridType == LinkpointTranslationLayer.GridType.AGNI ||
                (loginUrl != null && LinkpointTranslationLayer.isAgniGrid(loginUrl))
        return if (loginUrl != null) {
            LinkpointTranslationLayer.repairUrl(loginUrl, url)
        } else {
            LinkpointTranslationLayer.repairCapabilityUrl(isAgni, url)
        }
    }

    override fun transformHeaders(capName: String, headers: Map<String, String>): Map<String, String> {
        val result = headers.toMutableMap()
        if (!result.containsKey("Accept")) {
            result["Accept"] = "application/llsd+xml, application/llsd+binary"
        }
        if (!result.containsKey("User-Agent")) {
            result["User-Agent"] = "Linkpoint/1.0 (SecondLife-LindenS3)"
        }
        return result
    }
}

/**
 * Dedicated strategy for OpenSim WebFetch asset endpoints and capabilities.
 * Supports OpenSim WebFetch asset capabilities, JSON/LLSD responses, and OpenSim header formats.
 */
class OpenSimWebFetchCapHandler : CapabilityParserStrategy {

    companion object {
        private const val TAG = "OpenSimWebFetchCapHandler"
    }

    override val gridType: LinkpointTranslationLayer.GridType = LinkpointTranslationLayer.GridType.OPENSIM

    override fun parseCapabilityResponse(bodyBytes: ByteArray, contentType: String?): Map<String, String>? {
        if (bodyBytes.isEmpty()) return null

        // 1. Try LLSD parsing
        try {
            val llsd = LLSDParser.parseAuto(bodyBytes, contentType)
            if (llsd is LLSDMap) {
                val map = llsd.value.keys.mapNotNull { key ->
                    llsd.getString(key)?.takeIf { it.isNotEmpty() }?.let { key to it }
                }.toMap()
                if (map.isNotEmpty()) {
                    Log.d(TAG, "Successfully parsed OpenSim capabilities via LLSD (${map.size} caps)")
                    return map
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "LLSD parse attempt on OpenSim response failed, attempting JSON/text fallback: ${e.message}")
        }

        // 2. Try JSON parsing (OpenSim WebFetch services may return JSON)
        try {
            val text = bodyBytes.toString(Charsets.UTF_8).trim()
            if (text.startsWith("{") && text.endsWith("}")) {
                val json = JSONObject(text)
                val map = mutableMapOf<String, String>()
                val keys = json.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val value = json.optString(key)
                    if (value.isNotEmpty()) {
                        map[key] = value
                    }
                }
                if (map.isNotEmpty()) {
                    Log.d(TAG, "Successfully parsed OpenSim capabilities via JSON (${map.size} caps)")
                    return map
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "JSON parse attempt on OpenSim response failed: ${e.message}")
        }

        // 3. Try plain key-value parsing (key=value lines)
        try {
            val text = bodyBytes.toString(Charsets.UTF_8)
            val map = mutableMapOf<String, String>()
            text.lines().forEach { line ->
                val trimmed = line.trim()
                if (trimmed.isNotEmpty() && !trimmed.startsWith("#") && trimmed.contains("=")) {
                    val parts = trimmed.split("=", limit = 2)
                    if (parts.size == 2 && parts[0].trim().isNotEmpty() && parts[1].trim().isNotEmpty()) {
                        map[parts[0].trim()] = parts[1].trim()
                    }
                }
            }
            if (map.isNotEmpty()) {
                Log.d(TAG, "Successfully parsed OpenSim capabilities via key-value text (${map.size} caps)")
                return map
            }
        } catch (e: Exception) {
            Log.d(TAG, "Key-value parse attempt on OpenSim response failed: ${e.message}")
        }

        Log.w(TAG, "Failed to parse OpenSim capability response across all formats")
        return null
    }

    override fun validateCapabilityUrl(capName: String, url: String, loginUrl: String?): String {
        if (url.isBlank()) return url
        // Preserve OpenSim capability and WebFetch URLs without corrupting hostnames with Agni repairs.
        return try {
            val parsedUrl = URL(url)
            // Ensure valid scheme
            if (parsedUrl.protocol != "http" && parsedUrl.protocol != "https") {
                "http://$url"
            } else {
                url
            }
        } catch (e: Exception) {
            url
        }
    }

    override fun transformHeaders(capName: String, headers: Map<String, String>): Map<String, String> {
        val result = headers.toMutableMap()
        result["X-OpenSim-Capability"] = "true"
        if (!result.containsKey("Accept")) {
            result["Accept"] = "application/llsd+xml, application/json, image/x-j2c, */*"
        }
        if (!result.containsKey("User-Agent")) {
            result["User-Agent"] = "Linkpoint/1.0 (OpenSim-WebFetch)"
        }
        return result
    }
}

/**
 * Hybrid fallback handler when grid profile is unknown or mismatched.
 * Preserves basic asset fetching and URL resolution across edge cases.
 */
class HybridFallbackCapHandler : CapabilityParserStrategy {

    companion object {
        private const val TAG = "HybridFallbackCapHandler"
    }

    override val gridType: LinkpointTranslationLayer.GridType = LinkpointTranslationLayer.GridType.UNKNOWN

    override fun parseCapabilityResponse(bodyBytes: ByteArray, contentType: String?): Map<String, String>? {
        if (bodyBytes.isEmpty()) return null

        // Try LLSD first
        runCatching {
            val llsd = LLSDParser.parseAuto(bodyBytes, contentType)
            if (llsd is LLSDMap) {
                val map = llsd.value.keys.mapNotNull { key ->
                    llsd.getString(key)?.takeIf { it.isNotEmpty() }?.let { key to it }
                }.toMap()
                if (map.isNotEmpty()) return map
            }
        }

        // Try JSON fallback
        runCatching {
            val text = bodyBytes.toString(Charsets.UTF_8).trim()
            if (text.startsWith("{")) {
                val json = JSONObject(text)
                val map = mutableMapOf<String, String>()
                val keys = json.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val value = json.optString(key)
                    if (value.isNotEmpty()) map[key] = value
                }
                if (map.isNotEmpty()) return map
            }
        }

        return null
    }

    override fun validateCapabilityUrl(capName: String, url: String, loginUrl: String?): String {
        if (url.isBlank()) return url
        return try {
            if (loginUrl != null && LinkpointTranslationLayer.detectGridType(loginUrl) == LinkpointTranslationLayer.GridType.AGNI) {
                LinkpointTranslationLayer.repairUrl(loginUrl, url)
            } else {
                url
            }
        } catch (e: Exception) {
            url
        }
    }

    override fun transformHeaders(capName: String, headers: Map<String, String>): Map<String, String> {
        val result = headers.toMutableMap()
        if (!result.containsKey("Accept")) {
            result["Accept"] = "application/llsd+xml, application/llsd+binary, application/json, */*"
        }
        if (!result.containsKey("User-Agent")) {
            result["User-Agent"] = "Linkpoint/1.0 (HybridFallback)"
        }
        return result
    }
}

/**
 * Grid-aware capability strategy dispatcher.
 * Selects the appropriate [CapabilityParserStrategy] based on active session metadata / grid profiles.
 */
object CapabilityStrategyDispatcher {

    private const val TAG = "CapStrategyDispatcher"

    /**
     * Select strategy based on login URL or explicit grid type.
     *
     * @param loginUrl Active login URL
     * @param explicitGridType Optional explicit grid type
     * @return Matching [CapabilityParserStrategy] implementation
     */
    fun selectStrategy(
        loginUrl: String? = null,
        explicitGridType: LinkpointTranslationLayer.GridType? = null
    ): CapabilityParserStrategy {
        val resolvedGridType = explicitGridType
            ?: loginUrl?.let { LinkpointTranslationLayer.detectGridType(it) }
            ?: LinkpointTranslationLayer.GridType.UNKNOWN

        Log.d(TAG, "Selecting strategy for gridType=$resolvedGridType (loginUrl=$loginUrl)")

        return when (resolvedGridType) {
            LinkpointTranslationLayer.GridType.AGNI,
            LinkpointTranslationLayer.GridType.ADITI -> LindenS3CapHandler(resolvedGridType)
            LinkpointTranslationLayer.GridType.OPENSIM -> OpenSimWebFetchCapHandler()
            LinkpointTranslationLayer.GridType.UNKNOWN -> HybridFallbackCapHandler()
        }
    }
}
