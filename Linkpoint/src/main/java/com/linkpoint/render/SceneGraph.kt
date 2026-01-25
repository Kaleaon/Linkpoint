package com.linkpoint.render

import com.linkpoint.protocol.scenery.SceneObject
import com.linkpoint.protocol.scenery.TerrainData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Scene graph that holds all renderable objects.
 * Connects protocol data to rendering system.
 * 
 * This class maintains the current state of the scene including:
 * - All objects in the scene (positions, rotations, metadata)
 * - Terrain mesh data
 * - Scene statistics
 * 
 * The scene graph is updated by protocol handlers (SceneDataHandler) and
 * observed by the rendering system to render the scene.
 */
class SceneGraph {
    
    companion object {
        private const val TAG = "SceneGraph"
    }
    
    /** All renderable objects by UUID */
    private val _objects = MutableStateFlow<Map<UUID, RenderableObject>>(emptyMap())
    val objects: StateFlow<Map<UUID, RenderableObject>> = _objects.asStateFlow()
    
    /** Terrain mesh data */
    private val _terrain = MutableStateFlow<TerrainMesh?>(null)
    val terrain: StateFlow<TerrainMesh?> = _terrain.asStateFlow()
    
    /** Scene statistics */
    private val _statistics = MutableStateFlow(SceneStatistics())
    val statistics: StateFlow<SceneStatistics> = _statistics.asStateFlow()
    
    /** Timestamp of last update */
    private var lastUpdateTime = 0L
    
    /**
     * Update an object in the scene graph
     * 
     * @param sceneObject The object data from protocol
     */
    fun updateObject(sceneObject: com.linkpoint.protocol.scenery.SceneObject) {
        _objects.value = _objects.value.toMutableMap().apply {
            val renderable = RenderableObject(
                id = sceneObject.id,
                name = sceneObject.name,
                description = sceneObject.description,
                position = Vector3(
                    sceneObject.position.x,
                    sceneObject.position.y,
                    sceneObject.position.z
                ),
                rotation = Quaternion(
                    sceneObject.rotation.w,
                    sceneObject.rotation.x,
                    sceneObject.rotation.y,
                    sceneObject.rotation.z
                ),
                isAvatar = sceneObject.pCode == 47
            )
            put(sceneObject.id, renderable)
        }
        
        lastUpdateTime = System.currentTimeMillis()
        updateStatistics()
    }
    
    /**
     * Update terrain data
     * 
     * @param terrainData The terrain data from protocol
     */
    fun updateTerrain(terrainData: TerrainData) {
        if (terrainData.hasTerrainData()) {
            _terrain.value = TerrainMesh(
                heightmap = terrainData.getHeightmap(),
                width = 256,
                depth = 256
            )
            
            lastUpdateTime = System.currentTimeMillis()
            updateStatistics()
        }
    }
    
    /**
     * Remove an object from the scene
     * 
     * @param objectId The UUID of the object to remove
     */
    fun removeObject(objectId: UUID) {
        _objects.value = _objects.value.toMutableMap().apply {
            remove(objectId)
        }
        
        lastUpdateTime = System.currentTimeMillis()
        updateStatistics()
    }
    
    /**
     * Clear all objects from the scene
     */
    fun clearObjects() {
        _objects.value = emptyMap()
        lastUpdateTime = System.currentTimeMillis()
        updateStatistics()
    }
    
    /**
     * Clear the entire scene (objects and terrain)
     */
    fun clearScene() {
        _objects.value = emptyMap()
        _terrain.value = null
        lastUpdateTime = System.currentTimeMillis()
        updateStatistics()
    }
    
    /**
     * Update scene statistics
     */
    private fun updateStatistics() {
        _statistics.value = SceneStatistics(
            objectCount = _objects.value.size,
            hasTerrain = _terrain.value != null,
            lastUpdateTime = lastUpdateTime
        )
    }
    
    /**
     * Get scene information as a string
     */
    fun getSceneInfo(): String {
        val objCount = _objects.value.size
        val terrain = if (_terrain.value != null) "Yes" else "No"
        return "Objects: $objCount, Terrain: $terrain, Last Update: ${lastUpdateTime}ms ago"
    }
}

// ==================== Data Classes ====================

/**
 * Renderable object data for the rendering system
 */
data class RenderableObject(
    val id: java.util.UUID,
    val name: String,
    val description: String,
    val position: Vector3,
    val rotation: Quaternion,
    val isAvatar: Boolean = false
)

/**
 * 3D Vector for rendering
 */
data class Vector3(val x: Float, val y: Float, val z: Float) {
    companion object {
        val ZERO = Vector3(0f, 0f, 0f)
        val UP = Vector3(0f, 1f, 0f)
        val FORWARD = Vector3(0f, 0f, -1f)
    }
    
    fun distanceTo(other: Vector3): Float {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return Math.sqrt((dx * dx + dy * dy + dz * dz).toDouble()).toFloat()
    }
}

/**
 * Quaternion for rendering
 */
data class Quaternion(val w: Float, val x: Float, val y: Float, val z: Float) {
    companion object {
        val IDENTITY = Quaternion(1f, 0f, 0f, 0f)
    }
}

/**
 * Terrain mesh data for rendering
 */
data class TerrainMesh(
    val heightmap: FloatArray,
    val width: Int,
    val depth: Int
) {
    /**
     * Get height at normalized coordinates (0-1)
     */
    fun getHeightAt(normalizedX: Float, normalizedZ: Float): Float {
        val x = (normalizedX * (width - 1)).toInt().coerceIn(0, width - 1)
        val z = (normalizedZ * (depth - 1)).toInt().coerceIn(0, depth - 1)
        return heightmap[z * width + x]
    }
}

/**
 * Scene statistics
 */
data class SceneStatistics(
    val objectCount: Int = 0,
    val hasTerrain: Boolean = false,
    val lastUpdateTime: Long = 0L
) {
    companion object {
        val EMPTY = SceneStatistics()
    }
}