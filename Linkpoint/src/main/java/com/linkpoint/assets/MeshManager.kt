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

    /**
     * Per-mesh header cache. Headers are 1-10KB, addressed by all
     * subsequent range requests for that mesh, so caching them in
     * memory turns a "fetch the whole 1MB asset to get a 5KB
     * lowest_lod" into a "5KB header (cached after first request) +
     * 5KB LOD slice = 10KB" round-trip. Capped at [MAX_HEADER_CACHE]
     * with naive first-key eviction so memory stays bounded across
     * long sessions touching many meshes.
     */
    private data class ParsedHeader(val map: LLSDMap, val headerEnd: Int)
    private val headerCache = ConcurrentHashMap<UUID, ParsedHeader>()
    private val MAX_HEADER_CACHE = 500

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

        // Range-fetch path: header (cached after first hit) + LOD slice +
        // optional skin slice. For the common case of a low-priority
        // mesh ("lowest_lod" of a complex avatar mesh) this turns a
        // multi-MB full-asset fetch into ~10 KB of bytes on the wire.
        // Falls through to the legacy full-asset download below if any
        // step in the ranged path returns null (server doesn't honour
        // Range, transient failure, etc.).
        val rangedResult = tryRangedFetch(meshId, lod, url)
        if (rangedResult != null) return rangedResult

        // Legacy full-asset fallback: same Cronet-then-OkHttp pattern as
        // TextureManager. Used when the ranged path can't be served by
        // the CDN (rare; SL CDN normally honours bytes= ranges).
        Log.d(TAG, "Falling back to full-asset fetch for $meshId")
        return downloadFullAsset(meshId, lod, url)
    }

    /**
     * Attempts the ranged fetch path. Returns null on any failure (caller
     * falls back to full-asset download). On success the LOD slice is
     * decompressed, parsed, and (if the asset has skinning) the skin
     * blob is fetched and merged into the result.
     */
    private suspend fun tryRangedFetch(meshId: UUID, lod: MeshLOD, url: String): MeshData? {
        val header = ensureHeader(meshId, url) ?: return null
        val lodMap = header.map.getMap(lodKeyFor(lod)) ?: header.map.getMap("high_lod") ?: return null
        val lodOffset = lodMap.getInt("offset") ?: return null
        val lodSize = lodMap.getInt("size") ?: return null
        if (lodSize <= 0) return null

        val lodStart = (header.headerEnd + lodOffset).toLong()
        val lodEnd = lodStart + lodSize - 1
        val lodCompressed = fetchRange(url, lodStart..lodEnd, 60_000L) ?: return null

        // Skin is itself a ranged blob in the modern format. Old assets
        // sometimes embed the skin map directly in the header; we accept
        // both — see parseSkinDataFromHeaderOrBlob below.
        val skinMap = parseSkinDataFromHeaderOrBlob(url, header) // may be null

        return try {
            val decompressed = decompress(lodCompressed)
            downloadCount.incrementAndGet()
            downloadedBytes.addAndGet(lodCompressed.size.toLong())
            val faces = parseGeometryFromLodBytes(meshId, decompressed)
            val skinData = skinMap?.let { parseSkinData(it) }
            MeshData(meshId = meshId, faces = faces, skinData = skinData)
        } catch (e: Exception) {
            Log.w(TAG, "Ranged-fetch parse failed for $meshId, will retry full asset: ${e.message}")
            null
        }
    }

    /** Map [MeshLOD] to its header key. Centralised so the ranged path and the legacy parseMesh agree. */
    private fun lodKeyFor(lod: MeshLOD): String = when (lod) {
        MeshLOD.HIGHEST -> "high_lod"
        MeshLOD.HIGH -> "medium_lod"
        MeshLOD.MEDIUM -> "low_lod"
        MeshLOD.LOW -> "lowest_lod"
    }

    /**
     * Get the parsed header for [meshId], fetching if not cached. Tries
     * progressively larger byte ranges (8 KB → 32 KB → 128 KB) since we
     * don't know the header size up-front. Most SL mesh headers fit
     * comfortably in 8 KB.
     */
    private suspend fun ensureHeader(meshId: UUID, url: String): ParsedHeader? {
        headerCache[meshId]?.let { return it }
        for (probeSize in longArrayOf(8 * 1024L, 32 * 1024L, 128 * 1024L)) {
            val data = fetchRange(url, 0L until probeSize, 30_000L) ?: continue
            val (value, end) = LLSDParser.parseBinaryAndConsumed(data)
            if (end > 0 && value is LLSDMap) {
                val parsed = ParsedHeader(value, end)
                if (headerCache.size >= MAX_HEADER_CACHE) {
                    headerCache.keys.firstOrNull()?.let { headerCache.remove(it) }
                }
                headerCache[meshId] = parsed
                return parsed
            }
            // Header didn't fit in the probe — try a bigger one.
        }
        Log.w(TAG, "Mesh header didn't parse within 128 KB probe for $meshId")
        return null
    }

    /**
     * Resolve the skin LLSDMap for a mesh, accepting either the modern
     * layout (`skin: {offset, size}` pointing at a separate compressed
     * blob in the asset body) or the legacy/inline layout (`skin: {...}`
     * with joint_names embedded directly in the header). Returns null
     * if the asset has no skin data at all.
     */
    private suspend fun parseSkinDataFromHeaderOrBlob(url: String, header: ParsedHeader): LLSDMap? {
        val skinEntry = header.map.getMap("skin") ?: return null
        val offset = skinEntry.getInt("offset")
        val size = skinEntry.getInt("size")
        // Modern: separate blob. Range-fetch + decompress + parse.
        if (offset != null && size != null && size > 0) {
            val absStart = (header.headerEnd + offset).toLong()
            val absEnd = absStart + size - 1
            val skinCompressed = fetchRange(url, absStart..absEnd, 30_000L) ?: return null
            return try {
                val decoded = decompress(skinCompressed)
                LLSDParser.parseBinary(decoded) as? LLSDMap
            } catch (e: Exception) {
                Log.w(TAG, "Skin blob parse failed: ${e.message}")
                null
            }
        }
        // Legacy: skin map embedded inline in the header. Use directly
        // if it has the expected joint_names key.
        if (skinEntry.getArray("joint_names") != null) return skinEntry
        return null
    }

    /**
     * Range-aware fetch. Cronet primary, OkHttp fallback. If the server
     * returns 200 OK to a Range request (some CDNs ignore Range under
     * certain configs), we still get the full body and return it — the
     * caller's slicing math doesn't apply but the LOD path also handles
     * the full-asset case via [downloadFullAsset], so we just route the
     * caller to the fallback by returning null in that case.
     *
     * @param range null = full-body GET, non-null = HTTP byte range
     */
    private suspend fun fetchRange(url: String, range: LongRange?, timeoutMs: Long): ByteArray? {
        val rangeHeader: Map<String, String> = range?.let {
            mapOf("Range" to "bytes=${it.first}-${it.last}")
        } ?: emptyMap()
        val cronet = CronetHttpClient.getOrCreate(context)
        if (cronet.isAvailable) {
            val r = cronet.get(url, headers = rangeHeader, timeoutMs = timeoutMs)
            when (r) {
                is CronetResult.Success -> {
                    if (range == null && r.code in 200..299) return r.body
                    if (r.code == 206) return r.body
                    if (r.code == 200) {
                        // Server ignored Range — bail to the full-asset
                        // path so we don't try to slice the whole asset.
                        return null
                    }
                }
                is CronetResult.Failure -> Log.d(TAG, "Cronet ranged fetch failed for $url: ${r.message}")
                else -> {}
            }
        }
        // OkHttp fallback
        return try {
            val builder = Request.Builder().url(url)
            rangeHeader.forEach { (k, v) -> builder.header(k, v) }
            val response = httpClient.newCall(builder.build()).execute()
            try {
                if (range == null && response.isSuccessful) response.body?.bytes()
                else if (response.code == 206) response.body?.bytes()
                else null
            } finally {
                response.close()
            }
        } catch (e: Exception) {
            Log.d(TAG, "OkHttp ranged fetch failed for $url: ${e.message}")
            null
        }
    }

    /**
     * Parse just the geometry (submeshes) from an already-decompressed
     * LOD blob. Doesn't touch skin data — the caller composes that
     * separately. Mirrors the geometry-parsing portion of [parseMesh]
     * so the ranged path can reuse it without touching the legacy
     * full-asset code path.
     */
    private fun parseGeometryFromLodBytes(meshId: UUID, decompressed: ByteArray): List<MeshFace> {
        val faces = mutableListOf<MeshFace>()
        try {
            val lodValue = LLSDParser.parseBinary(decompressed)
            val submeshes = (lodValue as? LLSDArray)?.value ?: emptyList()
            for (sub in submeshes) {
                (sub as? LLSDMap)?.let { parseSubmesh(it)?.let(faces::add) }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Mesh $meshId LOD-array parse failed: ${e.message}")
        }
        return faces
    }

    /** Legacy full-asset download path. Kept as the fallback for ranged-fetch failures. */
    private suspend fun downloadFullAsset(meshId: UUID, lod: MeshLOD, url: String): MeshData? {
        val cronet = CronetHttpClient.getOrCreate(context)
        if (cronet.isAvailable) {
            val cronetResult = cronet.get(url, timeoutMs = 60_000L)
            if (cronetResult is CronetResult.Success && cronetResult.code in 200..299) {
                Log.d(TAG, "Mesh full-asset via Cronet/${cronetResult.protocol}: $meshId (${cronetResult.body.size} bytes)")
                downloadCount.incrementAndGet()
                downloadedBytes.addAndGet(cronetResult.body.size.toLong())
                cache.put(meshId, AssetType.MESH, cronetResult.body)
                return parseMesh(meshId, cronetResult.body, lod)
            }
        }
        return try {
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
                val data = response.body?.bytes() ?: run {
                    lastError = "Empty response body"
                    lastErrorTime = System.currentTimeMillis()
                    downloadFailCount.incrementAndGet()
                    return null
                }
                downloadCount.incrementAndGet()
                downloadedBytes.addAndGet(data.size.toLong())
                cache.put(meshId, AssetType.MESH, data)
                parseMesh(meshId, data, lod)
            } finally {
                response.close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Mesh download error: $meshId", e)
            lastError = "${e.javaClass.simpleName}: ${e.message}"
            lastErrorTime = System.currentTimeMillis()
            downloadFailCount.incrementAndGet()
            null
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

        // Normal: optional. Decode the asset-supplied normals if present;
        // otherwise compute smooth area-weighted vertex normals from the
        // triangle list, which gives much better lit-material shading
        // than the previous "default to +Z" approach (which made every
        // surface look like it was facing up).
        val normalBytes = (map.value["Normal"] as? LLSDBinary)?.value
        val normals = if (normalBytes != null && normalBytes.size >= vertexCount * 6) {
            val out = FloatArray(vertexCount * 3)
            val bb = ByteBuffer.wrap(normalBytes).order(ByteOrder.LITTLE_ENDIAN)
            for (i in 0 until vertexCount) {
                // Normals are quantised in [-1, 1].
                out[i * 3]     = dequantU16(bb.short, -1f, 1f)
                out[i * 3 + 1] = dequantU16(bb.short, -1f, 1f)
                out[i * 3 + 2] = dequantU16(bb.short, -1f, 1f)
            }
            out
        } else {
            computeSmoothNormals(positions, indices, vertexCount)
        }

        // Weights: optional. Per-vertex skinning influences for rigged
        // meshes. Format per LL viewer source: for each vertex, up to
        // MAX_INFLUENCES (4) (jointIndex: u8, weight: f32 LE) pairs,
        // terminated by jointIndex == 0xFF. Vertices appear in the same
        // order as Position. Weights are normalised so each vertex's
        // slots sum to 1 (silently dropped before normalisation if all
        // weights are zero, which would cause NaN). When this blob is
        // present the face is treated as rigged; the renderer can pick
        // it up via MeshFace.isRigged.
        val (jointIndices, weights) = (map.value["Weights"] as? LLSDBinary)?.value?.let {
            parseWeights(it, vertexCount)
        } ?: (null to null)

        return MeshFace(
            positions = positions,
            normals = normals,
            uvs = uvs,
            indices = indices,
            jointIndices = jointIndices,
            weights = weights
        )
    }

    /**
     * Compute area-weighted smooth vertex normals. For each triangle,
     * the un-normalised cross product of two edges has magnitude equal
     * to twice the triangle's area, so summing it into each vertex
     * implicitly weights by area. Final normalisation gives unit-length
     * vertex normals. Vertices that don't appear in any triangle (or
     * appear in degenerate triangles only) get a +Z fallback so they
     * don't shade as black.
     */
    private fun computeSmoothNormals(
        positions: FloatArray,
        indices: ShortArray,
        vertexCount: Int
    ): FloatArray {
        val normals = FloatArray(vertexCount * 3)
        val triCount = indices.size / 3
        for (t in 0 until triCount) {
            val i0 = indices[t * 3].toInt() and 0xFFFF
            val i1 = indices[t * 3 + 1].toInt() and 0xFFFF
            val i2 = indices[t * 3 + 2].toInt() and 0xFFFF
            if (i0 >= vertexCount || i1 >= vertexCount || i2 >= vertexCount) continue

            val p0x = positions[i0 * 3]; val p0y = positions[i0 * 3 + 1]; val p0z = positions[i0 * 3 + 2]
            val p1x = positions[i1 * 3]; val p1y = positions[i1 * 3 + 1]; val p1z = positions[i1 * 3 + 2]
            val p2x = positions[i2 * 3]; val p2y = positions[i2 * 3 + 1]; val p2z = positions[i2 * 3 + 2]

            // Cross((p1-p0), (p2-p0))
            val ex1 = p1x - p0x; val ey1 = p1y - p0y; val ez1 = p1z - p0z
            val ex2 = p2x - p0x; val ey2 = p2y - p0y; val ez2 = p2z - p0z
            val nx = ey1 * ez2 - ez1 * ey2
            val ny = ez1 * ex2 - ex1 * ez2
            val nz = ex1 * ey2 - ey1 * ex2

            normals[i0 * 3] += nx; normals[i0 * 3 + 1] += ny; normals[i0 * 3 + 2] += nz
            normals[i1 * 3] += nx; normals[i1 * 3 + 1] += ny; normals[i1 * 3 + 2] += nz
            normals[i2 * 3] += nx; normals[i2 * 3 + 1] += ny; normals[i2 * 3 + 2] += nz
        }
        for (i in 0 until vertexCount) {
            val nx = normals[i * 3]; val ny = normals[i * 3 + 1]; val nz = normals[i * 3 + 2]
            val len = kotlin.math.sqrt(nx * nx + ny * ny + nz * nz)
            if (len > 1e-6f) {
                normals[i * 3]     = nx / len
                normals[i * 3 + 1] = ny / len
                normals[i * 3 + 2] = nz / len
            } else {
                normals[i * 3 + 2] = 1f
            }
        }
        return normals
    }

    /**
     * Parse the Weights submesh blob. SL packs per-vertex skinning as a
     * variable-length stream:
     *
     *   for each vertex:
     *     while have-more-influences:
     *       jointIndex: U8
     *       if jointIndex == 0xFF: break  // end-of-vertex marker
     *       weight: F32 little-endian
     *
     * We collect up to [MAX_INFLUENCES_PER_VERTEX] = 4 per vertex and
     * normalise so each vertex's weights sum to 1 (anything past 4 is
     * dropped). Returns (jointIndices, weights) parallel arrays of
     * length `vertexCount * 4`. Joint index `-1` marks unused slots.
     */
    private fun parseWeights(blob: ByteArray, vertexCount: Int): Pair<IntArray, FloatArray>? {
        val maxInf = MeshFace.MAX_INFLUENCES_PER_VERTEX
        val jointIndices = IntArray(vertexCount * maxInf) { -1 }
        val weights = FloatArray(vertexCount * maxInf)
        val bb = ByteBuffer.wrap(blob).order(ByteOrder.LITTLE_ENDIAN)
        try {
            for (v in 0 until vertexCount) {
                var slotsFilled = 0
                while (bb.remaining() >= 1) {
                    val ji = bb.get().toInt() and 0xFF
                    if (ji == 0xFF) break
                    if (bb.remaining() < 4) break
                    val w = bb.float
                    if (slotsFilled < maxInf) {
                        jointIndices[v * maxInf + slotsFilled] = ji
                        weights[v * maxInf + slotsFilled] = w
                        slotsFilled++
                    }
                    // If we already have maxInf slots, keep draining until
                    // 0xFF terminator so the cursor stays aligned for the
                    // next vertex.
                }
                // Normalise this vertex's weights to sum to 1
                var sum = 0f
                for (k in 0 until maxInf) sum += weights[v * maxInf + k]
                if (sum > 1e-6f) {
                    for (k in 0 until maxInf) weights[v * maxInf + k] /= sum
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Weights blob truncated or malformed: ${e.message}")
            return null
        }
        return jointIndices to weights
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
        headerCache.clear()
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
    val indices: ShortArray,
    /**
     * Per-vertex skinning data (rigged meshes only). Both arrays are
     * length `vertexCount * MAX_INFLUENCES_PER_VERTEX` (= 4) when
     * present. Joint index `-1` means "unused slot". Weights are
     * already normalised to sum to 1 within their vertex's slots.
     * Null when the asset has no Weights blob (= static mesh).
     */
    val jointIndices: IntArray? = null,
    val weights: FloatArray? = null
) {
    val vertexCount: Int get() = positions.size / 3
    val indexCount: Int get() = indices.size
    val isRigged: Boolean get() = weights != null

    companion object {
        /** SL meshes cap per-vertex influences at 4 — matches GPU skinning conventions. */
        const val MAX_INFLUENCES_PER_VERTEX = 4
    }
}

data class SkinData(
    val jointNames: List<String>,
    val bindShapeMatrix: FloatArray,
    val inverseBindMatrices: List<FloatArray>
)
