package com.linkpoint.network.grid

import android.util.Log
import com.linkpoint.core.GridInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URI
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * GridInfoResolver
 *
 * Implements client-side dynamic `/grid_info` protocol discovery for OpenSim and Second Life grids.
 * Resolves standard parameters: `loginuri`, `gridname`, `gridnick`, `welcome`, `helperuri`, `economy`, `map`.
 *
 * Guardrails & Performance:
 * - 2.5-second strict network timeout with automated fallback.
 * - Strict HTTP/HTTPS URL format validation prior to session state update.
 * - Non-blocking asynchronous resolution via Coroutines (Dispatchers.IO).
 * - Bypasses SL main/beta grids with zero overhead.
 */
object GridInfoResolver {

    private const val TAG = "GridInfoResolver"
    const val DEFAULT_TIMEOUT_MS = 2500L

    // Reusable HTTP client configured with a 2.5s timeout for fast probe resolution
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .callTimeout(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .connectTimeout(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .readTimeout(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .writeTimeout(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .followRedirects(true)
            .build()
    }

    /**
     * Resolve GridInfo for a target grid address or existing GridInfo profile.
     *
     * @param inputAddress Target hostname or URL (e.g. "osgrid.org", "grid.kitely.com:8002", "http://login.osgrid.org:8002/")
     * @param initialGrid Optional existing GridInfo profile to merge/fallback into
     * @param timeoutMs Maximum probe duration in milliseconds (default: 2500 ms)
     * @return Resolved GridInfo profile if successful, or fallback profile if unreachable/timed out.
     */
    suspend fun resolveGridInfo(
        inputAddress: String,
        initialGrid: GridInfo? = null,
        timeoutMs: Long = DEFAULT_TIMEOUT_MS
    ): GridInfo = withContext(Dispatchers.IO) {
        val trimmed = inputAddress.trim()
        if (trimmed.isEmpty()) {
            return@withContext initialGrid ?: createFallbackGridInfo(inputAddress)
        }

        // Bypasses probe for known Second Life URIs for instant response
        if (isSecondLifeUri(trimmed) || (initialGrid != null && isSecondLifeUri(initialGrid.loginUri))) {
            logD(TAG, "Recognized Second Life grid address ($trimmed); bypassing GridInfo probe.")
            return@withContext initialGrid ?: createFallbackGridInfo(trimmed)
        }

        // Execute GridInfo probe under strict 2.5s coroutine timeout
        val resolved = withTimeoutOrNull(timeoutMs) {
            probeGridInfoEndpoint(trimmed, initialGrid)
        }

        if (resolved != null) {
            logI(TAG, "Successfully resolved GridInfo for $trimmed: name='${resolved.name}', loginuri='${resolved.loginUri}'")
            resolved
        } else {
            logW(TAG, "GridInfo probe for $trimmed timed out or failed within ${timeoutMs}ms; using fallback.")
            initialGrid ?: createFallbackGridInfo(trimmed)
        }
    }

    /**
     * Check if a URL or host belongs to Linden Lab Second Life.
     */
    fun isSecondLifeUri(uriStr: String): Boolean {
        val lower = uriStr.lowercase(Locale.ROOT)
        return lower.contains("lindenlab.com") ||
                lower.contains("secondlife.com") ||
                lower.contains("agni") ||
                lower.contains("aditi") ||
                lower == "secondlife" ||
                lower == "secondlife_beta"
    }

    /**
     * Probe `<grid_address>/grid_info` and fallback endpoints.
     */
    private fun probeGridInfoEndpoint(inputAddress: String, initialGrid: GridInfo?): GridInfo? {
        val probeUrls = buildProbeUrls(inputAddress, initialGrid)

        for (probeUrl in probeUrls) {
            try {
                val request = Request.Builder()
                    .url(probeUrl)
                    .header("User-Agent", "Linkpoint/1.0 GridInfoProbe")
                    .header("Accept", "application/xml, application/json, text/xml, text/plain")
                    .get()
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string() ?: ""
                        if (body.isNotBlank()) {
                            val parsedMap = parseGridInfoPayload(body)
                            if (parsedMap.isNotEmpty()) {
                                return mergeParsedGridInfo(inputAddress, parsedMap, initialGrid)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                logD(TAG, "Probe failed for URL $probeUrl: ${e.message}")
            }
        }
        return null
    }

    /**
     * Construct probe target URLs based on input address.
     */
    fun buildProbeUrls(inputAddress: String, initialGrid: GridInfo?): List<String> {
        val candidateBases = mutableListOf<String>()

        var norm = inputAddress.trim()
        if (!norm.startsWith("http://") && !norm.startsWith("https://")) {
            norm = "http://$norm"
        }

        try {
            val uri = URI(norm)
            val scheme = uri.scheme ?: "http"
            val host = uri.host ?: ""
            val port = if (uri.port != -1) ":${uri.port}" else ""
            if (host.isNotEmpty()) {
                candidateBases.add("$scheme://$host$port")
            }
        } catch (e: Exception) {
            // ignore
        }

        if (initialGrid != null && initialGrid.loginUri.isNotBlank()) {
            try {
                val uri = URI(initialGrid.loginUri)
                val scheme = uri.scheme ?: "http"
                val host = uri.host ?: ""
                val port = if (uri.port != -1) ":${uri.port}" else ""
                if (host.isNotEmpty()) {
                    val base = "$scheme://$host$port"
                    if (!candidateBases.contains(base)) {
                        candidateBases.add(base)
                    }
                }
            } catch (e: Exception) {
                // ignore
            }
        }

        val probeUrls = mutableListOf<String>()
        for (base in candidateBases) {
            val cleanBase = base.trimEnd('/')
            probeUrls.add("$cleanBase/grid_info")
            probeUrls.add("$cleanBase/grid_info.php")
            probeUrls.add("$cleanBase/grid_info.xml")
            probeUrls.add("$cleanBase/grid_info.json")
        }
        return probeUrls.distinct()
    }

    /**
     * Parse XML or JSON GridInfo response payload.
     */
    fun parseGridInfoPayload(payload: String): Map<String, String> {
        val trimmed = payload.trim()
        val result = mutableMapOf<String, String>()

        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            // JSON Payload
            try {
                val json = JSONObject(trimmed)
                val keys = json.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val value = json.optString(key, "").trim()
                    if (value.isNotEmpty()) {
                        result[key.lowercase(Locale.ROOT)] = value
                    }
                }
            } catch (e: Exception) {
                logD(TAG, "Failed to parse JSON GridInfo, falling back to regex: ${e.message}")
            }
        }

        if (result.isEmpty()) {
            // XML or key-value regex parsing
            val xmlTags = listOf("loginuri", "gridname", "gridnick", "welcome", "helperuri", "economy", "map", "search", "search_uri", "searchuri", "directory", "platform", "currency", "currency_symbol", "zero_currency", "asset_server_url", "asset_server", "assetserverurl", "assetserver", "asset_uri", "asseturi")
            for (tag in xmlTags) {
                // Pattern 1: <tag>value</tag> or <tag_name>value</tag_name>
                val tagPattern = Pattern.compile("<$tag>([^<]+)</$tag>", Pattern.CASE_INSENSITIVE)
                val matcher = tagPattern.matcher(payload)
                if (matcher.find()) {
                    matcher.group(1)?.trim()?.let { result[tag] = it }
                } else {
                    // Pattern 2: LLSD XML key-value format: <key>tag</key><string>value</string>
                    val llsdPattern = Pattern.compile("<key>$tag</key>\\s*<string>([^<]+)</string>", Pattern.CASE_INSENSITIVE)
                    val llsdMatcher = llsdPattern.matcher(payload)
                    if (llsdMatcher.find()) {
                        llsdMatcher.group(1)?.trim()?.let { result[tag] = it }
                    }
                }
            }
        }

        return result
    }

    /**
     * Merge extracted GridInfo parameters with initial grid and validate URIs.
     */
    private fun mergeParsedGridInfo(
        inputAddress: String,
        parsedMap: Map<String, String>,
        initialGrid: GridInfo?
    ): GridInfo {
        val gridName = parsedMap["gridname"]?.ifBlank { null }
            ?: initialGrid?.name
            ?: extractHostName(inputAddress)

        val gridNick = parsedMap["gridnick"]?.ifBlank { null }
            ?: initialGrid?.gridNick
            ?: gridName.lowercase(Locale.ROOT).replace(Regex("[^a-z0-9]"), "")

        val rawLoginUri = parsedMap["loginuri"] ?: parsedMap["login"]
        val loginUri = if (isValidHttpUrl(rawLoginUri)) {
            rawLoginUri!!
        } else {
            initialGrid?.loginUri ?: normalizeLoginUri(inputAddress)
        }

        val rawHelperUri = parsedMap["helperuri"] ?: parsedMap["helper"]
        val helperUri = if (isValidHttpUrl(rawHelperUri)) rawHelperUri else initialGrid?.helperUri

        val rawEconomyUri = parsedMap["economy"] ?: parsedMap["economy_uri"]
        val economyUri = if (isValidHttpUrl(rawEconomyUri)) rawEconomyUri else initialGrid?.economyUri

        val isSl = isSecondLifeUri(loginUri)
        val rawCurrencySymbol = parsedMap["currency_symbol"] ?: parsedMap["currency"]
        val currencySymbol = rawCurrencySymbol?.ifBlank { null }
            ?: initialGrid?.currencySymbol
            ?: if (isSl) "L$" else "OS$"

        val rawZeroCurrency = parsedMap["zero_currency"]
        val isZeroCurrencyExplicit = rawZeroCurrency?.equals("true", ignoreCase = true) == true ||
                rawCurrencySymbol?.equals("none", ignoreCase = true) == true ||
                rawCurrencySymbol == "0"
        val isZeroCurrency = if (isZeroCurrencyExplicit) {
            true
        } else if (economyUri == null && !isSl) {
            true
        } else {
            initialGrid?.isZeroCurrency ?: false
        }

        val rawMapUri = parsedMap["map"] ?: parsedMap["map_uri"] ?: parsedMap["mapuri"]
        val mapUri = if (isValidHttpUrl(rawMapUri)) rawMapUri else initialGrid?.mapUri

        val rawSearchUri = parsedMap["search"] ?: parsedMap["search_uri"] ?: parsedMap["searchuri"] ?: parsedMap["directory"]
        val searchUri = if (isValidHttpUrl(rawSearchUri)) rawSearchUri else initialGrid?.searchUri

        val rawWelcomeUri = parsedMap["welcome"] ?: parsedMap["welcome_page"]
        val welcomeUri = if (isValidHttpUrl(rawWelcomeUri)) rawWelcomeUri else initialGrid?.welcomeUri

        val rawAssetServerUrl = parsedMap["asset_server_url"]
            ?: parsedMap["asset_server"]
            ?: parsedMap["assetserverurl"]
            ?: parsedMap["assetserver"]
            ?: parsedMap["asset_uri"]
            ?: parsedMap["asseturi"]
        val assetServerUrl = if (isValidHttpUrl(rawAssetServerUrl)) rawAssetServerUrl else initialGrid?.assetServerUrl

        val id = initialGrid?.id ?: "grid_${gridNick.ifBlank { "custom" }}"

        return GridInfo(
            id = id,
            name = gridName,
            loginUri = loginUri,
            gridNick = gridNick,
            isSecure = loginUri.startsWith("https://"),
            helperUri = helperUri,
            website = welcomeUri ?: initialGrid?.website,
            support = initialGrid?.support,
            registerUri = initialGrid?.registerUri,
            passwordUri = initialGrid?.passwordUri,
            economyUri = economyUri,
            currencySymbol = currencySymbol,
            isZeroCurrency = isZeroCurrency,
            mapUri = mapUri,
            searchUri = searchUri,
            welcomeUri = welcomeUri,
            isResolved = true,
            assetServerUrl = assetServerUrl
        )
    }

    /**
     * Create fallback GridInfo if discovery fails or times out.
     */
    fun createFallbackGridInfo(inputAddress: String): GridInfo {
        val loginUri = normalizeLoginUri(inputAddress)
        val hostName = extractHostName(inputAddress)
        val nick = hostName.lowercase(Locale.ROOT).replace(Regex("[^a-z0-9]"), "")
        val isSl = isSecondLifeUri(loginUri)
        return GridInfo(
            id = "grid_${if (nick.isBlank()) "custom" else nick}",
            name = if (hostName.isBlank()) "Custom Grid" else hostName,
            loginUri = loginUri,
            gridNick = nick,
            isSecure = loginUri.startsWith("https://"),
            currencySymbol = if (isSl) "L$" else "OS$",
            isZeroCurrency = !isSl,
            isResolved = false
        )
    }

    /**
     * Validate HTTP/HTTPS URL format strictly.
     */
    fun isValidHttpUrl(urlStr: String?): Boolean {
        if (urlStr.isNullOrBlank()) return false
        return try {
            val uri = URI(urlStr.trim())
            val scheme = uri.scheme?.lowercase(Locale.ROOT)
            (scheme == "http" || scheme == "https") && !uri.host.isNullOrBlank()
        } catch (e: Exception) {
            false
        }
    }

    private fun normalizeLoginUri(inputAddress: String): String {
        var trimmed = inputAddress.trim()
        if (trimmed.isEmpty()) return "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            trimmed = "http://$trimmed"
        }
        if (!trimmed.endsWith("/")) {
            trimmed = "$trimmed/"
        }
        return trimmed
    }

    private fun extractHostName(inputAddress: String): String {
        var norm = inputAddress.trim()
        if (!norm.startsWith("http://") && !norm.startsWith("https://")) {
            norm = "http://$norm"
        }
        return try {
            URI(norm).host ?: inputAddress
        } catch (e: Exception) {
            inputAddress
        }
    }

    private fun logD(tag: String, msg: String) {
        try { Log.d(tag, msg) } catch (_: Throwable) {}
    }

    private fun logI(tag: String, msg: String) {
        try { Log.i(tag, msg) } catch (_: Throwable) {}
    }

    private fun logW(tag: String, msg: String) {
        try { Log.w(tag, msg) } catch (_: Throwable) {}
    }
}
