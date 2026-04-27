package com.linkpoint.camera

import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.agent.AgentManager
import kotlin.math.*

/**
 * CameraMode - Camera control modes
 */
enum class CameraMode {
    THIRD_PERSON,      // Standard third-person view
    FIRST_PERSON,      // First-person/mouselook
    FREE_CAMERA,       // Detached camera
    FOCUS_OBJECT,      // Focus on specific object
    ORBIT              // Orbit around point
}

/**
 * CameraManager - Manages camera position and control
 * 
 * Features:
 * - Third-person camera
 * - First-person (mouselook)
 * - Free camera movement
 * - Object focus
 * - Smooth transitions
 * - Zoom control
 * 
 * Based on Firestorm's LLAgentCamera
 */
class CameraManager(
    private val agentManager: AgentManager
) {
    
    companion object {
        const val MIN_ZOOM_DISTANCE = 0.5f
        const val MAX_ZOOM_DISTANCE = 100f
        const val DEFAULT_ZOOM_DISTANCE = 3f
        const val CAMERA_SMOOTHING = 0.15f
        const val CAMERA_HEIGHT_OFFSET = 0.3f
        const val LOOK_AHEAD_DISTANCE = 2f
    }
    
    // Camera state
    var mode = CameraMode.THIRD_PERSON
    var position = LLVector3()
    var lookAt = LLVector3()
    var up = LLVector3(0f, 0f, 1f)
    
    // Camera control
    var zoomDistance = DEFAULT_ZOOM_DISTANCE
    var pitch = 0f  // Radians
    var yaw = 0f    // Radians
    
    // Target for smooth movement
    private var targetPosition = LLVector3()
    private var targetLookAt = LLVector3()
    
    // Free camera state
    private var freeCameraPosition = LLVector3()
    private var freeCameraRotation = LLQuaternion()
    
    /**
     * Update camera (call every frame)
     */
    fun update(deltaTime: Float) {
        when (mode) {
            CameraMode.THIRD_PERSON -> updateThirdPerson()
            CameraMode.FIRST_PERSON -> updateFirstPerson()
            CameraMode.FREE_CAMERA -> updateFreeCamera()
            CameraMode.FOCUS_OBJECT -> updateObjectFocus()
            CameraMode.ORBIT -> updateOrbit()
        }
        
        // Smooth camera movement
        position = position + (targetPosition - position) * CAMERA_SMOOTHING
        lookAt = lookAt + (targetLookAt - lookAt) * CAMERA_SMOOTHING
    }
    
    /**
     * Update third-person camera
     */
    private fun updateThirdPerson() {
        val agentPos = agentManager.state.position
        val agentRot = agentManager.state.rotation
        
        // Calculate camera offset based on zoom and angles
        val forward = LLVector3(
            cos(pitch) * cos(yaw),
            cos(pitch) * sin(yaw),
            sin(pitch)
        )
        
        val offset = forward * -zoomDistance
        
        // Position camera behind and above agent
        targetPosition = agentPos + offset + LLVector3(0f, 0f, CAMERA_HEIGHT_OFFSET)
        
        // Look at point ahead of agent
        val agentForward = agentRot * LLVector3.x_axis
        targetLookAt = agentPos + agentForward * LOOK_AHEAD_DISTANCE + LLVector3(0f, 0f, CAMERA_HEIGHT_OFFSET)
    }
    
    /**
     * Update first-person camera
     */
    private fun updateFirstPerson() {
        val agentPos = agentManager.state.position
        val agentRot = agentManager.state.rotation
        
        // Camera at agent's eye level
        targetPosition = agentPos + LLVector3(0f, 0f, 1.6f)  // Eye height
        
        // Look direction based on pitch and yaw
        val lookDir = LLVector3(
            cos(pitch) * cos(yaw),
            cos(pitch) * sin(yaw),
            sin(pitch)
        )
        
        targetLookAt = targetPosition + lookDir
    }
    
    /**
     * Update free camera
     */
    private fun updateFreeCamera() {
        targetPosition = freeCameraPosition
        
        val lookDir = freeCameraRotation * LLVector3.x_axis
        targetLookAt = targetPosition + lookDir
    }
    
    /**
     * Update object focus camera
     */
    private fun updateObjectFocus() {
        // Similar to third person but focused on specific object
        updateThirdPerson()  // Default behavior for now
    }
    
    /**
     * Update orbit camera
     */
    private fun updateOrbit() {
        // Orbit around a point
        val center = agentManager.state.position
        
        val offset = LLVector3(
            cos(pitch) * cos(yaw) * zoomDistance,
            cos(pitch) * sin(yaw) * zoomDistance,
            sin(pitch) * zoomDistance
        )
        
        targetPosition = center + offset
        targetLookAt = center
    }
    
    /**
     * Set camera mode
     */
    fun setMode(newMode: CameraMode) {
        mode = newMode
        
        when (mode) {
            CameraMode.FREE_CAMERA -> {
                // Initialize free camera at current position
                freeCameraPosition = position
                freeCameraRotation = calculateRotationFromLookAt()
            }
            else -> {}
        }
    }
    
    /**
     * Zoom camera in/out
     */
    fun zoom(delta: Float) {
        zoomDistance = (zoomDistance + delta).coerceIn(MIN_ZOOM_DISTANCE, MAX_ZOOM_DISTANCE)
    }
    
    /**
     * Rotate camera (pitch/yaw)
     */
    fun rotate(pitchDelta: Float, yawDelta: Float) {
        pitch = (pitch + pitchDelta).coerceIn(-PI.toFloat() / 2f, PI.toFloat() / 2f)
        yaw += yawDelta
        
        // Normalize yaw to [0, 2π]
        while (yaw < 0) yaw += 2f * PI.toFloat()
        while (yaw >= 2f * PI.toFloat()) yaw -= 2f * PI.toFloat()
    }
    
    /**
     * Pan free camera
     */
    fun panFreeCamera(right: Float, up: Float, forward: Float) {
        if (mode != CameraMode.FREE_CAMERA) return
        
        val rightVec = freeCameraRotation * LLVector3.y_axis
        val upVec = freeCameraRotation * LLVector3.z_axis
        val forwardVec = freeCameraRotation * LLVector3.x_axis
        
        freeCameraPosition = freeCameraPosition + rightVec * right
        freeCameraPosition = freeCameraPosition + upVec * up
        freeCameraPosition = freeCameraPosition + forwardVec * forward
    }
    
    /**
     * Rotate free camera
     */
    fun rotateFreeCamera(pitchDelta: Float, yawDelta: Float) {
        if (mode != CameraMode.FREE_CAMERA) return
        
        val pitchRot = LLQuaternion(pitchDelta, LLVector3.y_axis)
        val yawRot = LLQuaternion(yawDelta, LLVector3.z_axis)
        
        freeCameraRotation = yawRot * freeCameraRotation * pitchRot
        freeCameraRotation.normalize()
    }
    
    /**
     * Reset camera to default
     */
    fun reset() {
        mode = CameraMode.THIRD_PERSON
        zoomDistance = DEFAULT_ZOOM_DISTANCE
        pitch = 0f
        yaw = 0f
    }
    
    /**
     * Get view matrix
     */
    fun getViewMatrix(): FloatArray {
        val matrix = FloatArray(16)
        
        val forward = (lookAt - position).apply { normalize() }
        val right = (forward cross up).apply { normalize() }
        val newUp = right cross forward
        
        matrix[0] = right.x
        matrix[4] = right.y
        matrix[8] = right.z
        matrix[12] = -(right dot position)
        
        matrix[1] = newUp.x
        matrix[5] = newUp.y
        matrix[9] = newUp.z
        matrix[13] = -(newUp dot position)
        
        matrix[2] = -forward.x
        matrix[6] = -forward.y
        matrix[10] = -forward.z
        matrix[14] = forward dot position
        
        matrix[3] = 0f
        matrix[7] = 0f
        matrix[11] = 0f
        matrix[15] = 1f
        
        return matrix
    }
    
    /**
     * Get projection matrix
     */
    fun getProjectionMatrix(fov: Float, aspectRatio: Float, near: Float, far: Float): FloatArray {
        val matrix = FloatArray(16)
        
        val f = 1f / tan(fov / 2f)
        
        matrix[0] = f / aspectRatio
        matrix[5] = f
        matrix[10] = (far + near) / (near - far)
        matrix[11] = -1f
        matrix[14] = (2f * far * near) / (near - far)
        
        return matrix
    }
    
    /**
     * Calculate rotation quaternion from current look-at
     */
    private fun calculateRotationFromLookAt(): LLQuaternion {
        val forward = (lookAt - position).apply { normalize() }
        val right = (forward cross up).apply { normalize() }
        val newUp = right cross forward
        
        // Convert to quaternion
        return LLQuaternion().apply {
            setFromMatrix3(floatArrayOf(
                right.x, right.y, right.z,
                newUp.x, newUp.y, newUp.z,
                -forward.x, -forward.y, -forward.z
            ))
        }
    }
}
