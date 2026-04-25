package com.linkpoint.world

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.CapabilityRequester
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages avatar and group profiles
 */
class ProfileManager(
    private val capabilityManager: CapabilityRequester
) {
    companion object {
        private const val TAG = "ProfileManager"
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Cached profiles
    private val avatarProfiles = ConcurrentHashMap<UUID, AvatarProfile>()
    private val groupProfiles = ConcurrentHashMap<UUID, GroupProfile>()
    
    // Display names cache
    private val displayNames = ConcurrentHashMap<UUID, String>()
    
    /**
     * Get avatar profile via capability request.
     */
    suspend fun getAvatarProfile(agentId: UUID): AvatarProfile? {
        avatarProfiles[agentId]?.let { return it }
        
        return withContext(Dispatchers.IO) {
            try {
                // Request profile from AgentProfile capability
                val request = LLSDMap().apply {
                    this["agent_id"] = LLSDString(agentId.toString())
                }
                
                val response = capabilityManager.request(CapabilityManager.CAP_AGENT_PROFILE, request)
                
                if (response is LLSDMap) {
                    val profile = AvatarProfile(
                        agentId = agentId,
                        displayName = response.getString("display_name") ?: displayNames[agentId] ?: "",
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
                        memberOf = emptyList(), // Parsed from response if available
                        groups = emptyList(),
                        picks = emptyList(),
                        interests = ProfileInterests()
                    )
                    avatarProfiles[agentId] = profile
                    Log.d(TAG, "Retrieved profile for $agentId")
                    profile
                } else {
                    // Return placeholder if capability not available
                    Log.w(TAG, "AgentProfile capability returned non-map response")
                    val profile = AvatarProfile(
                        agentId = agentId,
                        displayName = displayNames[agentId] ?: "",
                        userName = "",
                        aboutText = "",
                        firstLifeText = "",
                        profileImage = null,
                        firstLifeImage = null,
                        partner = null,
                        bornOn = "",
                        memberOf = emptyList(),
                        groups = emptyList(),
                        picks = emptyList(),
                        interests = ProfileInterests()
                    )
                    avatarProfiles[agentId] = profile
                    profile
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get avatar profile", e)
                null
            }
        }
    }
    
    /**
     * Get display name via GetDisplayNames capability.
     *
     * Returns null when the lookup did not resolve a name from the simulator.
     * We deliberately do not synthesise a UUID-prefix fallback here, because
     * caching that would prevent any later retry from ever updating the
     * friends list / chat headers with the real name.
     */
    suspend fun getDisplayName(agentId: UUID): String? {
        displayNames[agentId]?.let { return it }

        return withContext(Dispatchers.IO) {
            try {
                val request = LLSDMap().apply {
                    this["ids"] = LLSDArray().apply {
                        add(LLSDString(agentId.toString()))
                    }
                }

                val response = capabilityManager.request(CapabilityManager.CAP_GET_DISPLAY_NAMES, request)

                if (response is LLSDMap) {
                    val agents = response.getArray("agents")
                    if (agents != null && agents.size > 0) {
                        val agentData = agents.get(0) as? LLSDMap
                        val name = agentData?.getString("display_name")?.takeIf { it.isNotBlank() }
                            ?: agentData?.getString("username")?.takeIf { it.isNotBlank() }
                        if (name != null) {
                            displayNames[agentId] = name
                            return@withContext name
                        }
                    }
                }
                null
            } catch (e: Exception) {
                Log.w(TAG, "Failed to get display name for $agentId: ${e.message}")
                null
            }
        }
    }
    
    /**
     * Get multiple display names via batch GetDisplayNames capability request.
     */
    suspend fun getDisplayNames(agentIds: List<UUID>): Map<UUID, String> {
        return withContext(Dispatchers.IO) {
            val results = mutableMapOf<UUID, String>()
            val missing = mutableListOf<UUID>()
            
            for (id in agentIds) {
                displayNames[id]?.let { results[id] = it } ?: missing.add(id)
            }
            
            if (missing.isNotEmpty()) {
                try {
                    val request = LLSDMap().apply {
                        this["ids"] = LLSDArray().apply {
                            missing.forEach { add(LLSDString(it.toString())) }
                        }
                    }
                    
                    val response = capabilityManager.request(CapabilityManager.CAP_GET_DISPLAY_NAMES, request)
                    
                    if (response is LLSDMap) {
                        val agents = response.getArray("agents")
                        if (agents != null) {
                            for (i in 0 until agents.size) {
                                val agentData = agents.get(i) as? LLSDMap ?: continue
                                val idStr = agentData.getString("id") ?: continue
                                val name = agentData.getString("display_name")?.takeIf { it.isNotBlank() }
                                    ?: agentData.getString("username")?.takeIf { it.isNotBlank() }
                                    ?: continue
                                try {
                                    val uuid = UUID.fromString(idStr)
                                    displayNames[uuid] = name
                                    results[uuid] = name
                                } catch (e: Exception) {
                                    // Invalid UUID, skip
                                }
                            }
                        }
                    }
                    Log.d(TAG, "Retrieved ${results.size} display names")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to batch retrieve display names: ${e.message}")
                }
            }
            
            results
        }
    }
    
    /**
     * Update avatar profile
     */
    suspend fun updateProfile(
        aboutText: String? = null,
        firstLifeText: String? = null,
        profileImage: UUID? = null,
        firstLifeImage: UUID? = null,
        interests: ProfileInterests? = null
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Build profile update request using LLSD
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
                
                // Use AgentProfile capability
                val response = capabilityManager.request(CapabilityManager.CAP_AGENT_PROFILE, request)
                response != null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update profile", e)
                false
            }
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
                    Log.d(TAG, "Retrieved group profile for $groupId: ${profile.name}")
                    profile
                } else {
                    // Return placeholder if capability not available
                    Log.w(TAG, "GroupProfile capability returned non-map response")
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
                Log.e(TAG, "Failed to get group profile", e)
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
                    Log.i(TAG, "Successfully joined group $groupId")
                    true
                } else {
                    Log.w(TAG, "Failed to join group $groupId - no response")
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to join group", e)
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
                    Log.i(TAG, "Successfully left group $groupId")
                    true
                } else {
                    Log.w(TAG, "Failed to leave group $groupId - no response")
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to leave group", e)
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
                Log.i(TAG, "Sent friendship offer to $agentId")
                response != null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to offer friendship", e)
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
                Log.i(TAG, "Accepted friendship from $agentId")
                response != null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to accept friendship", e)
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
                Log.i(TAG, "Declined friendship from $agentId")
                response != null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to decline friendship", e)
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
                Log.i(TAG, "Removed friend $agentId")
                response != null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to remove friend", e)
                false
            }
        }
    }
    
    fun shutdown() {
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
