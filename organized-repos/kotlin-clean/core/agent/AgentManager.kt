package com.linkpoint.agent

import com.linkpoint.Debug
import com.linkpoint.slproto.SLCircuitNew
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.animation.AnimationSystem
import kotlinx.coroutines.*
import java.nio.ByteBuffer
import java.util.UUID
import kotlin.math.*

/**
 * AgentState - Current state of the agent
 */
data class AgentState(
    var position: LLVector3 = LLVector3(),
    var rotation: LLQuaternion = LLQuaternion(),
    var velocity: LLVector3 = LLVector3(),
    var lookAt: LLVector3 = LLVector3(1f, 0f, 0f),
    var isFlying: Boolean = false,
    var isSitting: Boolean = false,
    var isRunning: Boolean = false,
    var isTyping: Boolean = false,
    var health: Float = 100f,
    var energy: Float = 100f
)

/**
 * ControlFlags - Agent control bit flags
 */
object ControlFlags {
    const val NONE = 0
    const val AT_POS = 1 shl 0         // Move forward
    const val AT_NEG = 1 shl 1         // Move backward
    const val LEFT_POS = 1 shl 2       // Move left
    const val LEFT_NEG = 1 shl 3       // Move right
    const val UP_POS = 1 shl 4         // Move up
    const val UP_NEG = 1 shl 5         // Move down
    const val PITCH_POS = 1 shl 6      // Look up
    const val PITCH_NEG = 1 shl 7      // Look down
    const val YAW_POS = 1 shl 8        // Turn left
    const val YAW_NEG = 1 shl 9        // Turn right
    const val FAST_AT = 1 shl 10       // Run
    const val FAST_LEFT = 1 shl 11     // Fast strafe
    const val FAST_UP = 1 shl 12       // Fast fly
    const val FLY = 1 shl 13           // Fly mode
    const val STOP = 1 shl 14          // Stop moving
    const val FINISH_ANIM = 1 shl 15   // Finish animation
    const val STAND_UP = 1 shl 16      // Stand up from sitting
    const val SIT_ON_GROUND = 1 shl 17 // Sit on ground
    const val MOUSELOOK = 1 shl 18     // Mouselook mode
    const val NUDGE_AT_POS = 1 shl 19  // Nudge forward
    const val NUDGE_AT_NEG = 1 shl 20  // Nudge backward
    const val NUDGE_LEFT_POS = 1 shl 21 // Nudge left
    const val NUDGE_LEFT_NEG = 1 shl 22 // Nudge right
    const val NUDGE_UP_POS = 1 shl 23   // Nudge up
    const val NUDGE_UP_NEG = 1 shl 24   // Nudge down
    const val TURN_LEFT = 1 shl 25      // Turn left
    const val TURN_RIGHT = 1 shl 26     // Turn right
    const val AWAY = 1 shl 27           // Away mode
    const val LBUTTON_DOWN = 1 shl 28   // Left mouse button
    const val LBUTTON_UP = 1 shl 29     // Left mouse button up
    const val ML_LBUTTON_DOWN = 1 shl 30 // Mouselook left button
}

/**
 * AgentManager - Manages agent (avatar) state and control
 * 
 * Features:
 * - Movement (walk, run, fly)
 * - Camera control
 * - Sitting/standing
 * - Animation management
 * - Health/energy tracking
 * - Control flags
 * 
 * Based on Firestorm's LLAgent
 */
