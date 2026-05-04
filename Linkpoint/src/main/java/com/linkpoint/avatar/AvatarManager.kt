package com.linkpoint.avatar

import android.content.Context
import android.util.Log
import com.linkpoint.assets.AnimationManager
import com.linkpoint.assets.AssetCache
import com.linkpoint.assets.MeshManager
import com.linkpoint.assets.TextureManager
import com.linkpoint.inventory.OutfitManager
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
    private var outfitManager: OutfitManager? = null
    
    // Mapping from localId to agentId for terse updates
    // This is populated when we receive full object updates with both IDs
    private val localIdToAgentId = ConcurrentHashMap<Int, UUID>()
    
    // Movement controller for the local avatar
    val movementController: MovementController by lazy {
        MovementController(udpConnection ?: throw IllegalStateException("UDP connection required for movement"))
    }
    
    private val _avatarCount = MutableStateFlow(0)
    val avatarCount: StateFlow<Int> = _avatarCount
    
    /**
     * Set the local agent ID and proactively create the local Avatar entry.
     *
     * The local avatar (with skeleton, animator, baker) must exist as soon as
     * the agent ID is known so that downstream managers — OutfitManager and
     * AppearanceManager in particular — can wire to `myAvatar.baker` during
     * `LinkpointApp.initializeAgentManagers()`. Previously `myAvatar` was
     * only populated when the simulator's first ObjectUpdate for the local
     * agent arrived, which races with manager initialization and on cellular
     * loses outright (the 2026-04-25 Athanasia debug report had Force
     * Refresh Appearance reporting "not logged in" for this reason).
     */
    fun setMyAgentId(agentId: UUID) {
        myAgentId = agentId
        // Pre-create the local avatar so getMyAvatar() is non-null immediately.
        // Position/rotation default to zero and will be overwritten by the
        // first ObjectUpdate / TerseUpdate for this agent.
        val avatar = avatars.getOrPut(agentId) { createAvatar(agentId) }
        myAvatar = avatar
        _avatarCount.value = avatars.size
        // Initialize movement controller with session info
        if (udpConnection != null) {
            movementController.setSessionInfo(agentId, UUID(0, 0)) // Session ID would be set from login
            movementController.startMovementUpdates()
        }
    }

    /**
     * Connect the avatar manager to the outfit system.
     */
    fun setOutfitManager(manager: OutfitManager) {
        outfitManager = manager
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
     * Add or update an avatar with localId for terse update mapping.
     * This should be called when processing full ObjectUpdate messages for avatars.
     */
    fun updateAvatar(
        agentId: UUID,
        localId: Int,
        position: LLVector3,
        rotation: LLQuaternion,
        velocity: LLVector3 = LLVector3.zero()
    ) {
        // Maintain localId -> agentId mapping for terse updates
        localIdToAgentId[localId] = agentId

        // Delegate to the standard update
        updateAvatar(agentId, position, rotation, velocity)

        // Store localId on the avatar for reference
        avatars[agentId]?.localId = localId

        // Replay any terse update that arrived before this mapping landed.
        replayPendingTerseUpdate(localId, agentId)
    }

    /**
     * Register a localId -> agentId mapping (e.g., from ObjectUpdate for avatars).
     * This enables terse updates to update the correct avatar.
     */
    fun registerLocalIdMapping(localId: Int, agentId: UUID) {
        localIdToAgentId[localId] = agentId
        avatars[agentId]?.localId = localId
        Log.d(TAG, "Registered localId mapping: $localId -> $agentId")
        replayPendingTerseUpdate(localId, agentId)
    }
    
    /**
     * Remove an avatar
     */
    fun removeAvatar(agentId: UUID) {
        avatars.remove(agentId)?.let { avatar ->
            avatar.animator.stopAll()
            // Clean up localId mapping if present
            avatar.localId?.let { localId ->
                localIdToAgentId.remove(localId)
            }
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
     * Uses localId -> agentId mapping to update both our own and other avatars.
     *
     * If the localId hasn't been mapped to an agentId yet (terse update beat
     * the initial ObjectUpdate, which happens at region entry), the update
     * is buffered in [pendingTerseUpdates] and replayed in
     * [registerLocalIdMapping] / the localId-aware [updateAvatar] overload
     * when the mapping arrives. Previously the unknown-localId branch
     * silently overwrote `myAvatar` with whatever data came in — which
     * could teleport the user to a remote avatar's position during the
     * brief race window.
     */
    fun handleTerseUpdate(data: TerseUpdateData) {
        if (!data.isAvatar) return

        val agentId = localIdToAgentId[data.localId]
        if (agentId != null) {
            applyTerseUpdate(agentId, data)
            return
        }

        // Self-update fast path: if the terse update's localId matches our
        // own avatar's known localId, it's our position. Otherwise it
        // belongs to another avatar whose ObjectUpdate hasn't landed yet —
        // buffer it for replay rather than dropping or misapplying.
        val mine = myAvatar
        if (mine != null && mine.localId == data.localId) {
            applyTerseUpdate(mine.agentId, data)
            return
        }

        bufferPendingTerseUpdate(data)
    }

    private fun applyTerseUpdate(agentId: UUID, data: TerseUpdateData) {
        val avatar = avatars[agentId] ?: return
        avatar.position = data.position
        avatar.rotation = data.rotation
        avatar.velocity = data.velocity
        avatar.lastUpdate = System.currentTimeMillis()
    }

    /**
     * Per-localId pending terse updates. Bounded entry count, only the
     * most-recent update per localId kept (older deltas are stale once
     * a fresher one arrives). Cleared on shutdown / region change via
     * [clearPendingTerseUpdates].
     */
    private val pendingTerseUpdates = ConcurrentHashMap<Int, TerseUpdateData>()
    private val PENDING_TERSE_LIMIT = 256

    private fun bufferPendingTerseUpdate(data: TerseUpdateData) {
        if (pendingTerseUpdates.size >= PENDING_TERSE_LIMIT &&
            !pendingTerseUpdates.containsKey(data.localId)
        ) {
            // Evict an arbitrary entry to keep the map bounded; a steady
            // stream of unmapped localIds means the producer is well ahead
            // of ObjectUpdate and we can't catch them all.
            val victim = pendingTerseUpdates.keys.firstOrNull()
            if (victim != null) pendingTerseUpdates.remove(victim)
        }
        pendingTerseUpdates[data.localId] = data
    }

    private fun replayPendingTerseUpdate(localId: Int, agentId: UUID) {
        val pending = pendingTerseUpdates.remove(localId) ?: return
        applyTerseUpdate(agentId, pending)
    }

    fun clearPendingTerseUpdates() {
        pendingTerseUpdates.clear()
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
            
            // Reusable buffers for parsing loop
            val uuidBytes = ByteArray(16)
            val agentIdBuffer = java.nio.ByteBuffer.wrap(uuidBytes).order(java.nio.ByteOrder.BIG_ENDIAN)

            for (i in 0 until agentCount) {
                if (buffer.remaining() < 19) break // 16 bytes UUID + 3 bytes position
                
                // Agent ID (16 bytes UUID)
                buffer.get(uuidBytes)
                agentIdBuffer.clear()
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
        val radiusSquared = radius * radius
        return avatars.values.filter { avatar ->
            avatar.position.distanceSquared(position) <= radiusSquared
        }.sortedBy { it.position.distanceSquared(position) }
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
     * Handle incoming avatar appearance data from UDP message.
     */
    fun handleAvatarAppearance(agentId: UUID, textureEntries: ByteArray, visualParams: ByteArray) {
        val avatar = avatars[agentId]
        if (avatar == null) {
            Log.w(TAG, "Received appearance for unknown avatar: $agentId")
            return
        }
        Log.d(TAG, "Updating appearance for avatar $agentId: ${visualParams.size} visual params")
        avatar.visualParams = visualParams
        avatar.textureEntry = textureEntries

        // Parse the TextureEntry blob and pull out the per-bake-channel
        // texture UUIDs. SL's avatar uses 21 face slots (TEX_NUM_INDICES);
        // slots 8..18 are the baked channels in the order
        // head/upper/lower/eyes/skirt/hair/leftarm/leftleg/aux1/aux2/aux3.
        // Surfacing them on the avatar object lets the BoM resolver hand
        // them to mesh attachments that reference IMG_USE_BAKED_*.
        if (textureEntries.isNotEmpty()) {
            val faces = com.linkpoint.protocol.textures.TextureEntryParser.parseFull(textureEntries, 21)
            if (faces != null) {
                val bakes = HashMap<Int, UUID>()
                // SL bake-slot index → TextureEntry face index.
                val slotToFace = mapOf(
                    com.linkpoint.avatar.BakesOnMesh.SLOT_HEAD     to 8,
                    com.linkpoint.avatar.BakesOnMesh.SLOT_UPPER    to 9,
                    com.linkpoint.avatar.BakesOnMesh.SLOT_LOWER    to 10,
                    com.linkpoint.avatar.BakesOnMesh.SLOT_EYES     to 11,
                    com.linkpoint.avatar.BakesOnMesh.SLOT_SKIRT    to 12,
                    com.linkpoint.avatar.BakesOnMesh.SLOT_HAIR     to 13,
                    com.linkpoint.avatar.BakesOnMesh.SLOT_LEFT_ARM to 14,
                    com.linkpoint.avatar.BakesOnMesh.SLOT_LEFT_LEG to 15,
                    com.linkpoint.avatar.BakesOnMesh.SLOT_AUX1     to 16,
                    com.linkpoint.avatar.BakesOnMesh.SLOT_AUX2     to 17,
                    com.linkpoint.avatar.BakesOnMesh.SLOT_AUX3     to 18
                )
                for ((slot, faceIdx) in slotToFace) {
                    val face = faces.getOrNull(faceIdx) ?: continue
                    val tid = face.textureId
                    if (tid != UUID(0L, 0L)) bakes[slot] = tid
                }
                avatar.baker.setExternalBakedTextures(bakes)
                Log.d(TAG, "  Extracted ${bakes.size} baked-texture UUIDs for $agentId")
            }
        }
    }
    
    /**
     * Handle incoming wearables update from UDP message.
     */
    fun handleWearablesUpdate(wearables: List<com.linkpoint.protocol.messages.AdditionalMessageParsers.WearableData>) {
        Log.d(TAG, "Received wearables update: ${wearables.size} items")
        wearables.forEach { wearable ->
            Log.d(TAG, "  Wearable type ${wearable.wearableType}: item=${wearable.itemID}, asset=${wearable.assetID}")
        }

        // Apply each wearable through OutfitManager so the asset is fetched,
        // parsed, and handed to AvatarBaker. Without this the bakes never
        // get fresh layer data and `sendAgentSetAppearance` would push stale
        // (or default) baked-texture UUIDs.
        val manager = outfitManager
        if (manager == null) {
            Log.w(TAG, "OutfitManager not configured; AgentWearablesUpdate dropped on the floor")
            return
        }
        appearanceScope.launch {
            try {
                wearables.forEach { wearable ->
                    if (wearable.itemID != UUID(0L, 0L)) {
                        try {
                            manager.wearItem(wearable.itemID, replace = false)
                        } catch (e: Exception) {
                            Log.w(TAG, "wearItem(${wearable.itemID}) failed: ${e.message}")
                        }
                    }
                }
                // Once all wearables are queued, push the resulting bakes
                // up to the simulator. The AppearanceManager handles its
                // own bake → upload → AgentSetAppearance flow.
                appearanceUpdateTrigger?.invoke()
            } catch (e: Exception) {
                Log.w(TAG, "wearables update apply failed", e)
            }
        }
    }

    /**
     * Optional callback invoked after AgentWearablesUpdate has been
     * applied to OutfitManager. Wired by LinkpointApp to call
     * AppearanceManager.sendAppearanceUpdate() so the local agent's
     * bake gets re-uploaded automatically.
     */
    @Volatile
    var appearanceUpdateTrigger: (suspend () -> Unit)? = null

    private val appearanceScope = kotlinx.coroutines.CoroutineScope(
        kotlinx.coroutines.Dispatchers.Default + kotlinx.coroutines.SupervisorJob()
    )
    
    /**
     * Handle AvatarSitResponse from server.
     */
    fun handleSitResponse(data: com.linkpoint.protocol.messages.AdditionalMessageParsers.AvatarSitResponseData) {
        val avatar = myAvatar
        if (avatar != null) {
            avatar.isSitting = true
            avatar.position = data.sitPosition
            Log.d(TAG, "Agent is now sitting on ${data.sitObjectID} at ${data.sitPosition}")
        }
    }
    
    /**
     * Get wearables of a specific type from the current avatar's outfit
     */
    suspend fun getWearables(type: WearableType): List<Wearable> {
        val manager = outfitManager
        if (manager == null) {
            Log.w(TAG, "OutfitManager not configured; cannot resolve wearables")
            return emptyList()
        }

        val wornItemId = manager.getWornWearable(type) ?: return emptyList()
        val item = manager.getInventoryItem(wornItemId)
        if (item == null) {
            Log.w(TAG, "No inventory item found for worn wearable $wornItemId")
            return emptyList()
        }

        return listOf(
            Wearable.fromInventoryItem(
                itemId = item.itemId,
                assetId = item.assetId,
                name = item.name,
                description = item.description,
                wearableType = type,
                isWorn = true
            )
        )
    }
    
    /**
     * Wear a specific wearable item
     */
    suspend fun wear(wearable: Wearable) {
        val manager = outfitManager
        if (manager == null) {
            Log.w(TAG, "OutfitManager not configured; cannot wear ${wearable.name}")
            return
        }
        manager.wearItem(wearable.itemId, replace = true)
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
    var textureEntry: ByteArray? = null
    var lastUpdate: Long = 0
    
    // LocalId for terse update mapping (set from ObjectUpdate messages)
    var localId: Int? = null
    
    // Profile data
    var displayName: String? = null
    var userName: String? = null
    var groupTitle: String? = null
    
    // State
    var isFlying: Boolean = false
    var isSitting: Boolean = false
    var isTyping: Boolean = false
}
