package com.linkpoint.world.profile

import android.util.Log
import com.linkpoint.world.AvatarProfile
import com.linkpoint.world.ProfileInterests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

/**
 * OpenSim REST service profile strategy (Priority Tier 3).
 * Queries OpenSim REST endpoints for profile resolution when Capabilities and UDP strategies are unavailable/fail.
 */
class OpenSimRestProfileStrategy(
    private val restBaseUrl: String? = null,
    private val httpFetcher: (suspend (url: String, method: String, body: String?) -> String?)? = null
) : ProfileResolverStrategy {

    companion object {
        private const val TAG = "OpenSimRestProfileStrategy"
        private const val TIMEOUT_MS = 5000L

        private fun logW(msg: String) {
            try { Log.w(TAG, msg) } catch (_: Throwable) {}
        }
        private fun logE(msg: String, tr: Throwable? = null) {
            try { Log.e(TAG, msg, tr) } catch (_: Throwable) {}
        }
    }

    override val name: String = "OpenSimRestProfileStrategy"
    override val priority: Int = 3

    override fun isAvailable(): Boolean {
        return !restBaseUrl.isNullOrBlank() || httpFetcher != null
    }

    override suspend fun getAvatarProfile(agentId: UUID): AvatarProfile? {
        if (!isAvailable()) return null

        return withContext(Dispatchers.IO) {
            withTimeoutOrNull(TIMEOUT_MS) {
                try {
                    val url = buildProfileUrl(agentId)
                    val responseText = executeHttpRequest(url, "GET", null) ?: return@withTimeoutOrNull null
                    parseProfileResponse(agentId, responseText)
                } catch (e: Exception) {
                    logW("OpenSim REST profile request failed for $agentId: ${e.message}")
                    null
                }
            }
        }
    }

    override suspend fun getDisplayName(agentId: UUID): String? {
        val profile = getAvatarProfile(agentId)
        return profile?.displayName?.takeIf { it.isNotBlank() }
            ?: profile?.userName?.takeIf { it.isNotBlank() }
    }

    override suspend fun getDisplayNames(agentIds: List<UUID>): Map<UUID, String> {
        val results = mutableMapOf<UUID, String>()
        for (id in agentIds) {
            getDisplayName(id)?.let { results[id] = it }
        }
        return results
    }

    override suspend fun updateProfile(
        aboutText: String?,
        firstLifeText: String?,
        profileImage: UUID?,
        firstLifeImage: UUID?,
        interests: ProfileInterests?
    ): Boolean {
        if (!isAvailable()) return false

        return withContext(Dispatchers.IO) {
            withTimeoutOrNull(TIMEOUT_MS) {
                try {
                    val url = "${baseUrl.trimEnd('/')}/update_profile"
                    val payload = JSONObject().apply {
                        aboutText?.let { put("sl_about_text", it) }
                        firstLifeText?.let { put("fl_about_text", it) }
                        profileImage?.let { put("sl_image_id", it.toString()) }
                        firstLifeImage?.let { put("fl_image_id", it.toString()) }
                    }.toString()

                    val response = executeHttpRequest(url, "POST", payload)
                    response != null
                } catch (e: Exception) {
                    logE("OpenSim REST profile update failed", e)
                    false
                }
            } ?: false
        }
    }

    private val baseUrl: String get() = restBaseUrl ?: ""

    private fun buildProfileUrl(agentId: UUID): String {
        val cleanBase = baseUrl.trimEnd('/')
        return if (cleanBase.contains("?")) {
            "$cleanBase&avatar_id=$agentId"
        } else {
            "$cleanBase/profile/$agentId"
        }
    }

    private suspend fun executeHttpRequest(urlString: String, method: String, body: String?): String? {
        if (httpFetcher != null) {
            return httpFetcher.invoke(urlString, method, body)
        }

        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(urlString)
                connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = method
                    connectTimeout = 4000
                    readTimeout = 4000
                    setRequestProperty("Accept", "application/json, text/plain")
                    if (body != null) {
                        doOutput = true
                        setRequestProperty("Content-Type", "application/json")
                        outputStream.use { os ->
                            os.write(body.toByteArray(Charsets.UTF_8))
                        }
                    }
                }

                if (connection.responseCode in 200..299) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else null
            } catch (e: Exception) {
                logW("HTTP $method failed for $urlString: ${e.message}")
                null
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun parseProfileResponse(agentId: UUID, responseText: String): AvatarProfile? {
        return try {
            val json = JSONObject(responseText)
            val displayName = json.optString("display_name", json.optString("username", ""))
            val userName = json.optString("username", "")
            val aboutText = json.optString("sl_about_text", json.optString("about", ""))
            val flAboutText = json.optString("fl_about_text", json.optString("first_life_about", ""))
            val bornOn = json.optString("born_on", "")
            val slImageId = json.optString("sl_image_id").let { if (it.isNotBlank()) try { UUID.fromString(it) } catch (e: Exception) { null } else null }
            val flImageId = json.optString("fl_image_id").let { if (it.isNotBlank()) try { UUID.fromString(it) } catch (e: Exception) { null } else null }
            val partnerId = json.optString("partner_id").let { if (it.isNotBlank()) try { UUID.fromString(it) } catch (e: Exception) { null } else null }

            AvatarProfile(
                agentId = agentId,
                displayName = displayName,
                userName = userName,
                aboutText = aboutText,
                firstLifeText = flAboutText,
                profileImage = slImageId,
                firstLifeImage = flImageId,
                partner = partnerId,
                bornOn = bornOn,
                memberOf = emptyList(),
                groups = emptyList(),
                picks = emptyList(),
                interests = ProfileInterests()
            )
        } catch (e: Exception) {
            logW("Failed to parse JSON profile response: ${e.message}")
            null
        }
    }
}
