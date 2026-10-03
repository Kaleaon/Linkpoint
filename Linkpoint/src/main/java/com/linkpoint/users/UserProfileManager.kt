package com.linkpoint.users

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import com.linkpoint.protocol.messages.ids.MessageIdRegistry
import com.linkpoint.protocol.messages.UDPConnectionFixed
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * User Profile Manager - Handles avatar profiles (Second Life and Web profiles).
 * 
 * Based on the reference viewer's SLUserProfiles.java
 * 
 * Profiles contain:
 * - Basic info (born date, partner, about text)
 * - Interests (skills, languages, want to)
 * - Picks (favorite places)
 * - Classifieds (advertisements)
 * - First Life info
 * - Notes (personal notes about avatars)
 */
class UserProfileManager(
    private val capabilityManager: CapabilityManager,
    private val udpConnection: UDPConnectionFixed,
    private val agentId: UUID,
    private val capabilityRequest: suspend (String, LLSDValue?) -> LLSDValue? =
        { capabilityName, body -> capabilityManager.request(capabilityName, body) },
    private val udpFallbackRequest: (suspend (UUID) -> Unit)? = null
) {
    companion object {
        private const val TAG = "UserProfileManager"

        // Cache expiry (30 minutes)
        private const val CACHE_EXPIRY_MS = 30 * 60 * 1000L
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Profile cache
    private val profileCache = ConcurrentHashMap<UUID, CachedProfile>()
    
    // Pending requests
    private val pendingRequests = ConcurrentHashMap<UUID, MutableList<ProfileCallback>>()
    
    /**
     * Request an avatar's profile.
     */
    suspend fun requestProfile(
        avatarId: UUID,
        callback: ProfileCallback? = null
    ) {
        // Check cache
        val cached = profileCache[avatarId]
        if (cached != null && !cached.isExpired()) {
            callback?.onProfileLoaded(cached.profile)
            return
        }
        
        // Add callback
        if (callback != null) {
            pendingRequests.getOrPut(avatarId) { mutableListOf() }.add(callback)
        }
        
        // Capability-first flow (Lumiya-style), only fallback to UDP when caps are unavailable/invalid.
        try {
            val cap = capabilityManager.getCapability(CapabilityManager.CAP_AGENT_PROFILE)
            if (cap != null) {
                fetchProfileViaCap(avatarId)
                return
            }
        } catch (e: Exception) {
            Log.w(TAG, "Capability fetch failed, using UDP", e)
        }
        
        // Fallback to UDP
        sendPropertiesRequest(avatarId)
    }
    
    /**
     * Fetch profile via AgentProfile capability.
     */
    private suspend fun fetchProfileViaCap(avatarId: UUID) {
        try {
            // AgentProfile cap takes the *target* avatar id (which the
            // requester is asking about) under the key `avatar_id`, as
            // an LLSDUUID. The previous code sent it as `agent_id` of
            // type LLSDString, which doesn't match the cap's expected
            // wire format and was caught by UserProfileManagerTest.
            val request = LLSDMap().apply {
                this["avatar_id"] = LLSDUUID(avatarId)
            }
            val response = capabilityRequest(CapabilityManager.CAP_AGENT_PROFILE, request)

            if (response is LLSDMap) {
                val profile = parseProfileFromLLSD(avatarId, response)
                cacheProfile(avatarId, profile)
                notifyCallbacks(avatarId, profile)
                val capUrl = capabilityManager.getCapability(CapabilityManager.CAP_AGENT_PROFILE)
                Log.d(TAG, "Loaded profile for $avatarId via AgentProfile capability (${capUrl ?: "unknown"})")
                return
            }

            Log.w(TAG, "AgentProfile capability returned non-map response, falling back to UDP")
            sendPropertiesRequest(avatarId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch profile via capability", e)
            // Fallback to UDP
            sendPropertiesRequest(avatarId)
        }
    }
    
    /**
     * Send AvatarPropertiesRequest via UDP.
     */
    private suspend fun sendPropertiesRequest(avatarId: UUID) {
        udpFallbackRequest?.invoke(avatarId) ?: sendPropertiesRequestInternal(avatarId)
    }

    private suspend fun sendPropertiesRequestInternal(avatarId: UUID) {
        try {
            val payload = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
            
            // AgentData
            writeUUID(payload, agentId)
            writeUUID(payload, udpConnection.getSessionId())
            
            // AvatarData
            writeUUID(payload, avatarId)
            
            udpConnection.sendPacket(MessageIdRegistry.AVATAR_PROPERTIES_REQUEST, payload.array(), reliable = true)
            Log.d(TAG, "Sent AvatarPropertiesRequest for $avatarId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send properties request", e)
        }
    }
    
    /**
     * Handle AvatarPropertiesReply message.
     */
    fun handlePropertiesReply(payload: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
            
            val avatarId = readUUID(buffer)
            val imageId = readUUID(buffer)
            val flImageId = readUUID(buffer)
            val partnerId = readUUID(buffer)
            
            // Read about text
            val aboutLen = buffer.short.toInt() and 0xFFFF
            val aboutBytes = ByteArray(aboutLen)
            buffer.get(aboutBytes)
            val aboutText = String(aboutBytes, Charsets.UTF_8)
            
            // Read FL about text
            val flAboutLen = buffer.get().toInt() and 0xFF
            val flAboutBytes = ByteArray(flAboutLen)
            buffer.get(flAboutBytes)
            val flAboutText = String(flAboutBytes, Charsets.UTF_8)
            
            // Read born on
            val bornLen = buffer.get().toInt() and 0xFF
            val bornBytes = ByteArray(bornLen)
            buffer.get(bornBytes)
            val bornOn = String(bornBytes, Charsets.UTF_8)
            
            // Read profile URL
            val urlLen = buffer.get().toInt() and 0xFF
            val urlBytes = ByteArray(urlLen)
            buffer.get(urlBytes)
            val profileUrl = String(urlBytes, Charsets.UTF_8)
            
            // Flags
            val flags = buffer.int
            
            val profile = UserProfile(
                avatarId = avatarId,
                imageId = imageId,
                firstLifeImageId = flImageId,
                partnerId = partnerId,
                aboutText = aboutText,
                firstLifeAboutText = flAboutText,
                bornOn = bornOn,
                profileUrl = profileUrl,
                flags = flags
            )
            
            cacheProfile(avatarId, profile)
            notifyCallbacks(avatarId, profile)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing AvatarPropertiesReply", e)
        }
    }
    
    /**
     * Request avatar interests.
     */
    suspend fun requestInterests(avatarId: UUID) {
        try {
            val payload = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
            
            writeUUID(payload, agentId)
            writeUUID(payload, udpConnection.getSessionId())
            writeUUID(payload, avatarId)
            
            udpConnection.sendPacket(MessageIdRegistry.AVATAR_INTERESTS_REQUEST, payload.array(), reliable = true)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request interests", e)
        }
    }
    
    /**
     * Request personal notes about an avatar.
     */
    suspend fun requestNotes(avatarId: UUID) {
        try {
            val payload = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
            
            writeUUID(payload, agentId)
            writeUUID(payload, udpConnection.getSessionId())
            writeUUID(payload, avatarId)
            
            udpConnection.sendPacket(MessageIdRegistry.AVATAR_NOTES_REQUEST, payload.array(), reliable = true)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request notes", e)
        }
    }
    
    /**
     * Update personal notes about an avatar.
     */
    suspend fun updateNotes(avatarId: UUID, notes: String) {
        try {
            val notesBytes = notes.toByteArray(Charsets.UTF_8)
            val payload = ByteBuffer.allocate(50 + notesBytes.size).order(ByteOrder.LITTLE_ENDIAN)
            
            writeUUID(payload, agentId)
            writeUUID(payload, udpConnection.getSessionId())
            writeUUID(payload, avatarId)
            payload.putShort(notesBytes.size.toShort())
            payload.put(notesBytes)
            
            udpConnection.sendPacket(MessageIdRegistry.AVATAR_NOTES_UPDATE_REQUEST, payload.array().copyOf(payload.position()), reliable = true)
            Log.d(TAG, "Updated notes for $avatarId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update notes", e)
        }
    }
    
    /**
     * Request avatar picks.
     */
    suspend fun requestPicks(avatarId: UUID) {
        try {
            val payload = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
            
            writeUUID(payload, agentId)
            writeUUID(payload, udpConnection.getSessionId())
            writeUUID(payload, avatarId)
            
            udpConnection.sendPacket(MessageIdRegistry.AVATAR_PICKS_REQUEST, payload.array(), reliable = true)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request picks", e)
        }
    }
    
    /**
     * Parse profile from LLSD (capability response).
     */
    private fun parseProfileFromLLSD(avatarId: UUID, llsd: LLSDMap): UserProfile {
        return UserProfile(
            avatarId = avatarId,
            imageId = llsd.getUUID("sl_image_id") ?: UUID(0, 0),
            firstLifeImageId = llsd.getUUID("fl_image_id") ?: UUID(0, 0),
            partnerId = llsd.getUUID("partner_id") ?: UUID(0, 0),
            aboutText = llsd.getString("sl_about_text") ?: "",
            firstLifeAboutText = llsd.getString("fl_about_text") ?: "",
            bornOn = llsd.getString("born_on") ?: "",
            profileUrl = llsd.getString("profile_url") ?: "",
            flags = llsd.getInt("flags") ?: 0,
            displayName = llsd.getString("display_name"),
            username = llsd.getString("username"),
            accountType = llsd.getString("account_type")
        )
    }
    
    private fun cacheProfile(avatarId: UUID, profile: UserProfile) {
        profileCache[avatarId] = CachedProfile(profile, System.currentTimeMillis())
    }
    
    private fun notifyCallbacks(avatarId: UUID, profile: UserProfile) {
        val callbacks = pendingRequests.remove(avatarId) ?: return
        callbacks.forEach { it.onProfileLoaded(profile) }
    }
    
    private fun readUUID(buffer: ByteBuffer): UUID {
        val msb = buffer.long
        val lsb = buffer.long
        return UUID(msb, lsb)
    }
    
    private fun writeUUID(buffer: ByteBuffer, uuid: UUID) {
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
    }
    
    /**
     * Get cached profile.
     */
    fun getCachedProfile(avatarId: UUID): UserProfile? {
        return profileCache[avatarId]?.profile
    }
    
    /**
     * Clear profile cache.
     */
    fun clearCache() {
        profileCache.clear()
    }
    
    fun shutdown() {
        scope.cancel()
        profileCache.clear()
    }
}

/**
 * User profile data.
 */
data class UserProfile(
    val avatarId: UUID,
    val imageId: UUID,
    val firstLifeImageId: UUID,
    val partnerId: UUID,
    val aboutText: String,
    val firstLifeAboutText: String,
    val bornOn: String,
    val profileUrl: String,
    val flags: Int,
    val displayName: String? = null,
    val username: String? = null,
    val accountType: String? = null,
    val interests: UserInterests? = null,
    val picks: List<ProfilePick> = emptyList(),
    val notes: String = ""
) {
    val isOnline: Boolean get() = (flags and 0x10) != 0
    val allowPublish: Boolean get() = (flags and 0x01) != 0
    val identified: Boolean get() = (flags and 0x02) != 0
    val transacted: Boolean get() = (flags and 0x04) != 0
    val mature: Boolean get() = (flags and 0x08) != 0
}

/**
 * User interests data.
 */
data class UserInterests(
    val wantToMask: Int,
    val wantToText: String,
    val skillsMask: Int,
    val skillsText: String,
    val languagesText: String
)

/**
 * Profile pick (favorite place).
 */
data class ProfilePick(
    val pickId: UUID,
    val name: String,
    val description: String,
    val snapshotId: UUID,
    val parcelId: UUID,
    val simName: String,
    val posGlobal: Triple<Double, Double, Double>
)

/**
 * Cached profile with timestamp.
 */
internal data class CachedProfile(
    val profile: UserProfile,
    val cachedAt: Long
) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() - cachedAt > CACHE_EXPIRY_MS
    }
    
    companion object {
        private const val CACHE_EXPIRY_MS = 30 * 60 * 1000L
    }
}

/**
 * Profile callback.
 */
fun interface ProfileCallback {
    fun onProfileLoaded(profile: UserProfile)
}
