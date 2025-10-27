package com.linkpoint.animation

import com.linkpoint.Debug
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.assets.AssetManager
import com.linkpoint.assets.AssetType
import kotlinx.coroutines.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * AnimationPriority - Animation playback priority
 */
enum class AnimationPriority(val value: Int) {
    LOW(0),
    NORMAL(1),
    HIGH(2),
    HIGHEST(3)
    
    companion object {
        fun fromValue(value: Int) = values().find { it.value == value } ?: NORMAL
    }
}

/**
 * JointMotion - Motion data for a single joint
 */
data class JointMotion(
    val jointName: String,
    val jointPriority: Int,
    val positionKeys: List<PositionKey>,
    val rotationKeys: List<RotationKey>
)

/**
 * PositionKey - Position keyframe
 */
data class PositionKey(
    val time: Float,
    val position: LLVector3
)

/**
 * RotationKey - Rotation keyframe
 */
data class RotationKey(
    val time: Float,
    val rotation: LLQuaternion
)

/**
 * Animation - Represents an animation asset
 */
data class Animation(
    val animationID: UUID,
    val name: String,
    val duration: Float,
    val loopIn: Float,
    val loopOut: Float,
    val loop: Boolean,
    val easeIn: Float,
    val easeOut: Float,
    val handPose: Int,
    val priority: AnimationPriority,
    val joints: List<JointMotion>
)

/**
 * PlayingAnimation - Instance of a playing animation
 */
class PlayingAnimation(
    val animation: Animation,
    val startTime: Float,
    val weight: Float = 1.0f,
    val speedMultiplier: Float = 1.0f
) {
    var currentTime: Float = 0f
    var isActive: Boolean = true
    var fadeIn: Float = 0f
    var fadeOut: Float = 0f
    
    /**
     * Get interpolated pose at current time
     */
    fun getPose(joints: Map<String, JointState>): Map<String, JointState> {
        val result = mutableMapOf<String, JointState>()
        
        for (jointMotion in animation.joints) {
            val jointName = jointMotion.jointName
            val baseState = joints[jointName] ?: continue
            
            // Interpolate position
            val position = interpolatePosition(jointMotion, currentTime)
            
            // Interpolate rotation
            val rotation = interpolateRotation(jointMotion, currentTime)
            
            result[jointName] = JointState(
                position = position ?: baseState.position,
                rotation = rotation ?: baseState.rotation
            )
        }
        
        return result
    }
    
    /**
     * Interpolate position at given time
     */
    private fun interpolatePosition(jointMotion: JointMotion, time: Float): LLVector3? {
        val keys = jointMotion.positionKeys
        if (keys.isEmpty()) return null
        
        // Find surrounding keyframes
        var key1: PositionKey? = null
        var key2: PositionKey? = null
        
        for (i in keys.indices) {
            if (keys[i].time <= time) {
                key1 = keys[i]
            }
            if (keys[i].time >= time) {
                key2 = keys[i]
                break
            }
        }
        
        return when {
            key1 == null && key2 == null -> null
            key1 == null -> key2!!.position
            key2 == null -> key1.position
            key1.time == key2.time -> key1.position
            else -> {
                // Linear interpolation
                val t = (time - key1.time) / (key2.time - key1.time)
                key1.position + (key2.position - key1.position) * t
            }
        }
    }
    
    /**
     * Interpolate rotation at given time
     */
    private fun interpolateRotation(jointMotion: JointMotion, time: Float): LLQuaternion? {
        val keys = jointMotion.rotationKeys
        if (keys.isEmpty()) return null
        
        // Find surrounding keyframes
        var key1: RotationKey? = null
        var key2: RotationKey? = null
        
        for (i in keys.indices) {
            if (keys[i].time <= time) {
                key1 = keys[i]
            }
            if (keys[i].time >= time) {
                key2 = keys[i]
                break
            }
        }
        
        return when {
            key1 == null && key2 == null -> null
            key1 == null -> key2!!.rotation
            key2 == null -> key1.rotation
            key1.time == key2.time -> key1.rotation
            else -> {
                // Spherical linear interpolation (slerp)
                val t = (time - key1.time) / (key2.time - key1.time)
                key1.rotation.slerp(t, key2.rotation)
            }
        }
    }
    
    /**
     * Update animation time
     */
    fun update(deltaTime: Float) {
        currentTime += deltaTime * speedMultiplier
        
        // Handle looping
        if (animation.loop) {
            if (currentTime > animation.loopOut) {
                currentTime = animation.loopIn + (currentTime - animation.loopOut)
            }
        } else {
            if (currentTime >= animation.duration) {
                isActive = false
            }
        }
    }
}

