package com.linkpoint.protocol.scenery

import android.util.Log
import com.linkpoint.network.NetworkLogger
import com.linkpoint.protocol.messages.MessageParser
import com.linkpoint.protocol.messages.ObjectUpdateData
import com.linkpoint.protocol.messages.PacketCodec
import com.linkpoint.render.RenderQueue
import com.linkpoint.render.RenderableUpdate
import com.linkpoint.render.SceneGraph
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Handles scene data messages from Second Life server.
 * Based on the reference viewer's scene data handlers in SLAgentCircuit.
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
    private val sceneGraph: SceneGraph? = null,
    private val renderQueue: RenderQueue? = null
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
            if (renderQueue != null) {
                val heightmapCopy = terrainData.getHeightmap().copyOf()
                renderQueue.enqueue(RenderableUpdate.TerrainUpdate(heightmapCopy, 256, 256))
            } else {
                sceneGraph?.updateTerrain(terrainData)
            }
            
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
    fun handleObjectUpdate(rawPacket: ByteArray): Boolean {
        try {
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                "Processing ObjectUpdate (${rawPacket.size} bytes)")

            // rawPacket includes the full LLUDP header; extract the body before parsing.
            val payload = MessageParser.extractPayload(rawPacket)
            if (payload == null) {
                NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP,
                    "ObjectUpdate: failed to extract payload")
                return false
            }

            val updates: List<ObjectUpdateData> = MessageParser.parseObjectUpdate(payload)
            var objectCount = 0
            for (update in updates) {
                val obj = SceneObject(
                    id = update.fullId,
                    position = Vector3(update.position.x, update.position.y, update.position.z),
                    rotation = Quaternion(
                        update.rotation.w, update.rotation.x,
                        update.rotation.y, update.rotation.z
                    ),
                    pCode = update.pcode
                )
                // Merge any pending properties received ahead of this update.
                val pending = pendingProperties.remove(obj.id)
                if (pending != null) {
                    obj.name = pending.name
                    obj.description = pending.description
                }
                objects[obj.id] = obj
                objectCount++
                if (renderQueue != null) {
                    renderQueue.enqueue(RenderableUpdate.SceneObjectUpdate(obj.copy()))
                } else {
                    sceneGraph?.updateObject(obj)
                }
                NetworkLogger.log(NetworkLogger.Level.VERBOSE, NetworkLogger.Category.UDP,
                    "Updated object: ${obj.name} (${obj.id})")
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
                val codec = PacketCodec.fromBuffer(buffer)
                val objectId = codec.readUuid()
                val name = codec.readVariable1String()
                val description = codec.readVariable1String()
                
                // Update object properties
                val obj = objects[objectId]
                if (obj != null) {
                    obj.name = name
                    obj.description = description
                    
                    NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                        "Updated properties: $name (${objectId})")
                    
                    // Update scene graph
                    if (renderQueue != null) {
                        renderQueue.enqueue(RenderableUpdate.SceneObjectUpdate(obj.copy()))
                    } else {
                        sceneGraph?.updateObject(obj)
                    }
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
