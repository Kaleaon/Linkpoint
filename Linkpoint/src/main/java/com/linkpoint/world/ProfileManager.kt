package com.linkpoint.world

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.CapabilityRequester
import com.linkpoint.protocol.llsd.*
import com.linkpoint.world.profile.CapabilityProfileStrategy
import com.linkpoint.world.profile.ProfileResolverStrategy
import kotlinx.coroutines.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Manages avatar and group profiles using pluggable resolver strategies.
 */
class ProfileManager(
    private val capabilityManager: CapabilityRequester,
    initialStrategies: List<ProfileResolverStrategy> = emptyList()
) {
    companion object {
        private const val TAG = "ProfileManager"
        private const val STRATEGY_TIMEOUT_MS = 5000L

        private fun logD(msg: String) { try { Log.d(TAG, msg) } catch (_: Throwable) {} }
        private fun logI(msg: String) { try { Log.i(TAG, msg) } catch (_: Throwable) {} }
        private fun logW(msg: String) { try { Log.w(TAG, msg) } catch (_: Throwable) {} }
        private fun logE(msg: String, tr: Throwable? = null) { try { Log.e(TAG, msg, tr) } catch (_: Throwable) {} }
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Cached profiles
    private val avatarProfiles = ConcurrentHashMap<UUID, AvatarProfile>()
    private val groupProfiles = ConcurrentHashMap<UUID, GroupProfile>()

    // Display names cache
    private val displayNames = ConcurrentHashMap<UUID, String>()

    // Registered strategies
    private val strategies = CopyOnWriteArrayList<ProfileResolverStrategy>()

    init {
        strategies.add(CapabilityProfileStrategy(capabilityManager))
        strategies.addAll(initialStrategies)
    }

    fun registerStrategy(strategy: ProfileResolverStrategy) {
        if (!strategies.contains(strategy)) {
            strategies.add(strategy)
        }
    }

    fun unregisterStrategy(strategyName: String) {
        strategies.removeAll { it.name == strategyName }
    }

    fun getRegisteredStrategies(): List<ProfileResolverStrategy> {
        return strategies.sortedBy { it.priority }
    }

    /**
     * Get avatar profile via registered strategies in priority order.
     */
    suspend fun getAvatarProfile(agentId: UUID): AvatarProfile? {
        avatarProfiles[agentId]?.let { return it }

        return withContext(Dispatchers.IO) {
            val availableStrategies = strategies.filter { it.isAvailable() }.sortedBy { it.priority }

            for (strategy in availableStrategies) {
                try {
                    val profile = withTimeoutOrNull(STRATEGY_TIMEOUT_MS) {
                        strategy.getAvatarProfile(agentId)
                    }
                    if (profile != null) {
                        avatarProfiles[agentId] = profile
                        logD("Retrieved profile for $agentId via ${strategy.name}")
                        return@withContext profile
                    }
                } catch (e: Exception) {
                    logW("Strategy ${strategy.name} failed for $agentId: ${e.message}")
                }
            }
            null
        }
    }

    /**
     * Get display name via registered strategies in priority order.
     */
    suspend fun getDisplayName(agentId: UUID): String? {
        displayNames[agentId]?.let { return it }

        return withContext(Dispatchers.IO) {
            val availableStrategies = strategies.filter { it.isAvailable() }.sortedBy { it.priority }

            for (strategy in availableStrategies) {
                try {
                    val name = withTimeoutOrNull(STRATEGY_TIMEOUT_MS) {
                        strategy.getDisplayName(agentId)
                    }
                    if (!name.isNullOrBlank()) {
                        displayNames[agentId] = name
                        return@withContext name
                    }
                } catch (e: Exception) {
                    logW("Strategy ${strategy.name} failed for display name $agentId: ${e.message}")
                }
            }
            null
        }
    }

    /**
     * Get multiple display names via registered strategies in priority order.
     */
    suspend fun getDisplayNames(agentIds: List<UUID>): Map<UUID, String> {
        return withContext(Dispatchers.IO) {
            val results = mutableMapOf<UUID, String>()
            val missing = mutableListOf<UUID>()

            for (id in agentIds) {
                displayNames[id]?.let { results[id] = it } ?: missing.add(id)
            }
            if (missing.isEmpty()) return@withContext results

            val availableStrategies = strategies.filter { it.isAvailable() }.sortedBy { it.priority }

            var currentMissing = missing.toList()
            for (strategy in availableStrategies) {
                if (currentMissing.isEmpty()) break
                try {
                    val resolved = withTimeoutOrNull(STRATEGY_TIMEOUT_MS) {
                        strategy.getDisplayNames(currentMissing)
                    }
                    if (resolved != null && resolved.isNotEmpty()) {
                        for ((id, name) in resolved) {
                            if (name.isNotBlank()) {
                                displayNames[id] = name
                                results[id] = name
                            }
                        }
                        currentMissing = currentMissing.filterNot { results.containsKey(it) }
                    }
                } catch (e: Exception) {
                    logW("Strategy ${strategy.name} failed for batch display names: ${e.message}")
                }
            }
            logD("Retrieved ${results.size} display names (requested ${agentIds.size})")
            results
        }
    }

    /**
     * Update avatar profile using registered strategies in priority order.
     */
    suspend fun updateProfile(
        aboutText: String? = null,
        firstLifeText: String? = null,
        profileImage: UUID? = null,
        firstLifeImage: UUID? = null,
        interests: ProfileInterests? = null
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val availableStrategies = strategies.filter { it.isAvailable() }.sortedBy { it.priority }

            for (strategy in availableStrategies) {
                try {
                    val success = withTimeoutOrNull(STRATEGY_TIMEOUT_MS) {
                        strategy.updateProfile(aboutText, firstLifeText, profileImage, firstLifeImage, interests)
                    } ?: false

                    if (success) {
                        logI("Successfully updated profile via strategy ${strategy.name}")
                        return@withContext true
                    }
                } catch (e: Exception) {
                    logW("Strategy ${strategy.name} failed profile update: ${e.message}")
                }
            }
            false
        }
    }

    /**
     * Get group profile via GroupProfile capability.
     */
    suspend fun getGroupProfile(groupId: UUID): GroupProfile? {
        groupProfiles[groupId]?.let { return it }

        return withContext(Dispatchers.IO) {
            try {
                // Request group profile from capability
                val request = LLSDMap().apply {
                    this["group_id"] = LLSDString(groupId.toString())
                }

                val response = capabilityManager.request(CapabilityManager.CAP_GROUP_PROFILE, request)

                if (response is LLSDMap) {
                    val profile = GroupProfile(
                        groupId = groupId,
                        name = response.getString("name") ?: "",
                        charter = response.getString("charter") ?: "",
                        insigniaId = response.getString("insignia_id")?.let {
                            try { UUID.fromString(it) } catch (e: Exception) { null }
                        },
                        founderName = response.getString("founder_name") ?: "",
                        founderId = response.getString("founder_id")?.let {
                            try { UUID.fromString(it) } catch (e: Exception) { null }
                        },
                        memberCount = response.getInt("member_count") ?: 0,
                        isOpenEnrollment = response.getInt("open_enrollment") == 1,
                        membershipFee = response.getInt("membership_fee") ?: 0,
                        showInList = response.getInt("show_in_list") == 1,
                        maturePublish = response.getInt("mature_content") == 1,
                        ownerRole = response.getString("owner_role_id")?.let {
                            try { UUID.fromString(it) } catch (e: Exception) { null }
                        },
                        roles = emptyList(),
                        members = emptyList(),
                        notices = emptyList()
                    )
                    groupProfiles[groupId] = profile
                    logD("Retrieved group profile for $groupId: ${profile.name}")
                    profile
                } else {
                    // Return placeholder if capability not available
                    logW("GroupProfile capability returned non-map response")
                    val profile = GroupProfile(
                        groupId = groupId,
                        name = "",
                        charter = "",
                        insigniaId = null,
                        founderName = "",
                        founderId = null,
                        memberCount = 0,
                        isOpenEnrollment = false,
                        membershipFee = 0,
                        showInList = true,
                        maturePublish = false,
                        ownerRole = null,
                        roles = emptyList(),
                        members = emptyList(),
                        notices = emptyList()
                    )
                    groupProfiles[groupId] = profile
                    profile
                }
            } catch (e: Exception) {
                logE("Failed to get group profile", e)
                null
            }
        }
    }

    /**
     * Join a group
     */
    suspend fun joinGroup(groupId: UUID): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Use GroupMemberData capability to join group
                val request = LLSDMap().apply {
                    this["group_id"] = LLSDString(groupId.toString())
                    this["action"] = LLSDString("join")
                }

                val response = capabilityManager.request(CapabilityManager.CAP_GROUP_MEMBER_DATA, request)
                if (response != null) {
                    logI("Successfully joined group $groupId")
                    true
                } else {
                    logW("Failed to join group $groupId - no response")
                    false
                }
            } catch (e: Exception) {
                logE("Failed to join group", e)
                false
            }
        }
    }

    /**
     * Leave a group
     */
    suspend fun leaveGroup(groupId: UUID): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Use GroupMemberData capability to leave group
                val request = LLSDMap().apply {
                    this["group_id"] = LLSDString(groupId.toString())
                    this["action"] = LLSDString("leave")
                }

                val response = capabilityManager.request(CapabilityManager.CAP_GROUP_MEMBER_DATA, request)
                if (response != null) {
                    logI("Successfully left group $groupId")
                    true
                } else {
                    logW("Failed to leave group $groupId - no response")
                    false
                }
            } catch (e: Exception) {
                logE("Failed to leave group", e)
                false
            }
        }
    }

    /**
     * Send friendship request (via ImprovedInstantMessage with dialog type 38)
     * Note: This should typically be handled by FriendsManager which has UDP access
     */
    suspend fun offerFriendship(agentId: UUID, message: String = ""): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Use ChatSend capability for friendship offer
                val request = LLSDMap().apply {
                    this["target_id"] = LLSDString(agentId.toString())
                    this["dialog"] = LLSDInteger(38)  // IM_FRIENDSHIP_OFFERED
                    this["message"] = LLSDString(message)
                }

                val response = capabilityManager.request(CapabilityManager.CAP_CHAT_SEND, request)
                logI("Sent friendship offer to $agentId")
                response != null
            } catch (e: Exception) {
                logE("Failed to offer friendship", e)
                false
            }
        }
    }

    /**
     * Accept friendship
     */
    suspend fun acceptFriendship(agentId: UUID, transactionId: UUID): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Use ChatSend capability for friendship acceptance
                val request = LLSDMap().apply {
                    this["target_id"] = LLSDString(agentId.toString())
                    this["transaction_id"] = LLSDString(transactionId.toString())
                    this["dialog"] = LLSDInteger(39)  // IM_FRIENDSHIP_ACCEPTED
                }

                val response = capabilityManager.request(CapabilityManager.CAP_CHAT_SEND, request)
                logI("Accepted friendship from $agentId")
                response != null
            } catch (e: Exception) {
                logE("Failed to accept friendship", e)
                false
            }
        }
    }

    /**
     * Decline friendship
     */
    suspend fun declineFriendship(agentId: UUID, transactionId: UUID): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Use ChatSend capability for friendship decline
                val request = LLSDMap().apply {
                    this["target_id"] = LLSDString(agentId.toString())
                    this["transaction_id"] = LLSDString(transactionId.toString())
                    this["dialog"] = LLSDInteger(40)  // IM_FRIENDSHIP_DECLINED
                }

                val response = capabilityManager.request(CapabilityManager.CAP_CHAT_SEND, request)
                logI("Declined friendship from $agentId")
                response != null
            } catch (e: Exception) {
                logE("Failed to decline friendship", e)
                false
            }
        }
    }

    /**
     * Remove friend
     */
    suspend fun removeFriend(agentId: UUID): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Use FriendshipTerminate capability
                val request = LLSDMap().apply {
                    this["friend_id"] = LLSDString(agentId.toString())
                }

                val response = capabilityManager.request(CapabilityManager.CAP_FRIENDSHIP_TERMINATE, request)
                logI("Removed friend $agentId")
                response != null
            } catch (e: Exception) {
                logE("Failed to remove friend", e)
                false
            }
        }
    }

    fun shutdown() {
        for (strategy in strategies) {
            if (strategy is com.linkpoint.world.profile.UdpProfileStrategy) {
                strategy.dispose()
            }
        }
        strategies.clear()
        scope.cancel()
    }
}

