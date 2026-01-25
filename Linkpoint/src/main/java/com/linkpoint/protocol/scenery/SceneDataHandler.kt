package com.linkpoint.protocol.scenery

import android.util.Log
import com.linkpoint.network.NetworkLogger
import com.linkpoint.render.SceneGraph
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Handles scene data messages from Second Life server.
 * Based on Lumiya's scene data handlers in SLAgentCircuit.
 * 
 * This class processes scene data messages including:
 * - LayerData: Terrain heightmap data
 * - ObjectUpdate: Object position, rotation, scale
 * - ObjectProperties: Object metadata (name, description, etc.)
 * 
 * The parsed data is stored and made available to the rendering system
 * through the SceneGraph integration.
 */
class SceneDataHandler(
    private val sceneGraph: SceneGraph? = null
) {
    
    companion object {
        private const val TAG = "SceneDataHandler"
        
        // Layer types
        private const val LAYER_TYPE_TERRAIN = 76
    }
    
    /** Terrain data storage */
    private val terrainData = TerrainData()
    
    /** Object storage by UUID */
    private val objects = ConcurrentHashMap<UUID, SceneObject>()
    
    /** Objects pending property updates */
    private val pendingProperties = ConcurrentHashMap<UUID, SceneObject>()
    
    /** Count of processed messages for statistics */
    var layerDataCount = 0
    var objectUpdateCount = 0
    var objectPropertiesCount = 0
    
    /**
     * Handle LayerData message
     * Contains terrain heightmap data
     * 
     * @param data Raw message payload
     * @return true if handled successfully
     */
    fun handleLayerData(data: ByteArray): Boolean {
        try {
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                "Processing LayerData (${data.size} bytes)")
            
            val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
            
            // Parse LayerData message format
            val layerType = buffer.get().toInt() and 0xFF
            val layerId = buffer.get().toInt() and 0xFF
            
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                "Layer: type=$layerType, id=$layerId")
            
            // Only process terrain layer (type 76)
            if (layerType != LAYER_TYPE_TERRAIN) {
                NetworkLogger.log(NetworkLogger.Level.VERBOSE, NetworkLogger.Category.UDP,
                    "Skipping non-terrain layer type: $layerType")
                return true
            }
            
            // Extract heightmap data
            val dataSize = buffer.remaining()
            val heightmapData = ByteArray(dataSize)
            buffer.get(heightmapData)
            
            // Process terrain data
            terrainData.processLayerData(heightmapData)
            
            layerDataCount++
            
            NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
                "✓ Terrain data processed: ${heightmapData.size} bytes (total: $layerDataCount layers)")
            
            // Update scene graph if provided
            sceneGraph?.updateTerrain(terrainData)
            
            return true
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                "Error processing LayerData: ${e.message}")
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * Handle ObjectUpdate message
     * Contains object position, rotation, scale
     * 
     * @param data Raw message payload
     * @return true if handled successfully
     */
    fun handleObjectUpdate(data: ByteArray): Boolean {
        try {
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                "Processing ObjectUpdate (${data.size} bytes)")
            
            val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
            
            // Parse ObjectUpdate message format
            val regionHandle = buffer.long
            val timeDilation = buffer.short
            
            // Parse each object in the update
            var objectCount = 0
            while (buffer.remaining() > 0) {
                // Parse object header
                val pCode = buffer.get().toInt() and 0xFF
                
                // Only process avatars (pCode=47) and prims (pCode=9)
                if (pCode == 47 || pCode == 9) {
                    val obj = parseObjectUpdate(buffer, pCode)
                    if (obj != null) {
                        // Check if there are pending properties to apply
                        val pending = pendingProperties.remove(obj.id)
                        if (pending != null) {
                            obj.name = pending.name
                            obj.description = pending.description
                        }
                        
                        objects[obj.id] = obj
                        objectCount++
                        
                        // Update scene graph
                        sceneGraph?.updateObject(obj)
                        
                        NetworkLogger.log(NetworkLogger.Level.VERBOSE, NetworkLogger.Category.UDP,
                            "Updated object: ${obj.name} (${obj.id})")
                    }
                } else if (pCode == 0) {
                    // End of objects
                    break
                } else {
                    // Skip unknown object types
                    NetworkLogger.log(NetworkLogger.Level.VERBOSE, NetworkLogger.Category.UDP,
                        "Skipping object with pCode=$pCode")
                    break
                }
            }
            
            objectUpdateCount++
            
            NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
                "✓ Updated $objectCount objects (total: $objectUpdateCount updates)")
            
            return true
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                "Error processing ObjectUpdate: ${e.message}")
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * Handle ObjectProperties message
     * Contains object metadata (name, description, etc.)
     * 
     * @param data Raw message payload
     * @return true if handled successfully
     */
    fun handleObjectProperties(data: ByteArray): Boolean {
        try {
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                "Processing ObjectProperties (${data.size} bytes)")
            
            val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
            
            // Parse ObjectProperties message format
            val requestID = buffer.int
            val objectCount = buffer.get().toInt() and 0xFF
            
            for (i in 0 until objectCount) {
                val objectId = readUUID(buffer)
                val name = readVariableString(buffer)
                val description = readVariableString(buffer)
                
                // Update object properties
                val obj = objects[objectId]
                if (obj != null) {
                    obj.name = name
                    obj.description = description
                    
                    NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                        "Updated properties: $name (${objectId})")
                    
                    // Update scene graph
                    sceneGraph?.updateObject(obj)
                } else {
                    // Object not yet received, store pending
                    pendingProperties[objectId] = SceneObject(objectId).apply {
                        this.name = name
                        this.description = description
                    }
                    
                    NetworkLogger.log(NetworkLogger.Level.VERBOSE, NetworkLogger.Category.UDP,
                        "Stored pending properties for: $name (${objectId})")
                }
            }
            
            objectPropertiesCount++
            
            NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
                "✓ Updated properties for $objectCount objects (total: $objectPropertiesCount updates)")
            
            return true
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                "Error processing ObjectProperties: ${e.message}")
            e.printStackTrace()
            return false
        }
    }
    
    // ==================== Helper Methods ====================
    
    private fun parseObjectUpdate(buffer: ByteBuffer, pCode: Int): SceneObject? {
        return try {
            // Parse object ID
            val objectId = readUUID(buffer)
            
            // Parse state
            val state = buffer.get().toInt() and 0xFF
            
            // Parse position (Vector3)
            val x = buffer.float
            val y = buffer.float
            val z = buffer.float
            
            // Parse rotation (Quaternion)
            val rw = buffer.float
            val rx = buffer.float
            val ry = buffer.float
            val rz = buffer.float
            
            // Create object
            SceneObject(
                id = objectId,
                position = Vector3(x, y, z),
                rotation = Quaternion(rw, rx, ry, rz),
                pCode = pCode
            )
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP,
                "Failed to parse object: ${e.message}")
            null
        }
    }
    
    private fun readUUID(buffer: ByteBuffer): UUID {
        val msb = buffer.long
        val lsb = buffer.long
        return UUID(msb, lsb)
    }
    
    private fun readVariableString(buffer: ByteBuffer): String {
        val length = buffer.get().toInt() and 0xFF
        if (length == 0) return ""
        
        val bytes = ByteArray(length)
        buffer.get(bytes)
        return String(bytes, Charsets.UTF_8)
    }
    
    /**
     * Get terrain data for rendering
     */
    fun getTerrainData(): TerrainData = terrainData
    
    /**
     * Get all objects for rendering
     */
    fun getAllObjects(): Map<UUID, SceneObject> = objects.toMap()
    
    /**
     * Get statistics
     */
    fun getStatistics(): Map<String, Int> {
        return mapOf(
            "layerDataCount" to layerDataCount,
            "objectUpdateCount" to objectUpdateCount,
            "objectPropertiesCount" to objectPropertiesCount,
            "totalObjects" to objects.size
        )
    }
}

