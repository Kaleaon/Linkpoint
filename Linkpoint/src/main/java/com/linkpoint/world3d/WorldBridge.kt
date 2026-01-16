package com.linkpoint.world3d

import android.content.Context
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.math.Vector3
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ktx.app.KtxApplicationAdapter
import ktx.async.KtxAsync
import ktx.log.logger

/**
 * Bridge between libGDX game logic and SceneView/Filament rendering.
 * 
 * This class handles:
 * - Game loop and timing
 * - Input processing (movement, camera, interactions)
 * - Physics and collision detection
 * - Network state synchronization
 * - Asset loading coordination
 * 
 * SceneView handles the actual 3D rendering via Filament, while libGDX
 * provides the game logic layer and cross-platform compatibility.
 */
class WorldBridge(private val context: Context) : KtxApplicationAdapter {
    
    companion object {
        private val log = logger<WorldBridge>()
        
        @Volatile
        private var instance: WorldBridge? = null
        
        fun getInstance(context: Context): WorldBridge {
            return instance ?: synchronized(this) {
                instance ?: WorldBridge(context.applicationContext).also { instance = it }
            }
        }
    }
    
    // State exposed to Compose UI
    private val _worldState = MutableStateFlow(WorldStateData())
    val worldState: StateFlow<WorldStateData> = _worldState.asStateFlow()
    
    // Input state
    private val _inputState = MutableStateFlow(InputState())
    val inputState: StateFlow<InputState> = _inputState.asStateFlow()
    
    // Game timing
    private var accumulator = 0f
    private val fixedTimeStep = 1f / 60f  // 60 FPS physics
    
    // Avatar controller
    private val avatarController = AvatarController()
    
    // Camera controller
    private val cameraController = CameraController()
    
    // Is the bridge initialized?
    private var isInitialized = false
    
    override fun create() {
        log.info { "WorldBridge created" }
        KtxAsync.initiate()
        isInitialized = true
    }
    
    override fun render() {
        if (!isInitialized) return
        
        val deltaTime = Gdx.graphics.deltaTime.coerceAtMost(0.25f)
        
        // Fixed timestep for physics
        accumulator += deltaTime
        while (accumulator >= fixedTimeStep) {
            fixedUpdate(fixedTimeStep)
            accumulator -= fixedTimeStep
        }
        
        // Variable update for rendering interpolation
        update(deltaTime)
    }
    
    /**
     * Fixed timestep update for physics and networking
     */
    private fun fixedUpdate(dt: Float) {
        // Update avatar physics
        avatarController.fixedUpdate(dt, _inputState.value)
        
        // Update camera
        cameraController.update(dt, avatarController.position)
    }
    
    /**
     * Variable timestep update for rendering
     */
    private fun update(dt: Float) {
        // Update world state for Compose/SceneView
        _worldState.value = WorldStateData(
            avatarPosition = avatarController.position.toFloat3(),
            avatarRotation = avatarController.rotation,
            cameraPosition = cameraController.position.toFloat3(),
            cameraTarget = cameraController.target.toFloat3(),
            worldTime = _worldState.value.worldTime + dt
        )
    }
    
    /**
     * Process joystick input for avatar movement
     */
    fun onJoystickInput(x: Float, y: Float) {
        _inputState.value = _inputState.value.copy(
            moveX = x,
            moveY = y
        )
    }
    
    /**
     * Process joystick release
     */
    fun onJoystickReleased() {
        _inputState.value = _inputState.value.copy(
            moveX = 0f,
            moveY = 0f
        )
    }
    
    /**
     * Process camera rotation input
     */
    fun onCameraRotate(deltaX: Float, deltaY: Float) {
        cameraController.rotate(deltaX, deltaY)
    }
    
    /**
     * Process camera zoom input
     */
    fun onCameraZoom(delta: Float) {
        cameraController.zoom(delta)
    }
    
    /**
     * Teleport avatar to position
     */
    fun teleportAvatar(x: Float, y: Float, z: Float) {
        avatarController.teleport(Vector3(x, y, z))
        log.info { "Teleported avatar to ($x, $y, $z)" }
    }
    
    /**
     * Set avatar flying state
     */
    fun setFlying(flying: Boolean) {
        avatarController.isFlying = flying
    }
    
    /**
     * Jump
     */
    fun jump() {
        avatarController.jump()
    }
    
    override fun resize(width: Int, height: Int) {
        log.debug { "Resize: $width x $height" }
    }
    
    override fun pause() {
        log.info { "WorldBridge paused" }
    }
    
    override fun resume() {
        log.info { "WorldBridge resumed" }
    }
    
    override fun dispose() {
        log.info { "WorldBridge disposed" }
        isInitialized = false
        instance = null
    }
    
    /**
     * Get Android configuration for libGDX
     */
    fun getConfiguration(): AndroidApplicationConfiguration {
        return AndroidApplicationConfiguration().apply {
            useAccelerometer = false
            useCompass = false
            useGyroscope = false
            useImmersiveMode = true
            // Don't use libGDX rendering - we use SceneView/Filament
            disableAudio = false
        }
    }
}