class AgentManager(
    private val circuit: SLCircuitNew,
    private val animationSystem: AnimationSystem,
    val agentID: UUID,
    val sessionID: UUID
) {
    
    companion object {
        const val UPDATE_INTERVAL_MS = 33L  // ~30 FPS
        const val WALK_SPEED = 3.0f
        const val RUN_SPEED = 6.0f
        const val FLY_SPEED = 10.0f
        const val TURN_SPEED = PI.toFloat() / 2f  // radians per second
    }
    
    // Agent state
    val state = AgentState()
    
    // Control state
    private var controlFlags = 0
    private var lastUpdateTime = System.currentTimeMillis()
    
    // Update job
    private var updateJob: Job? = null
    
    /**
     * Start agent updates
     */
    fun start() {
        updateJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                try {
                    update()
                    sendAgentUpdate()
                    delay(UPDATE_INTERVAL_MS)
                } catch (e: CancellationException) {
                    break
                } catch (e: Exception) {
                    Debug.Log("Agent update error: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Stop agent updates
     */
    fun stop() {
        updateJob?.cancel()
    }
    
    /**
     * Update agent state
     */
    private fun update() {
        val currentTime = System.currentTimeMillis()
        val deltaTime = (currentTime - lastUpdateTime) / 1000f
        lastUpdateTime = currentTime
        
        // Update based on control flags
        updateMovement(deltaTime)
        updateRotation(deltaTime)
        updateAnimations()
        
        // Apply physics
        applyPhysics(deltaTime)
    }
    
    /**
     * Update movement based on control flags
     */
    private fun updateMovement(deltaTime: Float) {
        val forward = state.rotation * LLVector3.x_axis
        val left = state.rotation * LLVector3.y_axis
        val up = LLVector3.z_axis
        
        var moveDir = LLVector3()
        
        // Forward/backward
        if ((controlFlags and ControlFlags.AT_POS) != 0) {
            moveDir = moveDir + forward
        }
        if ((controlFlags and ControlFlags.AT_NEG) != 0) {
            moveDir = moveDir - forward
        }
        
        // Left/right
        if ((controlFlags and ControlFlags.LEFT_POS) != 0) {
            moveDir = moveDir + left
        }
        if ((controlFlags and ControlFlags.LEFT_NEG) != 0) {
            moveDir = moveDir - left
        }
        
        // Up/down (flying)
        if (state.isFlying) {
            if ((controlFlags and ControlFlags.UP_POS) != 0) {
                moveDir = moveDir + up
            }
            if ((controlFlags and ControlFlags.UP_NEG) != 0) {
                moveDir = moveDir - up
            }
        }
        
        // Normalize and apply speed
        if (moveDir.lengthSquared() > 0) {
            moveDir.normalize()
            
            val speed = when {
                state.isFlying -> FLY_SPEED
                state.isRunning || (controlFlags and ControlFlags.FAST_AT) != 0 -> RUN_SPEED
                else -> WALK_SPEED
            }
            
            state.velocity = moveDir * speed
        } else {
            state.velocity = LLVector3()  // Stop
        }
    }
    
    /**
     * Update rotation based on control flags
     */
    private fun updateRotation(deltaTime: Float) {
        var yawDelta = 0f
        
        if ((controlFlags and ControlFlags.YAW_POS) != 0 || (controlFlags and ControlFlags.TURN_LEFT) != 0) {
            yawDelta += TURN_SPEED * deltaTime
        }
        if ((controlFlags and ControlFlags.YAW_NEG) != 0 || (controlFlags and ControlFlags.TURN_RIGHT) != 0) {
            yawDelta -= TURN_SPEED * deltaTime
        }
        
        if (yawDelta != 0f) {
            val yawRot = LLQuaternion(yawDelta, LLVector3.z_axis)
            state.rotation = yawRot * state.rotation
            state.rotation.normalize()
        }
    }
    
    /**
     * Update animations based on state
     */
    private fun updateAnimations() {
        // Stop previous movement animations
        animationSystem.stopAnimation(AnimationSystem.ANIM_WALK)
        animationSystem.stopAnimation(AnimationSystem.ANIM_RUN)
        
        // Play appropriate animation
        when {
            state.isSitting -> {
                if (!animationSystem.isPlaying(AnimationSystem.ANIM_SIT)) {
                    CoroutineScope(Dispatchers.IO).launch {
                        animationSystem.playAnimation(AnimationSystem.ANIM_SIT)
                    }
                }
            }
            state.isFlying -> {
                if (!animationSystem.isPlaying(AnimationSystem.ANIM_FLY)) {
                    CoroutineScope(Dispatchers.IO).launch {
                        animationSystem.playAnimation(AnimationSystem.ANIM_FLY)
                    }
                }
            }
            state.velocity.lengthSquared() > 0 -> {
                if (state.isRunning) {
                    CoroutineScope(Dispatchers.IO).launch {
                        animationSystem.playAnimation(AnimationSystem.ANIM_RUN)
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        animationSystem.playAnimation(AnimationSystem.ANIM_WALK)
                    }
                }
            }
            else -> {
                if (!animationSystem.isPlaying(AnimationSystem.ANIM_STAND)) {
                    CoroutineScope(Dispatchers.IO).launch {
                        animationSystem.playAnimation(AnimationSystem.ANIM_STAND)
                    }
                }
            }
        }
    }
    
    /**
     * Apply physics to agent
     */
    private fun applyPhysics(deltaTime: Float) {
        // Update position
        state.position = state.position + (state.velocity * deltaTime)
        
        // Ground clamping (if not flying)
        if (!state.isFlying && state.position.z < 0f) {
            state.position.z = 0f
        }
    }
    
    /**
     * Send AgentUpdate message to server
     */
    private fun sendAgentUpdate() {
        val message = AgentUpdate(
            agentID = agentID,
            sessionID = sessionID,
            position = state.position,
            rotation = state.rotation,
            controlFlags = controlFlags,
            flags = buildFlags()
        )
        circuit.sendMessage(message)
    }
    
    /**
     * Build agent flags byte
     */
    private fun buildFlags(): Byte {
        var flags = 0
        if (state.isFlying) flags = flags or 0x01
        if (state.isSitting) flags = flags or 0x02
        if (state.isTyping) flags = flags or 0x04
        return flags.toByte()
    }
    
    /**
     * Movement controls
     */
    fun moveForward() {
        controlFlags = controlFlags or ControlFlags.AT_POS
    }
    
    fun moveBackward() {
        controlFlags = controlFlags or ControlFlags.AT_NEG
    }
    
    fun moveLeft() {
        controlFlags = controlFlags or ControlFlags.LEFT_POS
    }
    
    fun moveRight() {
        controlFlags = controlFlags or ControlFlags.LEFT_NEG
    }
    
    fun moveUp() {
        controlFlags = controlFlags or ControlFlags.UP_POS
    }
    
    fun moveDown() {
        controlFlags = controlFlags or ControlFlags.UP_NEG
    }
    
    fun stopMove() {
        controlFlags = controlFlags and ControlFlags.AT_POS.inv()
        controlFlags = controlFlags and ControlFlags.AT_NEG.inv()
        controlFlags = controlFlags and ControlFlags.LEFT_POS.inv()
        controlFlags = controlFlags and ControlFlags.LEFT_NEG.inv()
        controlFlags = controlFlags and ControlFlags.UP_POS.inv()
        controlFlags = controlFlags and ControlFlags.UP_NEG.inv()
    }
    
    fun turnLeft() {
        controlFlags = controlFlags or ControlFlags.YAW_POS
    }
    
    fun turnRight() {
        controlFlags = controlFlags or ControlFlags.YAW_NEG
    }
    
    fun stopTurn() {
        controlFlags = controlFlags and ControlFlags.YAW_POS.inv()
        controlFlags = controlFlags and ControlFlags.YAW_NEG.inv()
    }
    
    /**
     * Action controls
     */
    fun startFlying() {
        state.isFlying = true
        controlFlags = controlFlags or ControlFlags.FLY
    }
    
    fun stopFlying() {
        state.isFlying = false
        controlFlags = controlFlags and ControlFlags.FLY.inv()
    }
    
    fun toggleFlying() {
        if (state.isFlying) stopFlying() else startFlying()
    }
    
    fun startRunning() {
        state.isRunning = true
        controlFlags = controlFlags or ControlFlags.FAST_AT
    }
    
    fun stopRunning() {
        state.isRunning = false
        controlFlags = controlFlags and ControlFlags.FAST_AT.inv()
    }
    
    fun toggleRunning() {
        if (state.isRunning) stopRunning() else startRunning()
    }
    
    fun sit() {
        state.isSitting = true
        controlFlags = controlFlags or ControlFlags.SIT_ON_GROUND
    }
    
    fun standUp() {
        state.isSitting = false
        controlFlags = controlFlags or ControlFlags.STAND_UP
    }
    
    fun jump() {
        if (!state.isFlying) {
            state.velocity.z = 5.0f  // Jump velocity
            CoroutineScope(Dispatchers.IO).launch {
                animationSystem.playAnimation(AnimationSystem.ANIM_JUMP)
            }
        }
    }
    
    /**
     * Teleport to position
     */
    fun teleport(position: LLVector3) {
        state.position = position
        state.velocity = LLVector3()
    }
    
    /**
     * Set typing status
     */
    fun setTyping(typing: Boolean) {
        state.isTyping = typing
    }
}

/**
 * AgentUpdate - Message to send agent state to server
 */
class AgentUpdate(
    val agentID: UUID,
    val sessionID: UUID,
    val position: LLVector3,
    val rotation: LLQuaternion,
    val controlFlags: Int,
    val flags: Byte
) : SLMessage() {
    
    override fun getMessageName() = "AgentUpdate"
    
    override fun encode(buffer: ByteBuffer) {
        writeUUID(buffer, agentID)
        writeUUID(buffer, sessionID)
        
        // Position
        buffer.putFloat(position.x)
        buffer.putFloat(position.y)
        buffer.putFloat(position.z)
        
        // Rotation (as quaternion)
        buffer.putFloat(rotation.x)
        buffer.putFloat(rotation.y)
        buffer.putFloat(rotation.z)
        buffer.putFloat(rotation.w)
        
        // Control flags
        buffer.putInt(controlFlags)
        
        // Flags
        buffer.put(flags)
    }
    
    override fun decode(buffer: ByteBuffer) {
        // Not typically received
    }
}