data class AvatarProfile(
    val agentId: UUID,
    val displayName: String,
    val userName: String,
    val aboutText: String,
    val firstLifeText: String,
    val profileImage: UUID?,
    val firstLifeImage: UUID?,
    val partner: UUID?,
    val bornOn: String,
    val memberOf: List<String>,
    val groups: List<GroupMembership>,
    val picks: List<ProfilePick>,
    val interests: ProfileInterests
)

data class ProfileInterests(
    val wantToMask: Int = 0,
    val wantToText: String = "",
    val skillsMask: Int = 0,
    val skillsText: String = "",
    val languagesText: String = ""
)

data class ProfilePick(
    val pickId: UUID,
    val name: String,
    val description: String,
    val snapshotId: UUID?,
    val parcelId: UUID?,
    val simName: String,
    val posGlobal: Triple<Double, Double, Double>
)

data class GroupMembership(
    val groupId: UUID,
    val name: String,
    val insigniaId: UUID?,
    val contribution: Int,
    val acceptNotices: Boolean,
    val listInProfile: Boolean,
    val powers: Long
)

data class GroupProfile(
    val groupId: UUID,
    val name: String,
    val charter: String,
    val insigniaId: UUID?,
    val founderName: String,
    val founderId: UUID?,
    val memberCount: Int,
    val isOpenEnrollment: Boolean,
    val membershipFee: Int,
    val showInList: Boolean,
    val maturePublish: Boolean,
    val ownerRole: UUID?,
    val roles: List<GroupRole>,
    val members: List<GroupMember>,
    val notices: List<GroupNotice>
)

data class GroupRole(
    val roleId: UUID,
    val name: String,
    val title: String,
    val description: String,
    val powers: Long,
    val memberCount: Int
)

data class GroupMember(
    val agentId: UUID,
    val contribution: Int,
    val isOnline: Boolean,
    val roles: List<UUID>,
    val title: String
)

data class GroupNotice(
    val noticeId: UUID,
    val subject: String,
    val message: String,
    val fromName: String,
    val timestamp: Long,
    val hasAttachment: Boolean,
    val attachmentName: String?
)