// ==================== Data Classes ====================

/**
 * 3D Vector for position and scale
 */
data class Vector3(val x: Float, val y: Float, val z: Float) {
    companion object {
        val ZERO = Vector3(0f, 0f, 0f)
        val IDENTITY = Vector3(1f, 1f, 1f)
    }
}

/**
 * Quaternion for rotation
 */
data class Quaternion(val w: Float, val x: Float, val y: Float, val z: Float) {
    companion object {
        val IDENTITY = Quaternion(1f, 0f, 0f, 0f)
    }
}

/**
 * Scene object data
 */
data class SceneObject(
    val id: UUID,
    val position: Vector3 = Vector3.ZERO,
    val rotation: Quaternion = Quaternion.IDENTITY,
    var name: String = "",
    var description: String = "",
    val pCode: Int = 9
)

/**
 * Terrain data with heightmap
 */
class TerrainData {
    private val heightmap = FloatArray(256 * 256) // 16x16 patches of 16x16 points
    private var hasData = false
    
    /**
     * Process layer data and extract heightmap
     */
    fun processLayerData(data: ByteArray) {
        // Parse heightmap data
        // Format: 16-bit signed integers for each height point
        for (i in data.indices step 2) {
            if (i + 1 < data.size && (i / 2) < heightmap.size) {
                val value = ((data[i].toInt() and 0xFF) or 
                             ((data[i + 1].toInt() and 0xFF) shl 8)).toShort()
                heightmap[i / 2] = value.toFloat()
            }
        }
        hasData = true
    }
    
    /**
     * Get heightmap data
     */
    fun getHeightmap(): FloatArray = heightmap
    
    /**
     * Check if terrain data has been loaded
     */
    fun hasTerrainData(): Boolean = hasData
    
    /**
     * Get height at specific grid position
     */
    fun getHeightAt(x: Int, z: Int): Float {
        if (x < 0 || x >= 256 || z < 0 || z >= 256) return 0f
        return heightmap[z * 256 + x]
    }
}