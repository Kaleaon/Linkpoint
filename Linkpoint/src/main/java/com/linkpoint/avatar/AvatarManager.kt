package com.linkpoint.avatar

import android.content.Context
import android.util.Log
import com.linkpoint.assets.AnimationManager
import com.linkpoint.assets.AssetCache
import com.linkpoint.assets.MeshManager
import com.linkpoint.assets.TextureManager
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.messages.AvatarAnimationData
import com.linkpoint.protocol.messages.TerseUpdateData
import com.linkpoint.protocol.messages.UDPConnectionFixed
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
    private val capabilityManager: CapabilityManager,
    private val udpConnection: UDPConnectionFixed? = null
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
    
    // Movement controller for the local avatar
    val movementController: MovementController by lazy {
        MovementController(udpConnection ?: throw IllegalStateException("UDP connection required for movement"))
    }
    
    private val _avatarCount = MutableStateFlow(0)
    val avatarCount: StateFlow<Int> = _avatarCount
    
    /**
     * Set the local agent ID
     */
    fun setMyAgentId(agentId: UUID) {
        myAgentId = agentId
        // Initialize movement controller with session info
        if (udpConnection != null) {
            movementController.setSessionInfo(agentId, UUID(0, 0)) // Session ID would be set from login
            movementController.startMovementUpdates()
        }
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
     * Handle terse position update from ImprovedTerseObjectUpdate message.
     * This is used for fast position updates for avatars (when isAvatar=true).
     * Currently only updates our own avatar position.
     * TODO: Maintain a localId -> agentId mapping to update other avatars.
     */
    fun handleTerseUpdate(data: TerseUpdateData) {
        // TerseUpdate uses localId - in a full implementation, we'd maintain
        // a localId -> agentId mapping to update any avatar. For now, only
        // update our own avatar when receiving avatar terse updates.
        if (data.isAvatar && myAvatar != null) {
            myAvatar?.let { avatar ->
                avatar.position = data.position
                avatar.rotation = data.rotation
                avatar.velocity = data.velocity
                avatar.lastUpdate = System.currentTimeMillis()
                Log.d(TAG, "TerseUpdate: updated myAvatar, localId=${data.localId}, pos=${data.position}")
            }
        }
        // Note: other avatar terse updates are not processed until localId->agentId mapping is implemented
    }
    
    /**
     * Handle CoarseLocationUpdate message.
     * This provides rough positions for all avatars in the region.
     * Format:
     * - You block: 2 bytes (index of our agent in the list)
     * - Prey block: 2 bytes (index of prey agent, or -1)
     * - AgentData count: 1 byte
     * - AgentData block (variable): AgentID (UUID), X (U8), Y (U8), Z (U8) = 19 bytes each
     */
    fun handleCoarseLocationUpdate(payload: ByteArray) {
        // Minimum size: You (2) + Prey (2) + count (1) = 5 bytes
        // With at least 1 agent: 5 + 19 = 24 bytes
        if (payload.size < 5) return
        
        try {
            val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            
            // You block - index of our agent in the list (2 bytes, signed short, -1 if not in list)
            val youIndex = buffer.short.toInt()
            
            // Prey block - index of prey agent (2 bytes, signed short, -1 if no prey)
            val preyIndex = buffer.short.toInt()
            
            // AgentData blocks - count is 1 byte
            val agentCount = buffer.get().toInt() and 0xFF
            
            Log.d(TAG, "CoarseLocationUpdate: $agentCount agents, youIndex=$youIndex")
            
            for (i in 0 until agentCount) {
                if (buffer.remaining() < 19) break // 16 bytes UUID + 3 bytes position
                
                // Agent ID (16 bytes UUID)
                val uuidBytes = ByteArray(16)
                buffer.get(uuidBytes)
                val agentIdBuffer = java.nio.ByteBuffer.wrap(uuidBytes).order(java.nio.ByteOrder.BIG_ENDIAN)
                val agentId = UUID(agentIdBuffer.long, agentIdBuffer.long)
                
                // Position in region (X, Y, Z as bytes - each represents 0-255 in region coords)
                val x = (buffer.get().toInt() and 0xFF).toFloat()
                val y = (buffer.get().toInt() and 0xFF).toFloat()
                val z = (buffer.get().toInt() and 0xFF).toFloat()
                
                val position = LLVector3(x, y, z)
                
                // Update or create avatar entry with coarse position
                // Skip zero UUID (invalid/null UUID)
                val isValidUUID = agentId != UUID(0L, 0L)
                if (isValidUUID) {
                    val avatar = avatars.getOrPut(agentId) {
                        createAvatar(agentId)
                    }
                    avatar.position = position
                    avatar.lastUpdate = System.currentTimeMillis()
                    
                    if (agentId == myAgentId) {
                        myAvatar = avatar
                    }
                }
            }
            
            _avatarCount.value = avatars.size
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing CoarseLocationUpdate", e)
        }
    }
    
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
    
    /**
     * Get wearables of a specific type from the current avatar's outfit
     * Stub implementation - would need full outfit manager integration
     */
    suspend fun getWearables(type: WearableType): List<Wearable> {
        // This is a stub - in a full implementation, this would fetch
        // wearables from the outfit manager or inventory
        return emptyList()
    }
    
    /**
     * Wear a specific wearable item
     * Stub implementation - would need full outfit manager integration
     */
    suspend fun wear(wearable: Wearable) {
        // This is a stub - in a full implementation, this would
        // trigger the outfit manager to wear the item
        android.util.Log.i("AvatarManager", "Wear: ${wearable.name}")
    }
    
    // ==================== AGENT HEALTH ====================
    
    private var _agentHealth: Float = 100f
    val agentHealth: Float get() = _agentHealth
    
    /**
     * Update agent health from HealthMessage
     */
    fun updateAgentHealth(health: Float) {
        _agentHealth = health.coerceIn(0f, 100f)
        android.util.Log.d("AvatarManager", "Agent health updated: $_agentHealth%")
    }
    
    fun shutdown() {
        scope.cancel()
        if (udpConnection != null) {
            movementController.shutdown()
        }
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
