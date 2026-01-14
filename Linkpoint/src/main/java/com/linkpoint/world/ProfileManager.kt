package com.linkpoint.world

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages avatar and group profiles
 */
class ProfileManager(
    private val capabilityManager: CapabilityManager
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
     * Get avatar profile
     */
    suspend fun getAvatarProfile(agentId: UUID): AvatarProfile? {
        avatarProfiles[agentId]?.let { return it }
        
        return withContext(Dispatchers.IO) {
            try {
                // Would request profile from server
                // For now, return placeholder
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
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get avatar profile", e)
                null
            }
        }
    }
    
    /**
     * Get display name
     */
    suspend fun getDisplayName(agentId: UUID): String {
        displayNames[agentId]?.let { return it }
        
        return withContext(Dispatchers.IO) {
            try {
                val request = LLSDMap().apply {
                    this["ids"] = LLSDArray().apply {
                        add(LLSDString(agentId.toString()))
                    }
                }
                
                // Request from server
                // Placeholder
                displayNames[agentId] ?: agentId.toString().substring(0, 8)
            } catch (e: Exception) {
                agentId.toString().substring(0, 8)
            }
        }
    }
    
    /**
     * Get multiple display names
     */
    suspend fun getDisplayNames(agentIds: List<UUID>): Map<UUID, String> {
        return withContext(Dispatchers.IO) {
            val results = mutableMapOf<UUID, String>()
            val missing = mutableListOf<UUID>()
            
            for (id in agentIds) {
                displayNames[id]?.let { results[id] = it } ?: missing.add(id)
            }
            
            if (missing.isNotEmpty()) {
                // Would batch request from server
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
                val response = capabilityManager.request("AgentProfile", request)
                response != null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update profile", e)
                false
            }
        }
    }
    
    /**
     * Get group profile
     */
    suspend fun getGroupProfile(groupId: UUID): GroupProfile? {
        groupProfiles[groupId]?.let { return it }
        
        return withContext(Dispatchers.IO) {
            try {
                // Would request from server
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
                // Would send join request
                true
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
                // Would send leave request
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to leave group", e)
                false
            }
        }
    }
    
    /**
     * Send friendship request
     */
    suspend fun offerFriendship(agentId: UUID, message: String = ""): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Would send friendship offer
                true
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
                // Would send accept
                true
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
                // Would send decline
                true
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
                // Would send remove
                true
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
