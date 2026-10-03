package com.linkpoint.avatar

import android.util.Log
import com.linkpoint.assets.AnimationData
import com.linkpoint.assets.AnimationKey
import com.linkpoint.assets.AnimationManager
import com.linkpoint.assets.JointAnimation
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.UUID
import kotlin.math.max

/**
 * Handles avatar animation playback and blending
 * Implements Second Life's animation priority system
 */
class AvatarAnimator(
    private val skeleton: AvatarSkeleton,
    private val animationManager: AnimationManager
) {
    companion object {
        private const val TAG = "AvatarAnimator"
        
        // Animation priorities (higher = more important)
        const val PRIORITY_LOWEST = 0
        const val PRIORITY_LOW = 1
        const val PRIORITY_NORMAL = 2
        const val PRIORITY_HIGH = 3
        const val PRIORITY_HIGHEST = 4
    }
    
    private val playingAnimations = mutableMapOf<UUID, AnimationState>()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Accumulated bone transforms
    private val boneRotations = mutableMapOf<String, MutableList<WeightedRotation>>()
    private val bonePositions = mutableMapOf<String, MutableList<WeightedPosition>>()
    
    /**
     * Start playing an animation
     */
    suspend fun startAnimation(animId: UUID, loop: Boolean = false) {
        val anim = animationManager.getAnimation(animId) ?: return
        
        playingAnimations[animId] = AnimationState(
            animation = anim,
            startTime = System.nanoTime(),
            loop = loop || anim.loop,
            easeInTime = anim.easeIn,
            easeOutTime = anim.easeOut,
            state = PlayState.EASING_IN
        )
        
        Log.d(TAG, "Started animation: $animId (priority: ${anim.priority})")
    }
    
    /**
     * Stop an animation
     */
    fun stopAnimation(animId: UUID) {
        playingAnimations[animId]?.let { state ->
            if (state.state != PlayState.STOPPED) {
                state.state = PlayState.EASING_OUT
                state.easeOutStartTime = System.nanoTime()
            }
        }
    }
    
    /**
     * Update animations and apply to skeleton
     */
    fun update(deltaTime: Float) {
        val currentTime = System.nanoTime()
        
        // Clear accumulated transforms
        boneRotations.clear()
        bonePositions.clear()
        
        // Process each playing animation
        val iterator = playingAnimations.iterator()
        while (iterator.hasNext()) {
            val (animId, state) = iterator.next()
            
            // Calculate animation time and weight
            val elapsedSeconds = (currentTime - state.startTime) / 1_000_000_000f
            val animTime = calculateAnimTime(state, elapsedSeconds)
            val weight = calculateWeight(state, currentTime)
            
            if (weight <= 0 && state.state == PlayState.EASING_OUT) {
                iterator.remove()
                continue
            }
            
            // Update state
            if (state.state == PlayState.EASING_IN && 
                elapsedSeconds >= state.easeInTime) {
                state.state = PlayState.PLAYING
            }
            
            // Check for animation end
            if (!state.loop && animTime >= state.animation.duration) {
                if (state.state != PlayState.EASING_OUT) {
                    state.state = PlayState.EASING_OUT
                    state.easeOutStartTime = currentTime
                }
            }
            
            // Apply animation
            applyAnimation(state.animation, animTime, weight)
        }
        
        // Blend and apply to skeleton
        blendAndApply()
        
        // Update skeleton matrices
        skeleton.updateBoneMatrices()
    }
    
    private fun calculateAnimTime(state: AnimationState, elapsed: Float): Float {
        val anim = state.animation
        var time = elapsed
        
        if (state.loop) {
            val loopDuration = anim.loopOut - anim.loopIn
            if (elapsed > anim.loopIn && loopDuration > 0) {
                time = anim.loopIn + ((elapsed - anim.loopIn) % loopDuration)
            }
        }
        
        return time.coerceIn(0f, anim.duration)
    }
    
    private fun calculateWeight(state: AnimationState, currentTime: Long): Float {
        return when (state.state) {
            PlayState.EASING_IN -> {
                val elapsed = (currentTime - state.startTime) / 1_000_000_000f
                (elapsed / max(state.easeInTime, 0.01f)).coerceIn(0f, 1f)
            }
            PlayState.PLAYING -> 1f
            PlayState.EASING_OUT -> {
                val elapsed = (currentTime - state.easeOutStartTime) / 1_000_000_000f
                (1f - elapsed / max(state.easeOutTime, 0.01f)).coerceIn(0f, 1f)
            }
            PlayState.STOPPED -> 0f
        }
    }
    
    private fun applyAnimation(anim: AnimationData, time: Float, weight: Float) {
        for (joint in anim.joints) {
            val boneName = joint.jointName
            
            // Rotation
            if (joint.rotationKeys.isNotEmpty()) {
                val rotation = interpolateRotation(joint.rotationKeys, time)
                boneRotations.getOrPut(boneName) { mutableListOf() }
                    .add(WeightedRotation(rotation, weight, anim.priority))
            }
            
            // Position
            if (joint.positionKeys.isNotEmpty()) {
                val position = interpolatePosition(joint.positionKeys, time)
                bonePositions.getOrPut(boneName) { mutableListOf() }
                    .add(WeightedPosition(position, weight, anim.priority))
            }
        }
    }
    
    private fun interpolateRotation(keys: List<AnimationKey>, time: Float): LLQuaternion {
        if (keys.isEmpty()) return LLQuaternion.identity()
        if (keys.size == 1) {
            return LLQuaternion(keys[0].value[0], keys[0].value[1], keys[0].value[2], keys[0].value[3]).normalize()
        }
        
        // Find surrounding keys
        var prevKey = keys[0]
        var nextKey = keys[0]
        
        for (key in keys) {
            if (key.time <= time) {
                prevKey = key
            }
            if (key.time >= time) {
                nextKey = key
                break
            }
        }
        
        if (prevKey == nextKey) {
            return LLQuaternion(prevKey.value[0], prevKey.value[1], prevKey.value[2], prevKey.value[3]).normalize()
        }
        
        val t = (time - prevKey.time) / (nextKey.time - prevKey.time)
        
        val q1 = LLQuaternion(prevKey.value[0], prevKey.value[1], prevKey.value[2], prevKey.value[3])
        val q2 = LLQuaternion(nextKey.value[0], nextKey.value[1], nextKey.value[2], nextKey.value[3])
        
        return q1.slerp(q2, t)
    }
    
    private fun interpolatePosition(keys: List<AnimationKey>, time: Float): LLVector3 {
        if (keys.isEmpty()) return LLVector3.zero()
        if (keys.size == 1) return LLVector3(keys[0].value[0], keys[0].value[1], keys[0].value[2])
        
        var prevKey = keys[0]
        var nextKey = keys[0]
        
        for (key in keys) {
            if (key.time <= time) {
                prevKey = key
            }
            if (key.time >= time) {
                nextKey = key
                break
            }
        }
        
        if (prevKey == nextKey) {
            return LLVector3(prevKey.value[0], prevKey.value[1], prevKey.value[2])
        }
        
        val t = (time - prevKey.time) / (nextKey.time - prevKey.time)
        
        val p1 = LLVector3(prevKey.value[0], prevKey.value[1], prevKey.value[2])
        val p2 = LLVector3(nextKey.value[0], nextKey.value[1], nextKey.value[2])
        
        return p1.lerp(p2, t)
    }
    
    private fun blendAndApply() {
        // Blend rotations by priority
        for ((boneName, rotations) in boneRotations) {
            val bone = skeleton.getBone(boneName) ?: continue
            
            // Sort by priority
            rotations.sortByDescending { it.priority }
            
            // Take highest priority or blend same-priority
            var result = bone.bindRotation
            var totalWeight = 0f
            var currentPriority = Int.MAX_VALUE
            
            for (wr in rotations) {
                if (wr.priority < currentPriority && totalWeight >= 1f) break
                
                val useWeight = minOf(wr.weight, 1f - totalWeight)
                result = result.slerp(wr.rotation, useWeight)
                totalWeight += useWeight
                currentPriority = wr.priority
            }
            
            bone.rotation = result
        }
        
        // Blend positions
        for ((boneName, positions) in bonePositions) {
            val bone = skeleton.getBone(boneName) ?: continue
            
            positions.sortByDescending { it.priority }
            
            var result = bone.bindPosition
            var totalWeight = 0f
            var currentPriority = Int.MAX_VALUE
            
            for (wp in positions) {
                if (wp.priority < currentPriority && totalWeight >= 1f) break
                
                val useWeight = minOf(wp.weight, 1f - totalWeight)
                result = result.lerp(wp.position, useWeight)
                totalWeight += useWeight
                currentPriority = wp.priority
            }
            
            bone.position = result
        }
    }
    
    /**
     * Check if animation is playing
     */
    fun isPlaying(animId: UUID): Boolean = playingAnimations.containsKey(animId)
    
    /**
     * Get all playing animations
     */
    fun getPlayingAnimations(): Set<UUID> = playingAnimations.keys.toSet()
    
    /**
     * Stop all animations
     */
    fun stopAll() {
        playingAnimations.values.forEach { it.state = PlayState.STOPPED }
        playingAnimations.clear()
    }
}

private data class AnimationState(
    val animation: AnimationData,
    val startTime: Long,
    val loop: Boolean,
    val easeInTime: Float,
    val easeOutTime: Float,
    var state: PlayState = PlayState.EASING_IN,
    var easeOutStartTime: Long = 0
)

private enum class PlayState {
    EASING_IN, PLAYING, EASING_OUT, STOPPED
}

private data class WeightedRotation(
    val rotation: LLQuaternion,
    val weight: Float,
    val priority: Int
)

private data class WeightedPosition(
    val position: LLVector3,
    val weight: Float,
    val priority: Int
)
