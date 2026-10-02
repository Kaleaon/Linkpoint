package com.linkpoint.grid.network

import android.util.Log
import com.linkpoint.grid.persistence.GridProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Direct `/grid_info` HTTP prober for unlisted or private OpenSim grids.
 * Used as a fallback when a grid is missing from the local directory cache.
 */
class GridInfoProber(
    private val client: OkHttpClient = defaultClient
) {

    companion object {
        private const val TAG = "GridInfoProber"

        private val defaultClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    suspend fun probeGrid(gridUriOrAddress: String): GridProfileEntity? = withContext(Dispatchers.IO) {
        val normalizedBase = normalizeBaseUri(gridUriOrAddress)
        val probeEndpoints = listOf(
            "$normalizedBase/grid_info",
            "$normalizedBase/get_grid_info",
            "$normalizedBase/get_grid_info.php",
            normalizedBase
        )

        for (endpoint in probeEndpoints) {
            try {
                Log.d(TAG, "Probing grid info at $endpoint")
                val request = Request.Builder()
                    .url(endpoint)
                    .header("User-Agent", "Linkpoint-Viewer/2.0 GridInfoProber")
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) continue

                val responseBody = response.body?.string() ?: continue
                val profile = parseGridInfoResponse(responseBody, normalizedBase)
                if (profile != null) {
                    Log.i(TAG, "Successfully probed grid: ${profile.name} at ${profile.loginUri}")
                    return@withContext profile
                }
            } catch (e: Exception) {
                Log.d(TAG, "Probe attempt at $endpoint failed: ${e.message}")
            }
        }

        Log.w(TAG, "Direct /grid_info probe failed for $gridUriOrAddress — generating direct URI fallback")
        createFallbackProfile(normalizedBase)
    }

    private fun parseGridInfoResponse(rawResponse: String, baseUri: String): GridProfileEntity? {
        val trimmed = rawResponse.trim()
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            return parseJsonObject(JSONObject(trimmed), baseUri)
        }

        if (trimmed.contains("gridname") || trimmed.contains("login") || trimmed.contains("loginuri")) {
            val map = mutableMapOf<String, String>()
            trimmed.lines().forEach { line ->
                val parts = line.split("=", limit = 2)
                if (parts.size == 2) {
                    val key = parts[0].trim().lowercase().removePrefix("\"").removeSuffix("\"")
                    val value = parts[1].trim().removePrefix("\"").removeSuffix("\"")
                    map[key] = value
                }
            }
            if (map.isNotEmpty()) {
                val login = map["login"] ?: map["loginuri"] ?: map["login_uri"] ?: "$baseUri/"
                val name = map["gridname"] ?: map["grid_name"] ?: extractHostName(baseUri)
                val nick = map["gridnick"] ?: map["grid_nick"] ?: name.lowercase().replace(" ", "")
                return GridProfileEntity(
                    id = "custom_" + Math.abs(login.hashCode()),
                    name = name,
                    gridNick = nick,
                    loginUri = login,
                    helperUri = map["helper"] ?: map["helperuri"],
                    website = map["welcome"] ?: map["about"],
                    registerUri = map["register"],
                    passwordUri = map["password"],
                    status = "online",
                    isCustom = true
                )
            }
        }
        return null
    }

    private fun parseJsonObject(json: JSONObject, baseUri: String): GridProfileEntity? {
        val loginUri = json.optString("login", json.optString("loginuri", json.optString("login_uri", "$baseUri/")))
        val gridName = json.optString("gridname", json.optString("grid_name", json.optString("platform", extractHostName(baseUri))))
        val gridNick = json.optString("gridnick", json.optString("grid_nick", gridName.lowercase().replace(" ", "")))

        return GridProfileEntity(
            id = "custom_" + Math.abs(loginUri.hashCode()),
            name = gridName,
            gridNick = gridNick,
            loginUri = loginUri,
            helperUri = json.optNullableString("helper") ?: json.optNullableString("helperuri"),
            website = json.optNullableString("welcome") ?: json.optNullableString("about"),
            registerUri = json.optNullableString("register"),
            passwordUri = json.optNullableString("password"),
            status = "online",
            isCustom = true
        )
    }

    private fun createFallbackProfile(baseUri: String): GridProfileEntity {
        val hostName = extractHostName(baseUri)
        val loginUri = if (baseUri.endsWith("/")) baseUri else "$baseUri/"
        return GridProfileEntity(
            id = "custom_" + Math.abs(loginUri.hashCode()),
            name = hostName,
            gridNick = hostName.lowercase().replace(" ", ""),
            loginUri = loginUri,
            status = "unknown",
            isCustom = true
        )
    }

    private fun normalizeBaseUri(input: String): String {
        var uri = input.trim()
        if (!uri.startsWith("http://") && !uri.startsWith("https://")) {
            uri = "http://$uri"
        }
        return uri.removeSuffix("/")
    }

    private fun extractHostName(uri: String): String {
        return try {
            val url = java.net.URL(uri)
            url.host.takeIf { it.isNotEmpty() } ?: uri
        } catch (e: Exception) {
            uri
        }
    }
}
