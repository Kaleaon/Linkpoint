package com.linkpoint.linden.llfilesystem

import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llcommon.AssetType
import java.io.File
import java.nio.file.Path

class LLFileSystem(
    private val cacheDir: Path,
    private val maxMemoryCacheBytes: Long = 64L * 1024 * 1024
) {
    private val memCache: LinkedHashMap<String, ByteArray> = object : LinkedHashMap<String, ByteArray>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: Map.Entry<String, ByteArray>): Boolean =
            memoryUsed() > maxMemoryCacheBytes
    }

    private fun key(id: LLUUID, type: AssetType): String = "${id.uuid}_${type.value}"

    private fun diskPath(id: LLUUID, type: AssetType): File =
        cacheDir.resolve("${key(id, type)}.asset").toFile()

    private fun memoryUsed(): Long = memCache.values.sumOf { it.size.toLong() }

    fun get(id: LLUUID, type: AssetType): ByteArray? {
        val k = key(id, type)
        memCache[k]?.let { return it }
        val f = diskPath(id, type)
        if (!f.exists()) return null
        val data = f.readBytes()
        synchronized(memCache) { memCache[k] = data }
        return data
    }

    fun put(id: LLUUID, type: AssetType, data: ByteArray): Boolean {
        val k = key(id, type)
        synchronized(memCache) { memCache[k] = data }
        return runCatching {
            val f = diskPath(id, type)
            f.parentFile?.mkdirs()
            f.writeBytes(data)
        }.isSuccess
    }

    fun remove(id: LLUUID, type: AssetType): Boolean {
        val k = key(id, type)
        synchronized(memCache) { memCache.remove(k) }
        val f = diskPath(id, type)
        return if (f.exists()) f.delete() else true
    }

    fun exists(id: LLUUID, type: AssetType): Boolean =
        key(id, type) in memCache || diskPath(id, type).exists()

    fun clearMemoryCache() = synchronized(memCache) { memCache.clear() }
    fun cacheDir(): Path = cacheDir
}
