package com.linkpoint.assets

import android.content.Context
import android.util.Log
import com.linkpoint.network.CronetHttpClient
import com.linkpoint.network.CronetResult
import com.linkpoint.network.SSLHelper
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Protocol
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
 * 
 * Note: Uses custom SSL configuration to handle Akamai CDN hostname verification.
 * The Second Life asset CDN is served by Akamai, which uses certificates for
 * *.akamaized.net domains. The SSLHelper.configureForCdn() method handles this
 * hostname mismatch securely.
 */
class MeshManager(
    private val context: Context,
    private val cache: AssetCache,
    private val capabilityManager: CapabilityManager
) {
    companion object {
        private const val TAG = "MeshManager"
    }
    
    // HTTP client configured for CDN access with custom hostname verification.
    // HTTP/2 enabled (with HTTP/1.1 fallback) — see TextureManager for rationale; meshes
    // come from the same Akamai CDN and benefit from the same H2 multiplexing.
    private val httpClient = SSLHelper.configureForCdn(
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
    ).build()
    
    /**
     * Pending downloads keyed by (meshId, lod). Previously this was
     * keyed only by meshId, which meant two concurrent callers asking
     * for different LODs of the same mesh would have the second one
     * await the first's Deferred and receive the FIRST caller's LOD —
     * silently wrong geometry. Cache lookup uses raw bytes so the LOD
     * caching path stays correct.
     */
    private val pendingMeshes = ConcurrentHashMap<Pair<UUID, MeshLOD>, Deferred<MeshData?>>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Meshes queued for retry when capability becomes available
    private data class PendingMeshRequest(val meshId: UUID, val lod: MeshLOD)
    private val capabilityPendingMeshes = java.util.concurrent.ConcurrentLinkedQueue<PendingMeshRequest>()
    @Volatile private var capabilityRetryJob: Job? = null

    /**
     * Get mesh data (cached or download).
     */
    suspend fun getMesh(meshId: UUID, lod: MeshLOD = MeshLOD.HIGH): MeshData? {
        // Check cache (raw bytes cache is LOD-independent — parsing happens per call)
        cache.get(meshId, AssetType.MESH)?.let { data ->
            return parseMesh(meshId, data, lod)
        }

        val key = meshId to lod
        // Check pending — keyed by (mesh, lod) so concurrent different-LOD
        // requests don't share a Deferred from a different LOD.
        pendingMeshes[key]?.let { return it.await() }

        val deferred = scope.async {
            downloadAndParseMesh(meshId, lod)
        }
        pendingMeshes[key] = deferred

        return try {
            deferred.await()
        } finally {
            pendingMeshes.remove(key)
        }
    }

    private suspend fun downloadAndParseMesh(meshId: UUID, lod: MeshLOD): MeshData? {
        val meshUrl = capabilityManager.getCapability(CapabilityManager.CAP_GET_MESH2)
            ?: capabilityManager.getCapability(CapabilityManager.CAP_GET_MESH)

        if (meshUrl == null) {
            Log.w(TAG, "Mesh download queued for retry: $meshId - No mesh capability available yet")
            lastError = "No mesh capability available"
            lastErrorTime = System.currentTimeMillis()
            downloadFailCount.incrementAndGet()
            capabilityPendingMeshes.offer(PendingMeshRequest(meshId, lod))
            ensureMeshCapabilityRetryStarted()
            return null
        }

        val url = "$meshUrl?mesh_id=$meshId"

        // Cronet primary path: gets us HTTP/3 (QUIC) for the bulk of mesh
        // transfer when the CDN advertises it. Falls through to OkHttp
        // on per-request failure or engine-unavailable. Same pattern as
        // TextureManager.downloadTexture.
        val cronet = CronetHttpClient.getOrCreate(context)
        if (cronet.isAvailable) {
            val cronetResult = cronet.get(url, timeoutMs = 60_000L)
            if (cronetResult is CronetResult.Success && cronetResult.code in 200..299) {
                Log.d(TAG, "Mesh downloaded via Cronet/${cronetResult.protocol}: $meshId (${cronetResult.body.size} bytes)")
                downloadCount.incrementAndGet()
                downloadedBytes.addAndGet(cronetResult.body.size.toLong())
                cache.put(meshId, AssetType.MESH, cronetResult.body)
                return parseMesh(meshId, cronetResult.body, lod)
            }
            if (cronetResult is CronetResult.Failure) {
                Log.d(TAG, "Cronet path failed for mesh $meshId (${cronetResult.message}); falling back to OkHttp")
            }
        }

        try {
            val request = Request.Builder().url(url).build()
            val response = httpClient.newCall(request).execute()

            try {
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
                cache.put(meshId, AssetType.MESH, data)
                return parseMesh(meshId, data, lod)
            } finally {
                // Always close to release the connection back to the pool —
                // matches the TextureManager fix for "ProtocolException:
                // Unexpected status line" caused by leaked connections.
                response.close()
            }
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
            // Parse the mesh header. The previous implementation scanned
            // for the first '}' (0x7D) byte in the first 64KB and treated
            // that position as the header boundary — which is wrong any
            // time the header itself contains an LLSDBinary value whose
            // payload happens to include 0x7D (very common, e.g. UUIDs
            // or quantised binary blobs). This version uses the LLSD
            // parser's own consumed-byte count, which is the only
            // correct way to find the boundary.
            val (headerValue, headerEnd) = LLSDParser.parseBinaryAndConsumed(data)
            if (headerEnd <= 0) {
                lastError = "Mesh header parse failed"
                lastErrorTime = System.currentTimeMillis()
                parseFailCount.incrementAndGet()
                return null
            }
            val header = headerValue as? LLSDMap ?: run {
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
    
    private fun decompress(data: ByteArray): ByteArray {
        // Stream into a fixed-size chunk buffer rather than pre-allocating
        // `compressedSize * 10` upfront. Mesh LOD blobs commonly have a
        // 5-15× compression ratio so a 16 KB buffer keeps the working
        // set bounded even for the largest assets, while still allowing
        // unbounded output via ByteArrayOutputStream growth.
        val inflater = Inflater()
        try {
            inflater.setInput(data)
            val buffer = ByteArray(16 * 1024)
            val resultStream = java.io.ByteArrayOutputStream(data.size * 4)
            while (!inflater.finished()) {
                val count = inflater.inflate(buffer)
                if (count == 0) {
                    // Either inflater needs more input (shouldn't happen
                    // here since we provided the full payload) or we hit
                    // the end of the stream — break either way to avoid
                    // an infinite loop on malformed assets.
                    break
                }
                resultStream.write(buffer, 0, count)
            }
            return resultStream.toByteArray()
        } finally {
            inflater.end()
        }
    }
    
    private fun parseMeshGeometry(meshId: UUID, data: ByteArray, header: LLSDMap): MeshData {
        // Each LOD blob, after zlib decompression, is itself an LLSD payload:
        // an array of submesh maps. Each submesh has Position / Normal /
        // TexCoord0 binary blobs of U16-quantised values, plus PositionDomain /
        // TexCoord0Domain for de-quantisation, plus TriangleList. The previous
        // implementation read fixed-stride raw bytes off the start of the
        // blob, which never matched a real SL mesh and silently produced
        // garbage geometry. Reference: LL viewer
        // indra/llprimitive/llmodel.cpp readDecomposition / readModel.
        val faces = mutableListOf<MeshFace>()
        try {
            val lodValue = LLSDParser.parseBinary(data)
            val submeshes = (lodValue as? LLSDArray)?.value ?: emptyList()
            for (sub in submeshes) {
                val submesh = sub as? LLSDMap ?: continue
                val face = parseSubmesh(submesh) ?: continue
                faces.add(face)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Mesh $meshId LLSD-array LOD parse failed: ${e.message}")
        }

        // If LLSD-array parsing yielded nothing, fall back to the legacy
        // fixed-stride layout in case some asset uses it. This is best-effort
        // and almost never produces correct geometry, but it preserves
        // behaviour for any asset that might depend on it.
        if (faces.isEmpty()) {
            faces.addAll(parseLegacyFixedStride(data))
        }

        val skinData = header.getMap("skin")?.let { parseSkinData(it) }
        return MeshData(meshId = meshId, faces = faces, skinData = skinData)
    }

    /**
     * Parse one submesh (= one face's worth of geometry). Returns null if the
     * submesh is unrenderable (no position blob or no triangle list).
     */
    private fun parseSubmesh(map: LLSDMap): MeshFace? {
        val posBytes = (map.value["Position"] as? LLSDBinary)?.value ?: return null
        val triBytes = (map.value["TriangleList"] as? LLSDBinary)?.value ?: return null
        val posDomain = map.getMap("PositionDomain")
        val texDomain = map.getMap("TexCoord0Domain")

        val posMin = readDomainVec(posDomain, "Min", default = floatArrayOf(-0.5f, -0.5f, -0.5f))
        val posMax = readDomainVec(posDomain, "Max", default = floatArrayOf(0.5f, 0.5f, 0.5f))
        val texMin = readDomainVec(texDomain, "Min", default = floatArrayOf(0f, 0f))
        val texMax = readDomainVec(texDomain, "Max", default = floatArrayOf(1f, 1f))

        // Position: 3 × U16 per vertex.
        val vertexCount = posBytes.size / 6
        if (vertexCount == 0) return null
        val positions = FloatArray(vertexCount * 3)
        run {
            val bb = ByteBuffer.wrap(posBytes).order(ByteOrder.LITTLE_ENDIAN)
            for (i in 0 until vertexCount) {
                positions[i * 3]     = dequantU16(bb.short, posMin[0], posMax[0])
                positions[i * 3 + 1] = dequantU16(bb.short, posMin[1], posMax[1])
                positions[i * 3 + 2] = dequantU16(bb.short, posMin[2], posMax[2])
            }
        }

        // Normal: optional. Synthesise flat normals if missing.
        val normalBytes = (map.value["Normal"] as? LLSDBinary)?.value
        val normals = FloatArray(vertexCount * 3)
        if (normalBytes != null && normalBytes.size >= vertexCount * 6) {
            val bb = ByteBuffer.wrap(normalBytes).order(ByteOrder.LITTLE_ENDIAN)
            for (i in 0 until vertexCount) {
                // Normals are quantised in [-1, 1].
                normals[i * 3]     = dequantU16(bb.short, -1f, 1f)
                normals[i * 3 + 1] = dequantU16(bb.short, -1f, 1f)
                normals[i * 3 + 2] = dequantU16(bb.short, -1f, 1f)
            }
        } else {
            // Default to +Z so the lit material at least gets shaded; not
            // ideal, but better than zero normals which produce black faces.
            for (i in 0 until vertexCount) {
                normals[i * 3 + 2] = 1f
            }
        }

        // TexCoord0: optional.
        val uvBytes = (map.value["TexCoord0"] as? LLSDBinary)?.value
        val uvs = FloatArray(vertexCount * 2)
        if (uvBytes != null && uvBytes.size >= vertexCount * 4) {
            val bb = ByteBuffer.wrap(uvBytes).order(ByteOrder.LITTLE_ENDIAN)
            for (i in 0 until vertexCount) {
                uvs[i * 2]     = dequantU16(bb.short, texMin[0], texMax[0])
                uvs[i * 2 + 1] = dequantU16(bb.short, texMin[1], texMax[1])
            }
        }

        // Triangle list: U16 indices.
        val indexCount = triBytes.size / 2
        val indices = ShortArray(indexCount)
        run {
            val bb = ByteBuffer.wrap(triBytes).order(ByteOrder.LITTLE_ENDIAN)
            for (i in 0 until indexCount) indices[i] = bb.short
        }

        return MeshFace(positions = positions, normals = normals, uvs = uvs, indices = indices)
    }

    private fun readDomainVec(domain: LLSDMap?, key: String, default: FloatArray): FloatArray {
        val arr = domain?.getArray(key)?.value ?: return default
        if (arr.size < default.size) return default
        return FloatArray(default.size) { i ->
            (arr[i] as? LLSDReal)?.value?.toFloat() ?: default[i]
        }
    }

    /** Map a U16 (-32768..32767) into the [min, max] range. */
    private fun dequantU16(v: Short, min: Float, max: Float): Float {
        // SL meshes use unsigned 16-bit quantisation: 0..65535 maps to min..max.
        val u = v.toInt() and 0xFFFF
        return min + (max - min) * (u.toFloat() / 65535f)
    }

    /**
     * Legacy fallback: the old (incorrect) fixed-stride layout that this
     * codebase used to assume. Kept as a defensive fallback until we have
     * coverage data on whether any real SL asset uses it. Most likely this
     * always returns empty.
     */
    private fun parseLegacyFixedStride(data: ByteArray): List<MeshFace> {
        val out = mutableListOf<MeshFace>()
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        try {
            while (buffer.hasRemaining()) {
                val vertexCount = buffer.short.toInt() and 0xFFFF
                if (vertexCount == 0 || vertexCount > 65000) break
                val positions = FloatArray(vertexCount * 3)
                for (i in 0 until vertexCount) {
                    positions[i * 3] = buffer.short / 32767f
                    positions[i * 3 + 1] = buffer.short / 32767f
                    positions[i * 3 + 2] = buffer.short / 32767f
                }
                val normals = FloatArray(vertexCount * 3)
                for (i in 0 until vertexCount) {
                    normals[i * 3] = buffer.short / 32767f
                    normals[i * 3 + 1] = buffer.short / 32767f
                    normals[i * 3 + 2] = buffer.short / 32767f
                }
                val uvs = FloatArray(vertexCount * 2)
                for (i in 0 until vertexCount) {
                    uvs[i * 2] = buffer.short / 32767f
                    uvs[i * 2 + 1] = buffer.short / 32767f
                }
                val indexCount = buffer.short.toInt() and 0xFFFF
                if (indexCount == 0 || indexCount > 200_000) break
                val indices = ShortArray(indexCount)
                for (i in 0 until indexCount) indices[i] = buffer.short
                out.add(MeshFace(positions, normals, uvs, indices))
            }
        } catch (_: Exception) {
            // Expected for any mesh that doesn't use this layout.
        }
        return out
    }
    
    private fun parseSkinData(skinMap: LLSDMap): SkinData? {
        try {
            val jointNames = skinMap.getArray("joint_names")?.value?.mapNotNull {
                (it as? LLSDString)?.value
            } ?: return null

            val bindShapeMatrix = skinMap.getArray("bind_shape_matrix")?.value?.mapNotNull {
                (it as? LLSDReal)?.value?.toFloat()
            }?.toFloatArray() ?: FloatArray(16)

            // Inverse bind matrices arrive in two layouts depending on the
            // simulator / asset: either an array-of-arrays where each
            // inner is 16 floats (one matrix per joint), or a single flat
            // array of jointCount × 16 floats. The previous implementation
            // only handled the array-of-arrays case and silently produced
            // an empty list (= no skinning) for the flat case. Handle both.
            val ibmArrayRaw = skinMap.getArray("inverse_bind_matrix")?.value ?: emptyList()
            val inverseBindMatrices: List<FloatArray> = when {
                // Array-of-arrays: each row is its own matrix
                ibmArrayRaw.firstOrNull() is LLSDArray -> ibmArrayRaw.mapNotNull { row ->
                    (row as? LLSDArray)?.value?.mapNotNull { (it as? LLSDReal)?.value?.toFloat() }?.toFloatArray()
                }
                // Flat array: chunk into 16-float matrices, one per joint
                ibmArrayRaw.firstOrNull() is LLSDReal -> {
                    val flat = ibmArrayRaw.mapNotNull { (it as? LLSDReal)?.value?.toFloat() }
                    flat.chunked(16).filter { it.size == 16 }.map { it.toFloatArray() }
                }
                else -> emptyList()
            }

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
    
    /**
     * Called when capabilities become available.
     * Retries any mesh downloads that were queued due to missing GetMesh capability.
     */
    fun onCapabilitiesReady() {
        val meshCap = capabilityManager.getCapability(CapabilityManager.CAP_GET_MESH2)
            ?: capabilityManager.getCapability(CapabilityManager.CAP_GET_MESH)
        if (meshCap != null) {
            retryCapabilityPendingMeshes()
        }
    }

    private fun retryCapabilityPendingMeshes() {
        val pendingCount = capabilityPendingMeshes.size
        if (pendingCount == 0) return

        Log.i(TAG, "Retrying $pendingCount meshes now that GetMesh capability is available")
        val retryList = mutableListOf<PendingMeshRequest>()
        while (true) {
            val req = capabilityPendingMeshes.poll() ?: break
            retryList.add(req)
        }

        retryList.forEach { req ->
            scope.launch {
                downloadAndParseMesh(req.meshId, req.lod)
            }
        }
    }

    private fun ensureMeshCapabilityRetryStarted() {
        if (capabilityRetryJob?.isActive == true) return
        capabilityRetryJob = scope.launch {
            var attempts = 0
            while (isActive && capabilityPendingMeshes.isNotEmpty() && attempts < 30) {
                attempts++
                delay(5_000L)
                val meshCap = capabilityManager.getCapability(CapabilityManager.CAP_GET_MESH2)
                    ?: capabilityManager.getCapability(CapabilityManager.CAP_GET_MESH)
                if (meshCap != null) {
                    Log.i(TAG, "GetMesh capability now available, retrying queued meshes")
                    retryCapabilityPendingMeshes()
                    break
                }
            }
        }
    }

    fun shutdown() {
        capabilityRetryJob?.cancel()
        scope.cancel()
        pendingMeshes.clear()
    }
    
    // ==================== DIAGNOSTIC METHODS ====================
    
    // Tracking variables for diagnostics (volatile for thread safety)
    private val downloadCount = java.util.concurrent.atomic.AtomicInteger(0)
    private val downloadFailCount = java.util.concurrent.atomic.AtomicInteger(0)
    private val parseFailCount = java.util.concurrent.atomic.AtomicInteger(0)
    private val downloadedBytes = java.util.concurrent.atomic.AtomicLong(0)
    @Volatile private var lastError: String? = null
    @Volatile private var lastErrorTime: Long = 0
    
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
}

enum class MeshLOD {
    HIGHEST, HIGH, MEDIUM, LOW
}

data class MeshData(
    val meshId: UUID,
    val faces: List<MeshFace>,
    val skinData: SkinData? = null
) {
    val hasSkinData: Boolean get() = skinData != null
    val skinJointNames: List<String> get() = skinData?.jointNames ?: emptyList()
}

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
