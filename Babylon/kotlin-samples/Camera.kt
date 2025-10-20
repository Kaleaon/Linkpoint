package com.babylonkt.core

import com.babylonkt.math.Vector3
import com.babylonkt.math.Matrix4x4
import kotlin.math.tan

/**
 * Base class for all cameras.
 */
abstract class Camera(
    val name: String,
    var position: Vector3,
    var scene: Scene? = null
) {
    // ==================== Camera Properties ====================
    
    var target = Vector3.Zero()
    var upVector = Vector3.Up()
    
    // Projection properties
    var fov = 0.8f // Field of view in radians (approximately 45 degrees)
    var minZ = 0.1f // Near clipping plane
    var maxZ = 1000f // Far clipping plane
    var aspectRatio = 16f / 9f
    
    // Viewport
    var viewport = Viewport(0f, 0f, 1f, 1f)
    
    // State
    var isActive = true
    
    // Cached matrices
    private var viewMatrix: Matrix4x4? = null
    private var projectionMatrix: Matrix4x4? = null
    private var isDirty = true
    
    // ==================== Matrix Computation ====================
    
    /**
     * Gets the view matrix for this camera.
     */
    fun getViewMatrix(): Matrix4x4 {
        if (isDirty || viewMatrix == null) {
            viewMatrix = computeViewMatrix()
        }
        return viewMatrix!!
    }
    
    /**
     * Gets the projection matrix for this camera.
     */
    fun getProjectionMatrix(): Matrix4x4 {
        if (isDirty || projectionMatrix == null) {
            projectionMatrix = computeProjectionMatrix()
        }
        return projectionMatrix!!
    }
    
    protected open fun computeViewMatrix(): Matrix4x4 {
        return Matrix4x4.lookAt(position, target, upVector)
    }
    
    protected open fun computeProjectionMatrix(): Matrix4x4 {
        return Matrix4x4.perspective(fov, aspectRatio, minZ, maxZ)
    }
    
    /**
     * Updates the camera (called each frame).
     */
    open fun update() {
        if (isDirty) {
            viewMatrix = computeViewMatrix()
            projectionMatrix = computeProjectionMatrix()
            isDirty = false
        }
    }
    
    /**
     * Marks the camera as dirty (needs matrix recomputation).
     */
    fun markAsDirty() {
        isDirty = true
    }
    
    // ==================== Camera Control ====================
    
    /**
     * Sets the target that the camera looks at.
     */
    fun setTarget(target: Vector3) {
        this.target = target
        markAsDirty()
    }
    
    /**
     * Attaches input controls to this camera.
     */
    open fun attachControl(canvas: Any) {
        // To be implemented by subclasses
    }
    
    /**
     * Detaches input controls from this camera.
     */
    open fun detachControl() {
        // To be implemented by subclasses
    }
    
    // ==================== Picking ====================
    
    /**
     * Creates a picking ray from screen coordinates.
     */
    fun getPickingRay(x: Int, y: Int, viewportWidth: Int, viewportHeight: Int): Ray {
        // Convert screen coordinates to normalized device coordinates
        val ndcX = (2f * x / viewportWidth) - 1f
        val ndcY = 1f - (2f * y / viewportHeight)
        
        // Get inverse projection and view matrices
        val invProjection = getProjectionMatrix().inverse()
        val invView = getViewMatrix().inverse()
        
        // Transform to view space
        val nearPoint = Vector3(ndcX, ndcY, -1f)
        val farPoint = Vector3(ndcX, ndcY, 1f)
        
        val nearWorld = invProjection.transformPoint(nearPoint)
        val farWorld = invProjection.transformPoint(farPoint)
        
        // Transform to world space
        val rayOrigin = invView.transformPoint(nearWorld)
        val rayEnd = invView.transformPoint(farWorld)
        
        val rayDirection = (rayEnd - rayOrigin).normalize()
        
        return Ray(rayOrigin, rayDirection)
    }
    
    // ==================== Viewport Management ====================
    
    /**
     * Called when the viewport is resized.
     */
    open fun onResize(width: Int, height: Int) {
        aspectRatio = width.toFloat() / height.toFloat()
        markAsDirty()
    }
    
    override fun toString(): String {
        return "Camera(name='$name', position=$position, target=$target)"
    }
}

// ==================== Specific Camera Types ====================

/**
 * A free camera that can move freely in 3D space.
 */
class FreeCamera(
    name: String,
    position: Vector3,
    scene: Scene? = null
) : Camera(name, position, scene) {
    
    // Movement properties
    var speed = 1f
    var angularSensibility = 2000f
    
    // Rotation
    var rotation = Vector3.Zero()
    
    // Input state
    private val keysPressed = mutableSetOf<String>()
    
    override fun computeViewMatrix(): Matrix4x4 {
        // Calculate forward direction from rotation
        val forward = Vector3(
            kotlin.math.sin(rotation.y) * kotlin.math.cos(rotation.x),
            -kotlin.math.sin(rotation.x),
            kotlin.math.cos(rotation.y) * kotlin.math.cos(rotation.x)
        )
        
        target = position + forward
        
        return Matrix4x4.lookAt(position, target, upVector)
    }
    
    override fun update() {
        // Handle keyboard input for movement
        handleMovement()
        super.update()
    }
    
    private fun handleMovement() {
        val deltaTime = scene?.engine?.getDeltaTime() ?: 0.016f
        val moveSpeed = speed * deltaTime
        
        // Calculate movement vectors
        val forward = (target - position).normalize()
        val right = forward.cross(upVector).normalize()
        
        // Apply movement based on pressed keys
        if (keysPressed.contains("W")) {
            position += forward * moveSpeed
        }
        if (keysPressed.contains("S")) {
            position -= forward * moveSpeed
        }
        if (keysPressed.contains("A")) {
            position -= right * moveSpeed
        }
        if (keysPressed.contains("D")) {
            position += right * moveSpeed
        }
        if (keysPressed.contains("Q")) {
            position -= upVector * moveSpeed
        }
        if (keysPressed.contains("E")) {
            position += upVector * moveSpeed
        }
        
        markAsDirty()
    }
    
    /**
     * Handles key press events.
     */
    fun onKeyDown(key: String) {
        keysPressed.add(key.uppercase())
    }
    
    /**
     * Handles key release events.
     */
    fun onKeyUp(key: String) {
        keysPressed.remove(key.uppercase())
    }
    
    /**
     * Handles mouse movement for looking around.
     */
    fun onMouseMove(deltaX: Float, deltaY: Float) {
        rotation.y += deltaX / angularSensibility
        rotation.x += deltaY / angularSensibility
        
        // Clamp vertical rotation to avoid flipping
        rotation.x = rotation.x.coerceIn(-kotlin.math.PI.toFloat() / 2f, kotlin.math.PI.toFloat() / 2f)
        
        markAsDirty()
    }
}

