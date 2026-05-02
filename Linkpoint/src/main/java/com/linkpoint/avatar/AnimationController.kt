package com.linkpoint.avatar

import android.util.Log
import com.linkpoint.protocol.messages.ids.MessageIdRegistry
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import com.linkpoint.protocol.types.putUUID
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * AnimationController - Handles avatar animation playback and management.
 * 
 * Features:
 * - Play animations
 * - Stop animations
 * - Animation priority
 * - Animation state tracking
 * - Gesture animations
 * 
 * Based on the reference viewer animation system.
 */
class AnimationController(
    private val udpConnection: UDPConnectionFixed,
    private val agentId: UUID
) {
    companion object {
        private const val TAG = "AnimationController"
        
        // Built-in animation UUIDs
        val ANIM_STAND = UUID.fromString("2408fe9e-df1d-1d7d-f4ff-1384fa7b350f")
        val ANIM_WALK = UUID.fromString("6ed24bd8-91aa-4b12-ccc7-c97c857ab4e0")
        val ANIM_RUN = UUID.fromString("05ddbff8-aaa9-92a1-2b74-8fe77a29b445")
        val ANIM_CROUCH = UUID.fromString("201f3fdf-cb1f-dbec-201f-7333e328ae7c")
        val ANIM_CROUCH_WALK = UUID.fromString("47f5f6fb-22e5-ae44-f871-73aaaf91a28c")
        val ANIM_FLY = UUID.fromString("aec4610c-757f-bc4e-c092-c6e9caf18daf")
        val ANIM_FLY_SLOW = UUID.fromString("2b5a38b2-5e00-3a97-a495-4c826bc443e6")
        val ANIM_HOVER = UUID.fromString("4ae8016b-31b9-03bb-c401-b1ea941db41d")
        val ANIM_HOVER_UP = UUID.fromString("20f063ea-8306-2562-0b07-5c853b37b31e")
        val ANIM_HOVER_DOWN = UUID.fromString("62c5de58-cb33-5743-3d07-9e4cd4352864")
        val ANIM_JUMP = UUID.fromString("2305bd75-1ca9-b03b-1faa-b176b8a8c49e")
        val ANIM_PRE_JUMP = UUID.fromString("7a4e87fe-de39-6fcb-6223-024b00c0ee7b")
        val ANIM_LAND = UUID.fromString("7a17b059-12b2-41b1-570a-186368b6aa6f")
        val ANIM_FALLDOWN = UUID.fromString("666307d9-a860-572d-6fd4-c3ab8865c094")
        val ANIM_SIT = UUID.fromString("1a5fe8ac-a804-8a5d-7f59-0f0b12e4d1e9")
        val ANIM_SIT_GROUND = UUID.fromString("1c7600d6-661f-b87b-efe2-d7421eb93c86")
        val ANIM_TYPE = UUID.fromString("c541c47f-e0c0-058b-ad1a-d6ae3a4584d9")
        
        // Animation priorities
        const val PRIORITY_BACKGROUND = 0
        const val PRIORITY_LOW = 1
        const val PRIORITY_MEDIUM = 2
        const val PRIORITY_HIGH = 3
        const val PRIORITY_EMOTE = 4
        
        // Max simultaneous animations
        private const val MAX_PLAYING_ANIMATIONS = 30
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Currently playing animations
    private val playingAnimations = ConcurrentHashMap<UUID, AnimationPlayingState>()
    
    // Animation events
    private val _animationEvents = MutableSharedFlow<AnimationEvent>(replay = 0, extraBufferCapacity = 32)
    val animationEvents: SharedFlow<AnimationEvent> = _animationEvents
    
    // Current animations list
    private val _currentAnimations = MutableStateFlow<List<UUID>>(emptyList())
    val currentAnimations: StateFlow<List<UUID>> = _currentAnimations
    
    /**
     * Play an animation.
     */
    fun playAnimation(animationId: UUID, priority: Int = PRIORITY_MEDIUM) {
        scope.launch {
            try {
                // Check limit
                if (playingAnimations.size >= MAX_PLAYING_ANIMATIONS) {
                    Log.w(TAG, "Max animations reached, not starting $animationId")
                    return@launch
                }
                
                val payload = ByteBuffer.allocate(80).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // AnimationList - 1 entry
                payload.put(1) // Count
                
                // Animation entry
                payload.putUUID(animationId)
                payload.put(1) // Start (1 = start, 0 = stop)
                
                // ObjectIDs - empty for self
                payload.put(0)
                
                udpConnection.sendPacket(MessageIdRegistry.AGENT_ANIMATION, payload.array(), reliable = true)
                
                // Track locally
                playingAnimations[animationId] = AnimationPlayingState(
                    animationId = animationId,
                    priority = priority,
                    startTime = System.currentTimeMillis()
                )
                updateCurrentAnimations()
                
                _animationEvents.emit(AnimationEvent.Started(animationId))
                Log.d(TAG, "Started animation $animationId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to play animation", e)
            }
        }
    }
    
    /**
     * Stop an animation.
     */
    fun stopAnimation(animationId: UUID) {
        scope.launch {
            try {
                val payload = ByteBuffer.allocate(80).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // AnimationList - 1 entry
                payload.put(1) // Count
                
                // Animation entry
                payload.putUUID(animationId)
                payload.put(0) // Stop
                
                // ObjectIDs - empty
                payload.put(0)
                
                udpConnection.sendPacket(MessageIdRegistry.AGENT_ANIMATION, payload.array(), reliable = true)
                
                // Remove from tracking
                playingAnimations.remove(animationId)
                updateCurrentAnimations()
                
                _animationEvents.emit(AnimationEvent.Stopped(animationId))
                Log.d(TAG, "Stopped animation $animationId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop animation", e)
            }
        }
    }
    
    /**
     * Stop all playing animations.
     */
    fun stopAllAnimations() {
        scope.launch {
            val animations = playingAnimations.keys.toList()
            for (animId in animations) {
                stopAnimation(animId)
            }
        }
    }
    
    /**
     * Handle AvatarAnimation message from server.
     */
    fun handleAvatarAnimation(avatarId: UUID, animations: List<Pair<UUID, Int>>) {
        if (avatarId == agentId) {
            // Update our animation state
            val startedAnims = mutableSetOf<UUID>()
            val stoppedAnims = playingAnimations.keys.toMutableSet()
            
            for ((animId, sequenceId) in animations) {
                startedAnims.add(animId)
                stoppedAnims.remove(animId)
                
                if (!playingAnimations.containsKey(animId)) {
                    playingAnimations[animId] = AnimationPlayingState(
                        animationId = animId,
                        priority = PRIORITY_MEDIUM,
                        startTime = System.currentTimeMillis(),
                        sequenceId = sequenceId
                    )
                    scope.launch {
                        _animationEvents.emit(AnimationEvent.Started(animId))
                    }
                }
            }
            
            // Remove stopped animations
            for (animId in stoppedAnims) {
                playingAnimations.remove(animId)
                scope.launch {
                    _animationEvents.emit(AnimationEvent.Stopped(animId))
                }
            }
            
            updateCurrentAnimations()
        }
    }
    
    /**
     * Check if an animation is playing.
     */
    fun isAnimationPlaying(animationId: UUID): Boolean {
        return playingAnimations.containsKey(animationId)
    }
    
    /**
     * Get all playing animation IDs.
     */
    fun getPlayingAnimations(): List<UUID> {
        return playingAnimations.keys.toList()
    }
    
    /**
     * Get animation state.
     */
    fun getAnimationPlayingState(animationId: UUID): AnimationPlayingState? {
        return playingAnimations[animationId]
    }
    
    private fun updateCurrentAnimations() {
        _currentAnimations.value = playingAnimations.keys.toList()
    }
    
    fun shutdown() {
        scope.cancel()
        playingAnimations.clear()
    }
}

/**
 * Animation playing state tracking.
 */
data class AnimationPlayingState(
    val animationId: UUID,
    val priority: Int,
    val startTime: Long,
    val sequenceId: Int = 0
) {
    val duration: Long get() = System.currentTimeMillis() - startTime
}

/**
 * Animation events.
 */
sealed class AnimationEvent {
    data class Started(val animationId: UUID) : AnimationEvent()
    data class Stopped(val animationId: UUID) : AnimationEvent()
}
