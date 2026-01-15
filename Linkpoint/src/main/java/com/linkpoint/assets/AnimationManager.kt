package com.linkpoint.assets

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages animation assets
 * Handles BVH-based Second Life animations
 */
class AnimationManager(
    private val context: Context,
    private val cache: AssetCache
) {
    companion object {
        private const val TAG = "AnimationManager"
        
        // Built-in animation UUIDs
        val ANIM_WALK = UUID.fromString("6ed24bd8-91aa-4b12-ccc7-c97c857ab4e0")
        val ANIM_RUN = UUID.fromString("05ddbff8-aaa9-92a1-2b74-8fe77a29b445")
        val ANIM_STAND = UUID.fromString("2408fe9e-df1d-1d7d-f4ff-1384fa7b350f")
        val ANIM_SIT = UUID.fromString("1a5fe8ac-a804-8a5d-7f59-5c805d0d9ebc")
        val ANIM_CROUCH = UUID.fromString("201f3fdf-cb1f-dbec-201f-7333e328ae7c")
        val ANIM_FLY = UUID.fromString("aec4610c-757f-bc4e-c092-c6e9caf18daf")
        val ANIM_TURN_LEFT = UUID.fromString("56e0ba0d-4a9f-7f27-6117-32f2ebbf6135")
        val ANIM_TURN_RIGHT = UUID.fromString("2d6daa51-3192-6794-8e2e-a15f8338ec30")
    }
    
    private val loadedAnimations = ConcurrentHashMap<UUID, AnimationData>()
    private val builtInAnimations = ConcurrentHashMap<UUID, AnimationData>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    init {
        // Load built-in animations from assets
        loadBuiltInAnimations()
    }
    
    private fun loadBuiltInAnimations() {
        scope.launch {
            try {
                val animFiles = context.assets.list("anims") ?: return@launch
                
                for (file in animFiles) {
                    try {
                        val inputStream = context.assets.open("anims/$file")
                        val data = inputStream.readBytes()
                        inputStream.close()
                        
                        val anim = parseAnimation(UUID.randomUUID(), data)
                        if (anim != null) {
                            builtInAnimations[anim.animId] = anim
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to load built-in animation: $file", e)
                    }
                }
                
                Log.i(TAG, "Loaded ${builtInAnimations.size} built-in animations")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load built-in animations", e)
            }
        }
    }
    
    /**
     * Get animation data
     */
    suspend fun getAnimation(animId: UUID): AnimationData? {
        // Check loaded
        loadedAnimations[animId]?.let { return it }
        
        // Check built-in
        builtInAnimations[animId]?.let { return it }
        
        // Check cache
        cache.get(animId, AssetType.ANIMATION)?.let { data ->
            val anim = parseAnimation(animId, data)
            if (anim != null) {
                loadedAnimations[animId] = anim
            }
            return anim
        }
        
        return null
    }
    
    /**
     * Parse animation data
     */
    private fun parseAnimation(animId: UUID, data: ByteArray): AnimationData? {
        try {
            val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
            
            // Header
            val version = buffer.short.toInt()
            val subVersion = buffer.short.toInt()
            
            // Priority
            val priority = buffer.int
            
            // Duration
            val duration = buffer.float
            
            // Emote name
            val emoteNameLen = buffer.get().toInt() and 0xFF
            val emoteNameBytes = ByteArray(emoteNameLen)
            buffer.get(emoteNameBytes)
            val emoteName = String(emoteNameBytes, Charsets.UTF_8)
            
            // Loop
            val loopIn = buffer.float
            val loopOut = buffer.float
            val loop = buffer.int
            
            // Ease in/out
            val easeIn = buffer.float
            val easeOut = buffer.float
            
            // Hand pose
            val handPose = buffer.int
            
            // Joint count
            val jointCount = buffer.int
            
            val joints = mutableListOf<JointAnimation>()
            
            for (i in 0 until jointCount) {
                // Joint name
                val jointNameLen = buffer.get().toInt() and 0xFF
                val jointNameBytes = ByteArray(jointNameLen)
                buffer.get(jointNameBytes)
                val jointName = String(jointNameBytes, Charsets.UTF_8).trimEnd('\u0000')
                
                // Priority for this joint
                val jointPriority = buffer.int
                
                // Rotation keys
                val rotKeyCount = buffer.int
                val rotKeys = mutableListOf<AnimationKey>()
                for (j in 0 until rotKeyCount) {
                    val time = buffer.short.toInt() / 32767f * duration
                    val rx = buffer.short / 32767f * 2f - 1f
                    val ry = buffer.short / 32767f * 2f - 1f
                    val rz = buffer.short / 32767f * 2f - 1f
                    rotKeys.add(AnimationKey(time, floatArrayOf(rx, ry, rz, 1f)))
                }
                
                // Position keys
                val posKeyCount = buffer.int
                val posKeys = mutableListOf<AnimationKey>()
                for (j in 0 until posKeyCount) {
                    val time = buffer.short.toInt() / 32767f * duration
                    val px = buffer.short / 32767f * 5f
                    val py = buffer.short / 32767f * 5f
                    val pz = buffer.short / 32767f * 5f
                    posKeys.add(AnimationKey(time, floatArrayOf(px, py, pz)))
                }
                
                joints.add(JointAnimation(
                    jointName = jointName,
                    priority = jointPriority,
                    rotationKeys = rotKeys,
                    positionKeys = posKeys
                ))
            }
            
            // Constraints (optional)
            val constraints = mutableListOf<AnimationConstraint>()
            if (buffer.hasRemaining()) {
                val constraintCount = buffer.int
                for (i in 0 until constraintCount) {
                    // Parse constraint...
                    val chainLen = buffer.get().toInt() and 0xFF
                    // Skip constraint data for now
                    if (buffer.remaining() >= chainLen * 24) {
                        buffer.position(buffer.position() + chainLen * 24)
                    }
                }
            }
            
            return AnimationData(
                animId = animId,
                version = version,
                priority = priority,
                duration = duration,
                emoteName = emoteName,
                loopIn = loopIn,
                loopOut = loopOut,
                loop = loop != 0,
                easeIn = easeIn,
                easeOut = easeOut,
                handPose = handPose,
                joints = joints,
                constraints = constraints
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse animation: $animId", e)
            return null
        }
    }
    
    fun shutdown() {
        scope.cancel()
        loadedAnimations.clear()
    }
    
    // ==================== DIAGNOSTIC METHODS ====================
    
    // Tracking for diagnostics
    private var loadAttempts = java.util.concurrent.atomic.AtomicInteger(0)
    private var loadFailures = java.util.concurrent.atomic.AtomicInteger(0)
    private var parseFailures = java.util.concurrent.atomic.AtomicInteger(0)
    private var lastError: String? = null
    private var lastErrorTime: Long = 0
    
    /**
     * Get comprehensive diagnostic data for debug reports
     */
    fun getDiagnostics(): AnimationManagerDiagnostics {
        return AnimationManagerDiagnostics(
            loadedAnimationCount = loadedAnimations.size,
            builtInAnimationCount = builtInAnimations.size,
            loadAttempts = loadAttempts.get(),
            loadFailures = loadFailures.get(),
            parseFailures = parseFailures.get(),
            lastError = lastError,
            lastErrorTimeAgo = if (lastErrorTime > 0) System.currentTimeMillis() - lastErrorTime else null
        )
    }
    
    /**
     * Diagnostic data class for animation manager state
     */
    data class AnimationManagerDiagnostics(
        val loadedAnimationCount: Int,
        val builtInAnimationCount: Int,
        val loadAttempts: Int,
        val loadFailures: Int,
        val parseFailures: Int,
        val lastError: String?,
        val lastErrorTimeAgo: Long?
    )

data class AnimationData(
    val animId: UUID,
    val version: Int,
    val priority: Int,
    val duration: Float,
    val emoteName: String,
    val loopIn: Float,
    val loopOut: Float,
    val loop: Boolean,
    val easeIn: Float,
    val easeOut: Float,
    val handPose: Int,
    val joints: List<JointAnimation>,
    val constraints: List<AnimationConstraint>
)

data class JointAnimation(
    val jointName: String,
    val priority: Int,
    val rotationKeys: List<AnimationKey>,
    val positionKeys: List<AnimationKey>
)

data class AnimationKey(
    val time: Float,
    val value: FloatArray
)

data class AnimationConstraint(
    val chainLength: Int,
    val constraint: Int,
    val sourceJoint: String,
    val targetJoint: String,
    val targetOffset: FloatArray
)
