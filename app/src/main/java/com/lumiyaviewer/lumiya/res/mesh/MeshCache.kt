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
        params: UUID,
        manager: ResourceManager<UUID, MeshData>,
        output: File,
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
                val url = capUrl.get()
                if (url == null) {
                    completeRequest(null)
                    return
                }

                // TODO: implement mesh fetch; currently just log and signal failure
                Debug.Printf("MeshCache: fetch not implemented for %s", params)
                completeRequest(null)
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
}
