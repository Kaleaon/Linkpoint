package com.linkpoint.avatar

import android.util.Log
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import kotlin.math.cos
import kotlin.math.sin

/**
 * Controls avatar movement in Second Life.
 * 
 * This is the core movement system that translates user input (joystick, buttons)
 * into the control flags and position updates that the simulator expects.
 * 
 * Based on the reference viewer's movement controls and LibreMetaverse AgentManager.Movement
 * 
 * Control flags are sent with AgentUpdate messages approximately 10 times per second.
 * The simulator uses these to move the avatar.
 * 
 * @see <a href="https://wiki.secondlife.com/wiki/AgentUpdate">AgentUpdate Message</a>
 */
class MovementController(
    private val udpConnection: UDPConnectionFixed
) {
    companion object {
        private const val TAG = "MovementController"
        
        // Agent control flags from the SL protocol
        // These are bit flags that get combined and sent with AgentUpdate
        const val AGENT_CONTROL_AT_POS = 0x00000001        // Move forward
        const val AGENT_CONTROL_AT_NEG = 0x00000002        // Move backward
        const val AGENT_CONTROL_LEFT_POS = 0x00000004      // Strafe left
        const val AGENT_CONTROL_LEFT_NEG = 0x00000008      // Strafe right
        const val AGENT_CONTROL_UP_POS = 0x00000010        // Move up (fly/jump)
        const val AGENT_CONTROL_UP_NEG = 0x00000020        // Move down (fly) / crouch
        const val AGENT_CONTROL_PITCH_POS = 0x00000040     // Pitch up
        const val AGENT_CONTROL_PITCH_NEG = 0x00000080     // Pitch down
        const val AGENT_CONTROL_YAW_POS = 0x00000100       // Turn left
        const val AGENT_CONTROL_YAW_NEG = 0x00000200       // Turn right
        const val AGENT_CONTROL_FAST_AT = 0x00000400       // Run forward
        const val AGENT_CONTROL_FAST_LEFT = 0x00000800     // Run strafe
        const val AGENT_CONTROL_FAST_UP = 0x00001000       // Fast up
        const val AGENT_CONTROL_FLY = 0x00002000           // Flying
        const val AGENT_CONTROL_STOP = 0x00004000          // Stop
        const val AGENT_CONTROL_FINISH_ANIM = 0x00008000   // Finish animation
        const val AGENT_CONTROL_STAND_UP = 0x00010000      // Stand up
        const val AGENT_CONTROL_SIT_ON_GROUND = 0x00020000 // Sit on ground
        const val AGENT_CONTROL_MOUSELOOK = 0x00040000     // In mouselook
        const val AGENT_CONTROL_NUDGE_AT_POS = 0x00080000  // Nudge forward
        const val AGENT_CONTROL_NUDGE_AT_NEG = 0x00100000  // Nudge backward
        const val AGENT_CONTROL_NUDGE_LEFT_POS = 0x00200000 // Nudge left
        const val AGENT_CONTROL_NUDGE_LEFT_NEG = 0x00400000 // Nudge right
        const val AGENT_CONTROL_NUDGE_UP_POS = 0x00800000  // Nudge up
        const val AGENT_CONTROL_NUDGE_UP_NEG = 0x01000000  // Nudge down
        const val AGENT_CONTROL_TURN_LEFT = 0x02000000     // Turn left
        const val AGENT_CONTROL_TURN_RIGHT = 0x04000000    // Turn right
        const val AGENT_CONTROL_AWAY = 0x08000000          // Away from keyboard
        const val AGENT_CONTROL_LBUTTON_DOWN = 0x10000000  // Left button down
        const val AGENT_CONTROL_LBUTTON_UP = 0x20000000    // Left button up
        const val AGENT_CONTROL_ML_LBUTTON_DOWN = 0x40000000.toInt() // Mouselook left button
        const val AGENT_CONTROL_ML_LBUTTON_UP = 0x80000000.toInt()   // Mouselook left button up
        
        // Movement speeds (meters per second)
        const val WALK_SPEED = 3.2f
        const val RUN_SPEED = 5.0f
        const val FLY_SPEED = 10.0f
        
        // Turn speeds (radians per second)
        const val TURN_SPEED = 2.0f
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Current movement state
    private var controlFlags: Int = 0
    
    // Agent session info
    private var agentId: UUID = UUID(0, 0)
    private var sessionId: UUID = UUID(0, 0)
    
    // Current position and rotation
    private var position = LLVector3(128f, 128f, 25f)
    private var rotation = LLQuaternion(0f, 0f, 0f, 1f)
    private var heading: Float = 0f  // Yaw in radians
    
    // Camera direction
    private var lookAt = LLVector3(1f, 0f, 0f)
    
    // Movement modes
    private val _isFlying = MutableStateFlow(false)
    val isFlying: StateFlow<Boolean> = _isFlying
    
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning
    
    private val _isSitting = MutableStateFlow(false)
    val isSitting: StateFlow<Boolean> = _isSitting
    
    private val _isAway = MutableStateFlow(false)
    val isAway: StateFlow<Boolean> = _isAway
    
    // Movement update job
    private var updateJob: Job? = null
    
    /**
     * Set agent and session info (required for movement).
     */
    fun setSessionInfo(agentId: UUID, sessionId: UUID) {
        this.agentId = agentId
        this.sessionId = sessionId
    }
    
    /**
     * Update current position (from simulator).
     */
    fun updatePosition(pos: LLVector3) {
        position = pos
        udpConnection.updateAgentPosition(pos.x, pos.y, pos.z)
    }
    
    /**
     * Update current rotation (from simulator).
     */
    fun updateRotation(rot: LLQuaternion) {
        rotation = rot.normalize()
        // Extract yaw from quaternion
        heading = rotation.getYaw()
    }
    
    /**
     * Start the movement update loop.
     * This sends AgentUpdate messages regularly to the simulator.
     */
    fun startMovementUpdates() {
        updateJob?.cancel()
        updateJob = scope.launch {
            Log.i(TAG, "Starting movement updates")
            while (isActive) {
                sendMovementUpdate()
                delay(100) // 10 updates per second
            }
        }
    }
    
    /**
     * Stop movement updates.
     */
    fun stopMovementUpdates() {
        updateJob?.cancel()
        updateJob = null
        Log.i(TAG, "Stopped movement updates")
    }
    
    // ==================== MOVEMENT CONTROLS ====================
    
    /**
     * Move forward.
     * @param pressed true when button pressed, false when released
     */
    fun moveForward(pressed: Boolean) {
        setControlFlag(AGENT_CONTROL_AT_POS, pressed)
        if (_isRunning.value && pressed) {
            setControlFlag(AGENT_CONTROL_FAST_AT, true)
        } else {
            setControlFlag(AGENT_CONTROL_FAST_AT, false)
        }
    }
    
    /**
     * Move backward.
     */
    fun moveBackward(pressed: Boolean) {
        setControlFlag(AGENT_CONTROL_AT_NEG, pressed)
    }
    
    /**
     * Strafe left.
     */
    fun strafeLeft(pressed: Boolean) {
        setControlFlag(AGENT_CONTROL_LEFT_POS, pressed)
        if (_isRunning.value && pressed) {
            setControlFlag(AGENT_CONTROL_FAST_LEFT, true)
        } else {
            setControlFlag(AGENT_CONTROL_FAST_LEFT, false)
        }
    }
    
    /**
     * Strafe right.
     */
    fun strafeRight(pressed: Boolean) {
        setControlFlag(AGENT_CONTROL_LEFT_NEG, pressed)
    }
    
    /**
     * Turn left.
     */
    fun turnLeft(pressed: Boolean) {
        setControlFlag(AGENT_CONTROL_YAW_POS, pressed)
    }
    
    /**
     * Turn right.
     */
    fun turnRight(pressed: Boolean) {
        setControlFlag(AGENT_CONTROL_YAW_NEG, pressed)
    }
    
    /**
     * Move up (fly) or jump.
     */
    fun moveUp(pressed: Boolean) {
        if (_isFlying.value) {
            setControlFlag(AGENT_CONTROL_UP_POS, pressed)
        } else if (pressed) {
            // Jump
            jump()
        }
    }
    
    /**
     * Move down (fly) or crouch.
     */
    fun moveDown(pressed: Boolean) {
        if (_isFlying.value) {
            setControlFlag(AGENT_CONTROL_UP_NEG, pressed)
        } else {
            // Crouch
            setControlFlag(AGENT_CONTROL_UP_NEG, pressed)
        }
    }
    
    /**
     * Jump.
     */
    fun jump() {
        if (!_isFlying.value && !_isSitting.value) {
            scope.launch {
                setControlFlag(AGENT_CONTROL_UP_POS, true)
                delay(250)
                setControlFlag(AGENT_CONTROL_UP_POS, false)
            }
        }
    }
    
    /**
     * Toggle flying mode.
     */
    fun toggleFly() {
        _isFlying.value = !_isFlying.value
        setControlFlag(AGENT_CONTROL_FLY, _isFlying.value)
        Log.i(TAG, "Flying: ${_isFlying.value}")
    }
    
    /**
     * Set flying mode directly.
     */
    fun setFlying(flying: Boolean) {
        _isFlying.value = flying
        setControlFlag(AGENT_CONTROL_FLY, flying)
    }
    
    /**
     * Toggle running mode.
     */
    fun toggleRun() {
        _isRunning.value = !_isRunning.value
        Log.i(TAG, "Running: ${_isRunning.value}")
    }
    
    /**
     * Set running mode directly.
     */
    fun setRunning(running: Boolean) {
        _isRunning.value = running
    }
    
    /**
     * Stand up from sitting.
     */
    fun standUp() {
        if (_isSitting.value) {
            setControlFlag(AGENT_CONTROL_STAND_UP, true)
            _isSitting.value = false
            scope.launch {
                delay(250)
                setControlFlag(AGENT_CONTROL_STAND_UP, false)
            }
        }
    }
    
    /**
     * Sit on ground.
     */
    fun sitOnGround() {
        if (!_isSitting.value && !_isFlying.value) {
            setControlFlag(AGENT_CONTROL_SIT_ON_GROUND, true)
            _isSitting.value = true
            scope.launch {
                delay(250)
                setControlFlag(AGENT_CONTROL_SIT_ON_GROUND, false)
            }
        }
    }
    
    /**
     * Toggle away status.
     */
    fun toggleAway() {
        _isAway.value = !_isAway.value
        setControlFlag(AGENT_CONTROL_AWAY, _isAway.value)
        Log.i(TAG, "Away: ${_isAway.value}")
    }
    
    /**
     * Stop all movement.
     */
    fun stopAllMovement() {
        controlFlags = 0
        if (_isFlying.value) {
            controlFlags = AGENT_CONTROL_FLY
        }
        udpConnection.setControlFlags(controlFlags)
    }
    
    /**
     * Set joystick input for smooth movement.
     * 
     * @param x Horizontal input (-1 to 1, negative = left, positive = right)
     * @param y Vertical input (-1 to 1, negative = backward, positive = forward)
     */
    fun setJoystickInput(x: Float, y: Float) {
        // Clear directional flags
        controlFlags = controlFlags and (AGENT_CONTROL_AT_POS or AGENT_CONTROL_AT_NEG or
                AGENT_CONTROL_LEFT_POS or AGENT_CONTROL_LEFT_NEG or
                AGENT_CONTROL_FAST_AT or AGENT_CONTROL_FAST_LEFT).inv()
        
        // Apply based on deadzone (0.15)
        val deadzone = 0.15f
        
        if (y > deadzone) {
            controlFlags = controlFlags or AGENT_CONTROL_AT_POS
            if (_isRunning.value) {
                controlFlags = controlFlags or AGENT_CONTROL_FAST_AT
            }
        } else if (y < -deadzone) {
            controlFlags = controlFlags or AGENT_CONTROL_AT_NEG
        }
        
        if (x < -deadzone) {
            controlFlags = controlFlags or AGENT_CONTROL_LEFT_POS
            if (_isRunning.value) {
                controlFlags = controlFlags or AGENT_CONTROL_FAST_LEFT
            }
        } else if (x > deadzone) {
            controlFlags = controlFlags or AGENT_CONTROL_LEFT_NEG
        }
        
        udpConnection.setControlFlags(controlFlags)
    }
    
    /**
     * Set rotation joystick input.
     * 
     * @param x Horizontal input (-1 to 1, negative = turn left, positive = turn right)
     */
    fun setRotationInput(x: Float) {
        val deadzone = 0.15f
        
        controlFlags = controlFlags and (AGENT_CONTROL_YAW_POS or AGENT_CONTROL_YAW_NEG).inv()
        
        if (x < -deadzone) {
            controlFlags = controlFlags or AGENT_CONTROL_YAW_POS
        } else if (x > deadzone) {
            controlFlags = controlFlags or AGENT_CONTROL_YAW_NEG
        }
        
        udpConnection.setControlFlags(controlFlags)
    }
    
    // ==================== INTERNAL ====================
    
    private fun setControlFlag(flag: Int, enabled: Boolean) {
        controlFlags = if (enabled) {
            controlFlags or flag
        } else {
            controlFlags and flag.inv()
        }
        udpConnection.setControlFlags(controlFlags)
    }
    
    private suspend fun sendMovementUpdate() {
        // Update look-at based on heading
        lookAt = LLVector3(cos(heading), sin(heading), 0f)
        udpConnection.updateLookAt(lookAt.x, lookAt.y, lookAt.z)
        
        // The UDPConnection.sendAgentUpdate() handles the actual sending
        // We just need to make sure control flags are current
        udpConnection.setControlFlags(controlFlags)
    }
    
    /**
     * Get current position.
     */
    fun getPosition(): LLVector3 = position
    
    /**
     * Get current rotation.
     */
    fun getRotation(): LLQuaternion = rotation
    
    /**
     * Get current heading in degrees.
     */
    fun getHeadingDegrees(): Float = Math.toDegrees(heading.toDouble()).toFloat()
    
    /**
     * Check if any movement is active.
     */
    fun isMoving(): Boolean {
        return (controlFlags and (
            AGENT_CONTROL_AT_POS or AGENT_CONTROL_AT_NEG or
            AGENT_CONTROL_LEFT_POS or AGENT_CONTROL_LEFT_NEG or
            AGENT_CONTROL_UP_POS or AGENT_CONTROL_UP_NEG or
            AGENT_CONTROL_YAW_POS or AGENT_CONTROL_YAW_NEG
        )) != 0
    }
    
    fun shutdown() {
        stopMovementUpdates()
        scope.cancel()
    }
}

/**
 * Extension to get yaw from quaternion.
 */
private fun LLQuaternion.getYaw(): Float {
    // Extract yaw (rotation around Z axis)
    val siny_cosp = 2f * (w * z + x * y)
    val cosy_cosp = 1f - 2f * (y * y + z * z)
    return kotlin.math.atan2(siny_cosp, cosy_cosp)
}
