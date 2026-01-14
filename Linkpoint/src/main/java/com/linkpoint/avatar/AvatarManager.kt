package com.linkpoint.avatar

import android.content.Context
import android.util.Log
import com.linkpoint.assets.AnimationManager
import com.linkpoint.assets.AssetCache
import com.linkpoint.assets.MeshManager
import com.linkpoint.assets.TextureManager
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.messages.AvatarAnimationData
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages all avatars in the scene
 */
class AvatarManager(
    private val context: Context,
    private val meshManager: MeshManager,
    private val textureManager: TextureManager,
    private val animationManager: AnimationManager,
    private val capabilityManager: CapabilityManager
) {
    companion object {
        private const val TAG = "AvatarManager"
        private const val MAX_AVATARS = 100
        
        // Diagnostic threshold for "recently updated" avatars (5 seconds)
        private const val RECENT_UPDATE_THRESHOLD_MS = 5000L
    }
    
    private val avatars = ConcurrentHashMap<UUID, Avatar>()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Local agent
    private var myAgentId: UUID? = null
    private var myAvatar: Avatar? = null
    
    private val _avatarCount = MutableStateFlow(0)
    val avatarCount: StateFlow<Int> = _avatarCount
    
    /**
     * Set the local agent ID
     */
    fun setMyAgentId(agentId: UUID) {
        myAgentId = agentId
    }
    
    /**
     * Add or update an avatar
     */
    fun updateAvatar(
        agentId: UUID,
        position: LLVector3,
        rotation: LLQuaternion,
        velocity: LLVector3 = LLVector3.zero()
    ) {
        val avatar = avatars.getOrPut(agentId) {
            createAvatar(agentId)
        }
        
        avatar.position = position
        avatar.rotation = rotation
        avatar.velocity = velocity
        avatar.lastUpdate = System.currentTimeMillis()
        
        if (agentId == myAgentId) {
            myAvatar = avatar
        }
        
        _avatarCount.value = avatars.size
    }
    
    /**
     * Remove an avatar
     */
    fun removeAvatar(agentId: UUID) {
        avatars.remove(agentId)?.let { avatar ->
            avatar.animator.stopAll()
        }
        _avatarCount.value = avatars.size
    }
    
    /**
     * Handle animation update from simulator
     */
    fun handleAnimationUpdate(data: AvatarAnimationData) {
        val avatar = avatars[data.agentId] ?: return
        
        scope.launch {
            // Stop animations not in the new list
            val newAnimIds = data.animations.map { it.first }.toSet()
            avatar.animator.getPlayingAnimations().forEach { animId ->
                if (animId !in newAnimIds) {
                    avatar.animator.stopAnimation(animId)
                }
            }
            
            // Start new animations
            for ((animId, _) in data.animations) {
                if (!avatar.animator.isPlaying(animId)) {
                    avatar.animator.startAnimation(animId, loop = true)
                }
            }
        }
    }
    
    /**
     * Alias for handleAnimationUpdate - handles AvatarAnimation UDP messages
     */
    fun handleAvatarAnimation(data: AvatarAnimationData) = handleAnimationUpdate(data)
    
    /**
     * Update all avatars
     */
    fun update(deltaTime: Float) {
        for (avatar in avatars.values) {
            // Update animation
            avatar.animator.update(deltaTime)
            
            // Interpolate position
            if (avatar.velocity.length() > 0.01f) {
                avatar.position = avatar.position + avatar.velocity * deltaTime
            }
        }
    }
    
    /**
     * Get avatar by ID
     */
    fun getAvatar(agentId: UUID): Avatar? = avatars[agentId]
    
    /**
     * Get my avatar
     */
    fun getMyAvatar(): Avatar? = myAvatar
    
    /**
     * Get all nearby avatars
     */
    fun getNearbyAvatars(position: LLVector3, radius: Float): List<Avatar> {
        return avatars.values.filter { avatar ->
            avatar.position.distance(position) <= radius
        }.sortedBy { it.position.distance(position) }
    }
    
    /**
     * Get all avatars
     */
    fun getAllAvatars(): Collection<Avatar> = avatars.values
    
    private fun createAvatar(agentId: UUID): Avatar {
        val skeleton = AvatarSkeleton(context)
        val animator = AvatarAnimator(skeleton, animationManager)
        val baker = AvatarBaker(context, textureManager, capabilityManager)
        
        return Avatar(
            agentId = agentId,
            skeleton = skeleton,
            animator = animator,
            baker = baker
        )
    }
    
    /**
     * Update avatar appearance
     */
    fun setAvatarAppearance(
        agentId: UUID,
        wearables: List<WearableData>,
        visualParams: ByteArray?
    ) {
        val avatar = avatars[agentId] ?: return
        
        // Update wearables
        for (wearable in wearables) {
            avatar.baker.setWearable(wearable.type, wearable)
        }
        
        // Update visual params (shape, etc)
        if (visualParams != null) {
            avatar.visualParams = visualParams
            applyVisualParams(avatar)
        }
        
        // Trigger rebake
        scope.launch {
            avatar.baker.bakeAll()
        }
    }
    
    private fun applyVisualParams(avatar: Avatar) {
        val params = avatar.visualParams ?: return
        
        // Visual params affect bone positions and scales
        // This is a simplified version - full implementation would
        // parse the complete visual params data
        
        if (params.size >= 218) {
            // Height (param 33)
            val heightParam = params[33].toInt() and 0xFF
            val heightScale = 0.8f + (heightParam / 255f) * 0.4f
            
            avatar.skeleton.getBone("mPelvis")?.let { pelvis ->
                pelvis.scale = LLVector3(1f, 1f, heightScale)
            }
        }
        
        avatar.skeleton.updateBoneMatrices()
    }
    
    fun shutdown() {
        scope.cancel()
        avatars.values.forEach { avatar ->
            avatar.animator.stopAll()
            avatar.baker.shutdown()
        }
        avatars.clear()
    }
    
    // ==================== DIAGNOSTIC METHODS ====================
    
    /**
     * Get comprehensive diagnostic data for debug reports
     */
    fun getDiagnostics(): AvatarManagerDiagnostics {
        val allAvatars = avatars.values.toList()
        val now = System.currentTimeMillis()
        
        val recentlyUpdated = allAvatars.count { now - it.lastUpdate < RECENT_UPDATE_THRESHOLD_MS }
        val flyingCount = allAvatars.count { it.isFlying }
        val sittingCount = allAvatars.count { it.isSitting }
        val typingCount = allAvatars.count { it.isTyping }
        
        return AvatarManagerDiagnostics(
            totalAvatars = avatars.size,
            myAgentId = myAgentId,
            myAvatarLoaded = myAvatar != null,
            recentlyUpdatedCount = recentlyUpdated,
            flyingCount = flyingCount,
            sittingCount = sittingCount,
            typingCount = typingCount
        )
    }
    
    /**
     * Diagnostic data class for avatar manager state
     */
    data class AvatarManagerDiagnostics(
        val totalAvatars: Int,
        val myAgentId: UUID?,
        val myAvatarLoaded: Boolean,
        val recentlyUpdatedCount: Int,
        val flyingCount: Int,
        val sittingCount: Int,
        val typingCount: Int
    )
}

class Avatar(
    val agentId: UUID,
    val skeleton: AvatarSkeleton,
    val animator: AvatarAnimator,
    val baker: AvatarBaker
) {
    var position: LLVector3 = LLVector3.zero()
    var rotation: LLQuaternion = LLQuaternion.identity()
    var velocity: LLVector3 = LLVector3.zero()
    var visualParams: ByteArray? = null
    var lastUpdate: Long = 0
    
    // Profile data
    var displayName: String? = null
    var userName: String? = null
    var groupTitle: String? = null
    
    // State
    var isFlying: Boolean = false
    var isSitting: Boolean = false
    var isTyping: Boolean = false
}
