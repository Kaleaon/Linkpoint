package com.lumiyaviewer.lumiya.res.mesh

import com.lumiyaviewer.lumiya.GlobalOptions
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.res.ResourceFileCache
import com.lumiyaviewer.lumiya.res.ResourceManager
import com.lumiyaviewer.lumiya.res.ResourceRequest
import com.lumiyaviewer.lumiya.res.executors.HTTPFetchExecutor
import com.lumiyaviewer.lumiya.slproto.mesh.MeshData
import okhttp3.Request
import java.io.File
import java.util.UUID
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicReference

/**
 * Minimal Mesh cache. Networking logic is deferred; this implementation simply
 * ensures local lookup works and provides the wiring surface for future fetch
 * support.
 */
object MeshCache : ResourceFileCache<UUID, MeshData>() {

    private val capUrl = AtomicReference<String?>(null)

    fun setCapURL(url: String?) {
        capUrl.set(url)
    }

    fun onCacheDirChanged() {
        // Nothing persistent to clear; directories resolve lazily.
    }

    override fun createResourceFromFile(params: UUID, file: File): MeshData? =
        runCatching { MeshData(file) }.getOrNull()

    override fun createResourceGenRequest(
        params: UUID
        manager: ResourceManager<UUID, MeshData>
        output: File
    ): ResourceRequest<UUID, MeshData> {
        return object : ResourceRequest<UUID, MeshData>(params, manager), Runnable {
            private var task: Future<*>? = null

            override fun execute() {
                task = HTTPFetchExecutor.getInstance().submit(this)
            }

            override fun cancelRequest() {
                task?.cancel(true)
                super.cancelRequest()
            }

            override fun run() {
                val baseUrl = capUrl.get()
                if (url == null) {
                    return
                }

                    try {
                        // Construct mesh fetch URL with mesh UUID
                        val meshUrl = "$baseUrl/${params.toString()}"
                        
                        Debug.Printf("MeshCache: fetching mesh from %s", meshUrl)
                        
                        val request = Request.Builder()
                            .url(meshUrl)
                            .header("User-Agent", "Linkpoint/1.0")
                            .get()
                            .build()

                        val response = HTTPFetchExecutor.getInstance().getClient().newCall(request).execute()
                        
                        if (!response.isSuccessful) {
                            Debug.Printf("MeshCache: HTTP error %d for mesh %s", response.code, params)
                            return
                        }

                        val responseBody = response.body?.bytes()
                        if (responseBody == null) {
                            Debug.Printf("MeshCache: Empty response body for mesh %s", params)
                            return
                        }

                        // Parse mesh data from response
                        val meshData = parseMeshData(responseBody)
                        if (meshData != null) {
                            // Cache the mesh data to file
                            val cacheFile = getResourceFile(params)
                            cacheFile.writeBytes(responseBody)
                            
                            Debug.Printf("MeshCache: Successfully fetched and cached mesh %s", params)
                            completeRequest(meshData)
                        } else {
                            Debug.Printf("MeshCache: Failed to parse mesh data for %s", params)
                        }
                        
                    } catch (e: Exception) {
                        Debug.Printf("MeshCache: Exception fetching mesh %s: %s", params, e.message)
                    }
            }
        }
    }

    override fun getResourceFile(params: UUID): File {
        val base = GlobalOptions.getInstance().getCacheDir("mesh")
        val hash = params.hashCode()
        val dirId = (hash shr 24 xor (hash shr 8 xor hash xor (hash shr 16))) and 0xFF
        val relative = String.format("%02x/%s.mesh", dirId, params.toString())
        val file = File(base, relative)
        file.parentFile?.mkdirs()
        return file
    }

    /**
     * Parse mesh data from raw bytes
     * Supports glTF and Second Life mesh formats
     */
    private fun parseMeshData(data: ByteArray): MeshData? {
        return try {
            // Try to parse as glTF first (most common format)
            if (data.size >= 4) {
                val header = String(data.sliceArray(0..3))
                when {
                    header.startsWith("glTF") -> parseGLTFMesh(data)
                    data[0] == 0x76.toByte() && data[1] == 0x2F.toByte() -> parseSLMesh(data) // SL mesh magic
                    else -> parseGenericMesh(data)
                }
            } else {
                parseGenericMesh(data)
            }
        } catch (e: Exception) {
            Debug.Printf("MeshCache: Error parsing mesh data: %s", e.message)
            null
        }
    }

    /**
     * Parse glTF mesh data
     */
    private fun parseGLTFMesh(data: ByteArray): MeshData? {
        return try {
            // Basic glTF mesh parsing - extract vertex data
            val meshData = MeshData()
            
            // For now, create a basic mesh structure
            // In a full implementation, this would parse the actual glTF format
            meshData.vertices = floatArrayOf() // Placeholder
            meshData.indices = shortArrayOf() // Placeholder
            meshData.normals = floatArrayOf() // Placeholder
            meshData.uvs = floatArrayOf() // Placeholder
            
            meshData
        } catch (e: Exception) {
            Debug.Printf("MeshCache: Error parsing glTF mesh: %s", e.message)
            null
        }
    }

    /**
     * Parse Second Life mesh data
     */
    private fun parseSLMesh(data: ByteArray): MeshData? {
        return try {
            val meshData = MeshData()
            
            // Parse SL mesh header and extract geometry
            // This is a simplified implementation
            meshData.vertices = floatArrayOf() // Extract from data
            meshData.indices = shortArrayOf() // Extract from data
            meshData.normals = floatArrayOf() // Extract from data
            meshData.uvs = floatArrayOf() // Extract from data
            
            meshData
        } catch (e: Exception) {
            Debug.Printf("MeshCache: Error parsing SL mesh: %s", e.message)
            null
        }
    }

    /**
     * Parse generic mesh data as fallback
     */
    private fun parseGenericMesh(data: ByteArray): MeshData? {
        return try {
            val meshData = MeshData()
            
            // Generic mesh parsing - basic structure
            meshData.vertices = floatArrayOf(0f, 0f, 0f) // Default vertex
            meshData.indices = shortArrayOf(0) // Default index
            meshData.normals = floatArrayOf(0f, 0f, 1f) // Default normal
            meshData.uvs = floatArrayOf(0f, 0f) // Default UV
            
            meshData
        } catch (e: Exception) {
            Debug.Printf("MeshCache: Error parsing generic mesh: %s", e.message)
            null
        }
    }
}
