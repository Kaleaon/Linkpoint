package com.linkpoint.world.profile

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.CapabilityRequester
import com.linkpoint.protocol.llsd.*
import com.linkpoint.world.AvatarProfile
import com.linkpoint.world.ProfileInterests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Capability-based implementation of ProfileResolverStrategy (Priority Tier 1).
 */
class CapabilityProfileStrategy(
    private val capabilityManager: CapabilityRequester
) : ProfileResolverStrategy {

    companion object {
        private const val TAG = "CapabilityProfileStrategy"

        private fun logW(msg: String) {
            try { Log.w(TAG, msg) } catch (_: Throwable) {}
        }
        private fun logE(msg: String, tr: Throwable? = null) {
            try { Log.e(TAG, msg, tr) } catch (_: Throwable) {}
        }
    }

    override val name: String = "CapabilityProfileStrategy"
    override val priority: Int = 1

    override fun isAvailable(): Boolean {
        return capabilityManager.hasCapability(CapabilityManager.CAP_AGENT_PROFILE) ||
                capabilityManager.hasCapability(CapabilityManager.CAP_GET_DISPLAY_NAMES)
    }

    override suspend fun getAvatarProfile(agentId: UUID): AvatarProfile? {
        return withContext(Dispatchers.IO) {
            try {
                val request = LLSDMap().apply {
                    this["agent_id"] = LLSDString(agentId.toString())
                }
                val response = capabilityManager.request(CapabilityManager.CAP_AGENT_PROFILE, request)
                if (response is LLSDMap) {
                    AvatarProfile(
                        agentId = agentId,
                        displayName = response.getString("display_name") ?: "",
                        userName = response.getString("username") ?: "",
                        aboutText = response.getString("sl_about_text") ?: "",
                        firstLifeText = response.getString("fl_about_text") ?: "",
                        profileImage = response.getString("sl_image_id")?.let {
                            try { UUID.fromString(it) } catch (e: Exception) { null }
                        },
                        firstLifeImage = response.getString("fl_image_id")?.let {
                            try { UUID.fromString(it) } catch (e: Exception) { null }
                        },
                        partner = response.getString("partner_id")?.let {
                            try { UUID.fromString(it) } catch (e: Exception) { null }
                        },
                        bornOn = response.getString("born_on") ?: "",
                        memberOf = emptyList(),
                        groups = emptyList(),
                        picks = emptyList(),
                        interests = ProfileInterests()
                    )
                } else null
            } catch (e: Exception) {
                logW("Capability profile lookup failed for $agentId: ${e.message}")
                null
            }
        }
    }

    override suspend fun getDisplayName(agentId: UUID): String? {
        val names = getDisplayNames(listOf(agentId))
        return names[agentId]
    }

    override suspend fun getDisplayNames(agentIds: List<UUID>): Map<UUID, String> {
        return withContext(Dispatchers.IO) {
            val results = mutableMapOf<UUID, String>()
            if (agentIds.isEmpty()) return@withContext results

            for (batch in agentIds.chunked(80)) {
                try {
                    val response = capabilityManager.requestWithQuery(
                        CapabilityManager.CAP_GET_DISPLAY_NAMES,
                        batch.map { "ids" to it.toString() }
                    )
                    if (response is LLSDMap) {
                        val agents = response.getArray("agents")
                        if (agents != null) {
                            for (i in 0 until agents.size) {
                                val agentData = agents.get(i) as? LLSDMap ?: continue
                                val idStr = agentData.getString("id") ?: continue
                                val name = agentData.getString("display_name")?.takeIf { it.isNotBlank() }
                                    ?: agentData.getString("username")?.takeIf { it.isNotBlank() }
                                    ?: run {
                                        val first = agentData.getString("legacy_first_name") ?: ""
                                        val last = agentData.getString("legacy_last_name") ?: ""
                                        "$first $last".trim().takeIf { it.isNotBlank() }
                                    }
                                    ?: continue
                                try {
                                    val uuid = UUID.fromString(idStr)
                                    results[uuid] = name
                                } catch (_: Exception) { }
                            }
                        }
                    }
                } catch (e: Exception) {
                    logW("Batch display-name capability request failed: ${e.message}")
                }
            }
            results
        }
    }

    override suspend fun updateProfile(
        aboutText: String?,
        firstLifeText: String?,
        profileImage: UUID?,
        firstLifeImage: UUID?,
        interests: ProfileInterests?
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = LLSDMap().apply {
                    aboutText?.let { this["sl_about_text"] = LLSDString(it) }
                    firstLifeText?.let { this["fl_about_text"] = LLSDString(it) }
                    profileImage?.let { this["sl_image_id"] = LLSDString(it.toString()) }
                    firstLifeImage?.let { this["fl_image_id"] = LLSDString(it.toString()) }
                    interests?.let { interestsData ->
                        this["interests"] = LLSDMap().apply {
                            this["want_to_mask"] = LLSDInteger(interestsData.wantToMask)
                            this["want_to_text"] = LLSDString(interestsData.wantToText)
                            this["skills_mask"] = LLSDInteger(interestsData.skillsMask)
                            this["skills_text"] = LLSDString(interestsData.skillsText)
                            this["languages_text"] = LLSDString(interestsData.languagesText)
                        }
                    }
                }
                val response = capabilityManager.request(CapabilityManager.CAP_AGENT_PROFILE, request)
                response != null
            } catch (e: Exception) {
                logE("Failed capability profile update", e)
                false
            }
        }
    }
}
