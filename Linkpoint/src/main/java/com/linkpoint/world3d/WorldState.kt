package com.linkpoint.world3d

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion

private const val PI_F = kotlin.math.PI.toFloat()

/**
 * State holder for the 3D world scene.
 * 
 * Contains all dynamic data needed to render the world:
 * - Camera position and orientation
 * - Loaded models (avatars, objects, terrain)
 * - Lighting settings
 * - Environment/skybox
 * 
 * This class bridges SceneView rendering with libGDX game logic.
 */
class WorldState {
    
    // Camera state
    var cameraPosition by mutableStateOf(Float3(0f, 2f, 10f))
    var cameraTarget by mutableStateOf(Float3(0f, 0f, 0f))
    var cameraFov by mutableFloatStateOf(60f)
    
    // Lighting
    var sunIntensity by mutableFloatStateOf(100_000f)
    var sunDirection by mutableStateOf(Float3(0f, -1f, -0.8f))
    var sunColor by mutableStateOf(Float3(0.98f, 0.92f, 0.89f))
    
    // Environment
    var environmentHdrPath: String? by mutableStateOf(null)
    var skyboxEnabled by mutableStateOf(true)
    
    // Models in the scene
    val modelNodes = mutableStateListOf<WorldModelData>()
    
    // Avatar state
    var avatarPosition by mutableStateOf(Float3(128f, 128f, 25f))
    var avatarRotation by mutableStateOf(Quaternion())
    
    // World time (for day/night cycle)
    var worldTimeHours by mutableFloatStateOf(12f) // 0-24
    
    /**
     * Add a model to the scene
     */
    fun addModel(model: WorldModelData) {
        modelNodes.add(model)
    }
    
    /**
     * Remove a model from the scene
     */
    fun removeModel(modelId: String) {
        modelNodes.removeIf { it.id == modelId }
    }
    
    /**
     * Update a model's position
     */
    fun updateModelPosition(modelId: String, position: Float3) {
        val index = modelNodes.indexOfFirst { it.id == modelId }
        if (index >= 0) {
            modelNodes[index] = modelNodes[index].copy(position = position)
        }
    }
    
    /**
     * Update a model's rotation
     */
    fun updateModelRotation(modelId: String, rotation: Float3) {
        val index = modelNodes.indexOfFirst { it.id == modelId }
        if (index >= 0) {
            modelNodes[index] = modelNodes[index].copy(rotation = rotation)
        }
    }
    
    /**
     * Clear all models
     */
    fun clearModels() {
        modelNodes.clear()
    }
    
    /**
     * Update camera to follow avatar
     */
    fun updateCameraForAvatar(
        avatarPos: Float3,
        cameraDistance: Float = 5f,
        cameraHeight: Float = 2f
    ) {
        avatarPosition = avatarPos
        cameraPosition = Float3(
            avatarPos.x,
            avatarPos.y + cameraHeight,
            avatarPos.z + cameraDistance
        )
        cameraTarget = avatarPos
    }
    
    /**
     * Update sun position based on world time
     */
    fun updateSunForTime(hours: Float) {
        worldTimeHours = hours
        
        // Calculate sun angle (0 = midnight, 12 = noon)
        val sunAngle = (hours / 24f) * 2f * PI_F - (PI_F / 2f)
        
        sunDirection = Float3(
            kotlin.math.cos(sunAngle),
            -kotlin.math.abs(kotlin.math.sin(sunAngle)),
            -0.3f
        )
        
        // Adjust intensity based on time
        sunIntensity = when {
            hours < 6f || hours > 18f -> 10_000f  // Night
            hours < 8f || hours > 16f -> 50_000f  // Twilight
            else -> 100_000f  // Day
        }
        
        // Adjust color based on time
        sunColor = when {
            hours < 6f || hours > 18f -> Float3(0.3f, 0.3f, 0.5f)  // Night (blue)
            hours < 8f || hours > 16f -> Float3(1f, 0.7f, 0.5f)    // Twilight (orange)
            else -> Float3(0.98f, 0.92f, 0.89f)  // Day (warm white)
        }
    }
}

/**
 * Data class representing a 3D model in the world
 */
data class WorldModelData(
    val id: String,
    val assetPath: String,
    val position: Float3 = Float3(0f, 0f, 0f),
    val rotation: Float3 = Float3(0f, 0f, 0f),
    val scale: Float = 1f,
    val type: ModelType = ModelType.OBJECT,
    val metadata: Map<String, Any> = emptyMap()
)

/**
 * Types of models in the world
 */
enum class ModelType {
    AVATAR,
    OBJECT,
    TERRAIN,
    ATTACHMENT,
    PARTICLE
}

/**
 * Remember and create a WorldState instance
 */
@Composable
fun rememberWorldState(): WorldState {
    return remember { WorldState() }
}