/**
 * World state data exposed to Compose
 */
data class WorldStateData(
    val avatarPosition: dev.romainguy.kotlin.math.Float3 = dev.romainguy.kotlin.math.Float3(128f, 128f, 25f),
    val avatarRotation: Float = 0f,
    val cameraPosition: dev.romainguy.kotlin.math.Float3 = dev.romainguy.kotlin.math.Float3(128f, 130f, 30f),
    val cameraTarget: dev.romainguy.kotlin.math.Float3 = dev.romainguy.kotlin.math.Float3(128f, 128f, 25f),
    val worldTime: Float = 0f
)

/**
 * Input state from UI controls
 */
data class InputState(
    val moveX: Float = 0f,
    val moveY: Float = 0f,
    val lookX: Float = 0f,
    val lookY: Float = 0f,
    val jumping: Boolean = false,
    val flying: Boolean = false
)

/**
 * Extension to convert libGDX Vector3 to Float3
 */
private fun Vector3.toFloat3(): dev.romainguy.kotlin.math.Float3 {
    return dev.romainguy.kotlin.math.Float3(x, y, z)
}

/**
 * Avatar movement controller using libGDX
 */
class AvatarController {
    var position = Vector3(128f, 128f, 25f)
        private set
    var velocity = Vector3()
        private set
    var rotation = 0f
        private set
    var isFlying = false
    
    private val walkSpeed = 3f      // m/s
    private val runSpeed = 6f       // m/s
    private val flySpeed = 10f      // m/s
    private val jumpVelocity = 5f   // m/s
    private val gravity = -9.8f     // m/s²
    private var isGrounded = true
    
    fun fixedUpdate(dt: Float, input: InputState) {
        // Calculate movement direction (avoid normalizing zero vector)
        // Deadzone threshold: 0.01f squared length ≈ 0.1 actual length
        val speed = if (isFlying) flySpeed else walkSpeed
        val rawMoveDir = Vector3(input.moveX, 0f, -input.moveY)
        val moveDir = if (rawMoveDir.len2() > 0.01f) rawMoveDir.nor() else Vector3.Zero
        
        // Apply movement
        if (isFlying) {
            velocity.x = moveDir.x * speed
            velocity.z = moveDir.z * speed
            // Vertical movement in fly mode: use joystick Y axis for altitude control
            // Push forward (positive Y) to ascend, pull back (negative Y) to descend
            velocity.y = input.moveY * speed
        } else {
            velocity.x = moveDir.x * speed
            velocity.z = moveDir.z * speed
            
            // Apply gravity
            if (!isGrounded) {
                velocity.y += gravity * dt
            }
        }
        
        // Update position
        position.add(velocity.x * dt, velocity.y * dt, velocity.z * dt)
        
        // Ground check (simple - assumes ground at z=25)
        if (!isFlying && position.z <= 25f) {
            position.z = 25f
            velocity.y = 0f
            isGrounded = true
        }
        
        // Update rotation based on movement
        if (moveDir.len2() > 0.01f) {
            rotation = kotlin.math.atan2(moveDir.x, -moveDir.z) * (180f / Math.PI.toFloat())
        }
    }
    
    fun jump() {
        if (isGrounded && !isFlying) {
            velocity.y = jumpVelocity
            isGrounded = false
        }
    }
    
    fun teleport(newPosition: Vector3) {
        position.set(newPosition)
        velocity.set(0f, 0f, 0f)
    }
}

/**
 * Third-person camera controller
 */
class CameraController {
    var position = Vector3(128f, 130f, 30f)
        private set
    var target = Vector3(128f, 128f, 25f)
        private set
    
    private var distance = 5f
    private var yaw = 0f      // Horizontal rotation
    private var pitch = 20f   // Vertical rotation (degrees)
    
    private val minDistance = 1f
    private val maxDistance = 20f
    private val minPitch = -80f
    private val maxPitch = 80f
    
    fun update(dt: Float, avatarPosition: Vector3) {
        target.set(avatarPosition).add(0f, 1.5f, 0f)  // Look at avatar head
        
        // Calculate camera position from spherical coordinates
        val pitchRad = pitch * (Math.PI.toFloat() / 180f)
        val yawRad = yaw * (Math.PI.toFloat() / 180f)
        
        val offsetX = distance * kotlin.math.cos(pitchRad) * kotlin.math.sin(yawRad)
        val offsetY = distance * kotlin.math.sin(pitchRad)
        val offsetZ = distance * kotlin.math.cos(pitchRad) * kotlin.math.cos(yawRad)
        
        position.set(
            target.x + offsetX,
            target.y + offsetY,
            target.z + offsetZ
        )
    }
    
    fun rotate(deltaX: Float, deltaY: Float) {
        yaw += deltaX * 0.5f
        pitch = (pitch - deltaY * 0.5f).coerceIn(minPitch, maxPitch)
    }
    
    fun zoom(delta: Float) {
        distance = (distance - delta).coerceIn(minDistance, maxDistance)
    }
}