/**
 * An arc rotate camera that orbits around a target.
 */
class ArcRotateCamera(
    name: String,
    var alpha: Float, // Horizontal rotation (radians)
    var beta: Float,  // Vertical rotation (radians)
    var radius: Float, // Distance from target
    target: Vector3,
    scene: Scene? = null
) : Camera(name, Vector3.Zero(), scene) {
    
    init {
        this.target = target
        updatePosition()
    }
    
    // Rotation limits
    var lowerAlphaLimit: Float? = null
    var upperAlphaLimit: Float? = null
    var lowerBetaLimit = 0.01f
    var upperBetaLimit = kotlin.math.PI.toFloat() - 0.01f
    
    // Zoom limits
    var lowerRadiusLimit = 0.1f
    var upperRadiusLimit = 1000f
    
    // Input sensitivity
    var angularSensibilityX = 1000f
    var angularSensibilityY = 1000f
    var wheelPrecision = 3f
    
    private fun updatePosition() {
        // Convert spherical coordinates to Cartesian
        val sinBeta = kotlin.math.sin(beta)
        val cosBeta = kotlin.math.cos(beta)
        val sinAlpha = kotlin.math.sin(alpha)
        val cosAlpha = kotlin.math.cos(alpha)
        
        position = Vector3(
            target.x + radius * sinBeta * cosAlpha,
            target.y + radius * cosBeta,
            target.z + radius * sinBeta * sinAlpha
        )
        
        markAsDirty()
    }
    
    override fun update() {
        updatePosition()
        super.update()
    }
    
    /**
     * Rotates the camera.
     */
    fun rotate(deltaAlpha: Float, deltaBeta: Float) {
        alpha += deltaAlpha
        beta += deltaBeta
        
        // Apply limits
        lowerAlphaLimit?.let { alpha = maxOf(alpha, it) }
        upperAlphaLimit?.let { alpha = minOf(alpha, it) }
        beta = beta.coerceIn(lowerBetaLimit, upperBetaLimit)
        
        updatePosition()
    }
    
    /**
     * Zooms the camera in or out.
     */
    fun zoom(delta: Float) {
        radius += delta
        radius = radius.coerceIn(lowerRadiusLimit, upperRadiusLimit)
        updatePosition()
    }
    
    /**
     * Handles mouse drag for rotation.
     */
    fun onMouseDrag(deltaX: Float, deltaY: Float) {
        rotate(
            deltaX / angularSensibilityX,
            deltaY / angularSensibilityY
        )
    }
    
    /**
     * Handles mouse wheel for zooming.
     */
    fun onMouseWheel(delta: Float) {
        zoom(delta / wheelPrecision)
    }
}

/**
 * A follow camera that follows a target mesh.
 */
class FollowCamera(
    name: String,
    position: Vector3,
    scene: Scene? = null
) : Camera(name, position, scene) {
    
    var followedMesh: Mesh? = null
    var radius = 10f
    var heightOffset = 5f
    var rotationOffset = 0f
    var cameraAcceleration = 0.05f
    var maxCameraSpeed = 20f
    
    override fun update() {
        followedMesh?.let { mesh ->
            // Calculate desired position behind the mesh
            val meshPosition = mesh.position
            val meshRotation = mesh.rotation
            
            val targetPosition = Vector3(
                meshPosition.x - radius * kotlin.math.sin(meshRotation.y + rotationOffset),
                meshPosition.y + heightOffset,
                meshPosition.z - radius * kotlin.math.cos(meshRotation.y + rotationOffset)
            )
            
            // Smoothly move camera towards target position
            val direction = targetPosition - position
            val distance = direction.length()
            
            if (distance > 0.01f) {
                val speed = minOf(distance * cameraAcceleration, maxCameraSpeed)
                val deltaTime = scene?.engine?.getDeltaTime() ?: 0.016f
                position += direction.normalize() * speed * deltaTime
            }
            
            // Look at the mesh
            target = meshPosition
            markAsDirty()
        }
        
        super.update()
    }
}

/**
 * A universal camera that combines multiple camera behaviors.
 */
class UniversalCamera(
    name: String,
    position: Vector3,
    scene: Scene? = null
) : FreeCamera(name, position, scene) {
    
    // Touch input support
    var touchAngularSensibility = 200000f
    var touchMoveSensibility = 250f
    
    // Gamepad support
    var gamepadAngularSensibility = 200f
    var gamepadMoveSensibility = 40f
}

// ==================== Supporting Classes ====================

/**
 * Represents a viewport (portion of the screen).
 */
data class Viewport(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)