/**
 * JointState - Current state of a joint
 */
data class JointState(
    var position: LLVector3,
    var rotation: LLQuaternion
)

/**
 * AnimationSystem - Manages avatar animations
 * 
 * Features:
 * - Animation loading from assets
 * - Animation playback
 * - Animation blending
 * - Priority system
 * - Looping and transitions
 * 
 * Based on Firestorm's LLKeyframeMotion
 */
class AnimationSystem(
    private val assetManager: AssetManager
) {
    
    companion object {
        const val MAX_ACTIVE_ANIMATIONS = 32
        const val DEFAULT_FPS = 30f
        
        // Built-in animation UUIDs (from Second Life)
        val ANIM_STAND = UUID.fromString("2408fe9e-df1d-1d7d-f4ff-1384fa7b350f")
        val ANIM_WALK = UUID.fromString("6ed24bd8-91aa-4b12-ccc7-c97c857ab4e0")
        val ANIM_RUN = UUID.fromString("05ddbff8-aaa9-92a1-2b74-8fe77a29b445")
        val ANIM_SIT = UUID.fromString("1a5fe8ac-a804-8a5d-7cbd-56bd83184568")
        val ANIM_JUMP = UUID.fromString("2305bd75-1ca9-b03b-1faa-b176b8a8c49e")
        val ANIM_FLY = UUID.fromString("aec4610c-757f-bc4e-c092-c6e9caf18daf")
    }
    
    // Animation cache
    private val animationCache = ConcurrentHashMap<UUID, Animation>()
    
    // Currently playing animations
    private val playingAnimations = mutableListOf<PlayingAnimation>()
    
    // Current joint states
    private val currentJointStates = mutableMapOf<String, JointState>()
    
    // Last update time
    private var lastUpdateTime = System.nanoTime()
    
    /**
     * Load animation from asset
     */
    suspend fun loadAnimation(animationID: UUID): Animation? = withContext(Dispatchers.IO) {
        // Check cache
        animationCache[animationID]?.let { return@withContext it }
        
        try {
            // Download animation asset
            val data = assetManager.downloadAsset(animationID, AssetType.ANIMATION)
            if (data == null) {
                Debug.Log("Failed to download animation: $animationID")
                return@withContext null
            }
            
            // Parse animation
            val animation = parseAnimation(animationID, data)
            if (animation != null) {
                animationCache[animationID] = animation
            }
            
            animation
        } catch (e: Exception) {
            Debug.Log("Failed to load animation: ${e.message}")
            null
        }
    }
    
    /**
     * Parse animation from binary data
     */
    private fun parseAnimation(animationID: UUID, data: ByteArray): Animation? {
        try {
            val buffer = ByteBuffer.wrap(data)
            buffer.order(ByteOrder.LITTLE_ENDIAN)
            
            // Read header
            val version = buffer.getShort()
            if (version != 1.toShort()) {
                Debug.Log("Unsupported animation version: $version")
                return null
            }
            
            val subVersion = buffer.getShort()
            val priority = buffer.getInt()
            val duration = buffer.getFloat()
            
            // Read emote name
            val emoteNameLength = buffer.getInt()
            val emoteNameBytes = ByteArray(emoteNameLength)
            buffer.get(emoteNameBytes)
            val emoteName = String(emoteNameBytes)
            
            // Read loop info
            val loopIn = buffer.getFloat()
            val loopOut = buffer.getFloat()
            val loop = buffer.getInt() != 0
            
            // Read ease in/out
            val easeIn = buffer.getFloat()
            val easeOut = buffer.getFloat()
            
            // Read hand pose
            val handPose = buffer.getInt()
            
            // Read joint count
            val jointCount = buffer.getInt()
            val joints = mutableListOf<JointMotion>()
            
            // Read each joint
            for (i in 0 until jointCount) {
                // Read joint name
                val jointNameLength = buffer.getInt()
                val jointNameBytes = ByteArray(jointNameLength)
                buffer.get(jointNameBytes)
                val jointName = String(jointNameBytes)
                
                // Read joint priority
                val jointPriority = buffer.getInt()
                
                // Read rotation keys
                val rotationKeyCount = buffer.getInt()
                val rotationKeys = mutableListOf<RotationKey>()
                for (j in 0 until rotationKeyCount) {
                    val time = buffer.getFloat()
                    val x = buffer.getFloat()
                    val y = buffer.getFloat()
                    val z = buffer.getFloat()
                    // Quaternion is stored as 3 components, compute w
                    val w = kotlin.math.sqrt(1.0f - (x*x + y*y + z*z))
                    rotationKeys.add(RotationKey(time, LLQuaternion(x, y, z, w)))
                }
                
                // Read position keys
                val positionKeyCount = buffer.getInt()
                val positionKeys = mutableListOf<PositionKey>()
                for (j in 0 until positionKeyCount) {
                    val time = buffer.getFloat()
                    val x = buffer.getFloat()
                    val y = buffer.getFloat()
                    val z = buffer.getFloat()
                    positionKeys.add(PositionKey(time, LLVector3(x, y, z)))
                }
                
                joints.add(JointMotion(jointName, jointPriority, positionKeys, rotationKeys))
            }
            
            return Animation(
                animationID = animationID,
                name = emoteName,
                duration = duration,
                loopIn = loopIn,
                loopOut = loopOut,
                loop = loop,
                easeIn = easeIn,
                easeOut = easeOut,
                handPose = handPose,
                priority = AnimationPriority.fromValue(priority),
                joints = joints
            )
            
        } catch (e: Exception) {
            Debug.Log("Failed to parse animation: ${e.message}")
            return null
        }
    }
    
    /**
     * Play animation
     */
    suspend fun playAnimation(
        animationID: UUID,
        weight: Float = 1.0f,
        speedMultiplier: Float = 1.0f
    ): Boolean {
        val animation = loadAnimation(animationID) ?: return false
        
        // Remove animations with same ID
        playingAnimations.removeAll { it.animation.animationID == animationID }
        
        // Add new animation
        val playingAnim = PlayingAnimation(animation, 0f, weight, speedMultiplier)
        playingAnimations.add(playingAnim)
        
        // Sort by priority
        playingAnimations.sortByDescending { it.animation.priority.value }
        
        // Limit number of active animations
        while (playingAnimations.size > MAX_ACTIVE_ANIMATIONS) {
            playingAnimations.removeAt(playingAnimations.size - 1)
        }
        
        return true
    }
    
    /**
     * Stop animation
     */
    fun stopAnimation(animationID: UUID) {
        playingAnimations.removeAll { it.animation.animationID == animationID }
    }
    
    /**
     * Stop all animations
     */
    fun stopAllAnimations() {
        playingAnimations.clear()
    }
    
    /**
     * Update animations (call every frame)
     */
    fun update(): Map<String, JointState> {
        val currentTime = System.nanoTime()
        val deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000f
        lastUpdateTime = currentTime
        
        // Update all playing animations
        val toRemove = mutableListOf<PlayingAnimation>()
        for (anim in playingAnimations) {
            anim.update(deltaTime)
            if (!anim.isActive) {
                toRemove.add(anim)
            }
        }
        
        // Remove finished animations
        playingAnimations.removeAll(toRemove)
        
        // Blend all active animations
        val blendedPose = blendAnimations()
        
        // Update current joint states
        currentJointStates.putAll(blendedPose)
        
        return currentJointStates
    }
    
    /**
     * Blend all active animations
     */
    private fun blendAnimations(): Map<String, JointState> {
        if (playingAnimations.isEmpty()) {
            return emptyMap()
        }
        
        val result = mutableMapOf<String, JointState>()
        val weights = mutableMapOf<String, Float>()
        
        // Accumulate weighted poses
        for (anim in playingAnimations) {
            val pose = anim.getPose(currentJointStates)
            
            for ((jointName, jointState) in pose) {
                val currentWeight = weights.getOrDefault(jointName, 0f)
                val newWeight = anim.weight
                
                if (currentWeight == 0f) {
                    result[jointName] = jointState
                    weights[jointName] = newWeight
                } else {
                    // Blend with existing
                    val existing = result[jointName]!!
                    val t = newWeight / (currentWeight + newWeight)
                    
                    result[jointName] = JointState(
                        position = existing.position + (jointState.position - existing.position) * t,
                        rotation = existing.rotation.slerp(t, jointState.rotation)
                    )
                    weights[jointName] = currentWeight + newWeight
                }
            }
        }
        
        return result
    }
    
    /**
     * Get currently playing animations
     */
    fun getPlayingAnimations(): List<UUID> {
        return playingAnimations.map { it.animation.animationID }
    }
    
    /**
     * Check if animation is playing
     */
    fun isPlaying(animationID: UUID): Boolean {
        return playingAnimations.any { it.animation.animationID == animationID }
    }
}
