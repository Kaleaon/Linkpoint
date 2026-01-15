package com.linkpoint.assets

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.zip.Inflater

/**
 * Manages mesh asset downloading and parsing
 * Handles Second Life mesh format (LLMESH)
 */
class MeshManager(
    private val cache: AssetCache,
    private val capabilityManager: CapabilityManager
) {
    companion object {
        private const val TAG = "MeshManager"
    }
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
    
    private val pendingMeshes = ConcurrentHashMap<UUID, Deferred<MeshData?>>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    /**
     * Get mesh data (cached or download)
     */
    suspend fun getMesh(meshId: UUID, lod: MeshLOD = MeshLOD.HIGH): MeshData? {
        // Check cache
        cache.get(meshId, AssetType.MESH)?.let { data ->
            return parseMesh(meshId, data, lod)
        }
        
        // Check pending
        pendingMeshes[meshId]?.let { return it.await() }
        
        // Download
        val deferred = scope.async {
            downloadAndParseMesh(meshId, lod)
        }
        pendingMeshes[meshId] = deferred
        
        return try {
            deferred.await()
        } finally {
            pendingMeshes.remove(meshId)
        }
    }
    
    private suspend fun downloadAndParseMesh(meshId: UUID, lod: MeshLOD): MeshData? {
        val meshUrl = capabilityManager.getCapability(CapabilityManager.CAP_GET_MESH2)
            ?: capabilityManager.getCapability(CapabilityManager.CAP_GET_MESH)
        
        if (meshUrl == null) {
            lastError = "No mesh capability available"
            lastErrorTime = System.currentTimeMillis()
            downloadFailCount.incrementAndGet()
            return null
        }
        
        val url = "$meshUrl?mesh_id=$meshId"
        
        try {
            val request = Request.Builder().url(url).build()
            val response = httpClient.newCall(request).execute()
            
            if (!response.isSuccessful) {
                Log.w(TAG, "Mesh download failed: ${response.code}")
                lastError = "HTTP ${response.code}: ${response.message}"
                lastErrorTime = System.currentTimeMillis()
                downloadFailCount.incrementAndGet()
                return null
            }
            
            val data = response.body?.bytes()
            if (data == null) {
                lastError = "Empty response body"
                lastErrorTime = System.currentTimeMillis()
                downloadFailCount.incrementAndGet()
                return null
            }
            
            downloadCount.incrementAndGet()
            downloadedBytes.addAndGet(data.size.toLong())
            
            // Cache raw data
            cache.put(meshId, AssetType.MESH, data)
            
            return parseMesh(meshId, data, lod)
        } catch (e: Exception) {
            Log.e(TAG, "Mesh download error: $meshId", e)
            lastError = "${e.javaClass.simpleName}: ${e.message}"
            lastErrorTime = System.currentTimeMillis()
            downloadFailCount.incrementAndGet()
            return null
        }
    }
    
    private fun parseMesh(meshId: UUID, data: ByteArray, lod: MeshLOD): MeshData? {
        try {
            // Parse mesh header (LLSD)
            val headerEnd = findHeaderEnd(data)
            if (headerEnd < 0) return null
            
            val headerBytes = data.copyOfRange(0, headerEnd)
            val header = LLSDParser.parseBinary(headerBytes)
            
            if (header !is LLSDMap) {
                lastError = "Invalid mesh header - not an LLSDMap"
                lastErrorTime = System.currentTimeMillis()
                parseFailCount.incrementAndGet()
                return null
            }
            
            // Get LOD data offset/size
            val lodKey = when (lod) {
                MeshLOD.HIGHEST -> "high_lod"
                MeshLOD.HIGH -> "medium_lod"
                MeshLOD.MEDIUM -> "low_lod"
                MeshLOD.LOW -> "lowest_lod"
            }
            
            val lodMap = header.getMap(lodKey) ?: header.getMap("high_lod")
            if (lodMap == null) {
                lastError = "Missing LOD map in mesh header"
                lastErrorTime = System.currentTimeMillis()
                parseFailCount.incrementAndGet()
                return null
            }
            
            val offset = lodMap.getInt("offset")
            val size = lodMap.getInt("size")
            if (offset == null || size == null) {
                lastError = "Missing offset/size in LOD map"
                lastErrorTime = System.currentTimeMillis()
                parseFailCount.incrementAndGet()
                return null
            }
            
            // Extract and decompress LOD data
            val compressedData = data.copyOfRange(headerEnd + offset, headerEnd + offset + size)
            val decompressed = decompress(compressedData)
            
            // Parse mesh geometry
            return parseMeshGeometry(meshId, decompressed, header)
        } catch (e: Exception) {
            Log.e(TAG, "Mesh parse error: $meshId", e)
            lastError = "Parse: ${e.javaClass.simpleName}: ${e.message}"
            lastErrorTime = System.currentTimeMillis()
            parseFailCount.incrementAndGet()
            return null
        }
    }
    
    private fun findHeaderEnd(data: ByteArray): Int {
        // Find end of LLSD header (binary LLSD ends with specific markers)
        for (i in 0 until minOf(data.size, 65536)) {
            if (data[i] == '}' .code.toByte()) {
                return i + 1
            }
        }
        return -1
    }
    
    private fun decompress(data: ByteArray): ByteArray {
        val inflater = Inflater()
        inflater.setInput(data)
        
        val buffer = ByteArray(data.size * 10) // Estimate
        val resultStream = java.io.ByteArrayOutputStream()
        
        while (!inflater.finished()) {
            val count = inflater.inflate(buffer)
            if (count == 0) break
            resultStream.write(buffer, 0, count)
        }
        
        inflater.end()
        return resultStream.toByteArray()
    }
    
    private fun parseMeshGeometry(meshId: UUID, data: ByteArray, header: LLSDMap): MeshData {
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        
        val faces = mutableListOf<MeshFace>()
        
        // Parse each face
        while (buffer.hasRemaining()) {
            try {
                // Vertex count
                val vertexCount = buffer.short.toInt() and 0xFFFF
                if (vertexCount == 0) break
                
                // Positions (quantized)
                val positions = FloatArray(vertexCount * 3)
                for (i in 0 until vertexCount) {
                    positions[i * 3] = buffer.short / 32767f
                    positions[i * 3 + 1] = buffer.short / 32767f
                    positions[i * 3 + 2] = buffer.short / 32767f
                }
                
                // Normals (quantized)
                val normals = FloatArray(vertexCount * 3)
                for (i in 0 until vertexCount) {
                    normals[i * 3] = buffer.short / 32767f
                    normals[i * 3 + 1] = buffer.short / 32767f
                    normals[i * 3 + 2] = buffer.short / 32767f
                }
                
                // UVs (quantized)
                val uvs = FloatArray(vertexCount * 2)
                for (i in 0 until vertexCount) {
                    uvs[i * 2] = buffer.short / 32767f
                    uvs[i * 2 + 1] = buffer.short / 32767f
                }
                
                // Index count
                val indexCount = buffer.short.toInt() and 0xFFFF
                
                // Indices
                val indices = ShortArray(indexCount)
                for (i in 0 until indexCount) {
                    indices[i] = buffer.short
                }
                
                faces.add(MeshFace(
                    positions = positions,
                    normals = normals,
                    uvs = uvs,
                    indices = indices
                ))
            } catch (e: Exception) {
                break
            }
        }
        
        // Get skin/rig data if present
        val skinData = header.getMap("skin")?.let { parseSkinData(it) }
        
        return MeshData(
            meshId = meshId,
            faces = faces,
            skinData = skinData
        )
    }
    
    private fun parseSkinData(skinMap: LLSDMap): SkinData? {
        try {
            val jointNames = skinMap.getArray("joint_names")?.value?.mapNotNull { 
                (it as? LLSDString)?.value 
            } ?: return null
            
            val bindShapeMatrix = skinMap.getArray("bind_shape_matrix")?.value?.mapNotNull {
                (it as? LLSDReal)?.value?.toFloat()
            }?.toFloatArray() ?: FloatArray(16)
            
            val inverseBindMatrices = skinMap.getArray("inverse_bind_matrix")?.value?.mapNotNull { row ->
                (row as? LLSDArray)?.value?.mapNotNull { (it as? LLSDReal)?.value?.toFloat() }?.toFloatArray()
            } ?: emptyList()
            
            return SkinData(
                jointNames = jointNames,
                bindShapeMatrix = bindShapeMatrix,
                inverseBindMatrices = inverseBindMatrices
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse skin data", e)
            return null
        }
    }
    
    fun shutdown() {
        scope.cancel()
        pendingMeshes.clear()
    }
    
    // ==================== DIAGNOSTIC METHODS ====================
    
    // Tracking variables for diagnostics
    private val downloadCount = java.util.concurrent.atomic.AtomicInteger(0)
    private val downloadFailCount = java.util.concurrent.atomic.AtomicInteger(0)
    private val parseFailCount = java.util.concurrent.atomic.AtomicInteger(0)
    private val downloadedBytes = java.util.concurrent.atomic.AtomicLong(0)
    private var lastError: String? = null
    private var lastErrorTime: Long = 0
    
    /**
     * Get comprehensive diagnostic data for debug reports
     */
    fun getDiagnostics(): MeshManagerDiagnostics {
        val getMeshCap = capabilityManager.getCapability(CapabilityManager.CAP_GET_MESH2)
            ?: capabilityManager.getCapability(CapabilityManager.CAP_GET_MESH)
        
        return MeshManagerDiagnostics(
            pendingDownloads = pendingMeshes.size,
            downloadedCount = downloadCount.get(),
            downloadedBytes = downloadedBytes.get(),
            downloadFailedCount = downloadFailCount.get(),
            parseFailedCount = parseFailCount.get(),
            hasMeshCapability = getMeshCap != null,
            lastError = lastError,
            lastErrorTimeAgo = if (lastErrorTime > 0) System.currentTimeMillis() - lastErrorTime else null
        )
    }
    
    /**
     * Diagnostic data class for mesh manager state
     */
    data class MeshManagerDiagnostics(
        val pendingDownloads: Int,
        val downloadedCount: Int,
        val downloadedBytes: Long,
        val downloadFailedCount: Int,
        val parseFailedCount: Int,
        val hasMeshCapability: Boolean,
        val lastError: String?,
        val lastErrorTimeAgo: Long?
    )

enum class MeshLOD {
    HIGHEST, HIGH, MEDIUM, LOW
}

data class MeshData(
    val meshId: UUID,
    val faces: List<MeshFace>,
    val skinData: SkinData? = null
)

data class MeshFace(
    val positions: FloatArray,
    val normals: FloatArray,
    val uvs: FloatArray,
    val indices: ShortArray
) {
    val vertexCount: Int get() = positions.size / 3
    val indexCount: Int get() = indices.size
}

data class SkinData(
    val jointNames: List<String>,
    val bindShapeMatrix: FloatArray,
    val inverseBindMatrices: List<FloatArray>
)
