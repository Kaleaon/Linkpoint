package com.linkpoint.assets.mesh

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.assets.AssetCache
import com.linkpoint.assets.MeshLOD
import com.linkpoint.assets.MeshManager
import com.linkpoint.protocol.capabilities.CapabilityManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.zip.Deflater

@RunWith(AndroidJUnit4::class)
class MeshManagerPipelineIntegrationTest {

    private lateinit var mockContext: Context
    private lateinit var mockCache: AssetCache
    private lateinit var mockCapManager: CapabilityManager

    private lateinit var headerDecoder: MeshHeaderDecoder
    private lateinit var decompressor: SafeMeshDecompressor
    private lateinit var lodResolver: CascadingLodResolver
    private lateinit var meshManager: MeshManager

    @Before
    fun setUp() {
        mockContext = mock()
        mockCache = mock()
        mockCapManager = mock()

        headerDecoder = MeshHeaderDecoder()
        decompressor = SafeMeshDecompressor(maxQuotaBytes = SafeMeshDecompressor.LOW_MEMORY_QUOTA_BYTES)
        lodResolver = CascadingLodResolver()

        meshManager = MeshManager(
            context = mockContext,
            cache = mockCache,
            capabilityManager = mockCapManager,
            headerDecoder = headerDecoder,
            decompressor = decompressor,
            lodResolver = lodResolver
        )
    }

    @Test
    fun testPipelineFullAssetParseAndDiagnostics() {
        val meshId = UUID.randomUUID()
        val lodPayload = createLodSubmeshPayload()
        val compressedLod = compress(lodPayload)

        val headerMapBytes = buildBinaryLlsdMap(
            "high_lod" to mapOf("offset" to 0, "size" to compressedLod.size)
        )

        val fullAsset = headerMapBytes + compressedLod

        val cachedHeader = meshManager.getCachedHeader(meshId)
        assertNull(cachedHeader)

        val diagnosticsBefore = meshManager.getDiagnostics()
        assertNotNull(diagnosticsBefore.decompressorTelemetry)
        assertNotNull(diagnosticsBefore.lodResolverTelemetry)
        assertEquals(0L, diagnosticsBefore.decompressorTelemetry!!.decompressionCount)

        // Parse via pipeline components
        val method = MeshManager::class.java.getDeclaredMethod(
            "parseMesh",
            UUID::class.java,
            ByteArray::class.java,
            MeshLOD::class.java
        ).apply { isAccessible = true }

        val result = method.invoke(meshManager, meshId, fullAsset, MeshLOD.HIGHEST)
        assertNotNull(result)

        val diagnosticsAfter = meshManager.getDiagnostics()
        assertEquals(1L, diagnosticsAfter.decompressorTelemetry!!.decompressionCount)
        assertEquals(1L, diagnosticsAfter.lodResolverTelemetry!!.totalResolutions)
        assertEquals(0L, diagnosticsAfter.headerParseFailures)
        assertEquals(32 * 1024 * 1024L, decompressor.maxQuotaBytes)
    }

    @Test
    fun testPipelineLodFallbackCascade() {
        val meshId = UUID.randomUUID()
        val lodPayload = createLodSubmeshPayload()
        val compressedLod = compress(lodPayload)

        // Header has only "low_lod" (MEDIUM tier)
        val headerMapBytes = buildBinaryLlsdMap(
            "low_lod" to mapOf("offset" to 0, "size" to compressedLod.size)
        )

        val fullAsset = headerMapBytes + compressedLod

        val method = MeshManager::class.java.getDeclaredMethod(
            "parseMesh",
            UUID::class.java,
            ByteArray::class.java,
            MeshLOD::class.java
        ).apply { isAccessible = true }

        // Request HIGHEST -> falls back 2 steps to "low_lod" (MEDIUM)
        val result = method.invoke(meshManager, meshId, fullAsset, MeshLOD.HIGHEST)
        assertNotNull(result)

        val diagnostics = meshManager.getDiagnostics()
        val lodTelemetry = diagnostics.lodResolverTelemetry!!
        assertEquals(1L, lodTelemetry.totalResolutions)
        assertEquals(1L, lodTelemetry.fallbackCount)
        assertEquals(2, lodTelemetry.maxCascadeDepth)
    }

    private fun createLodSubmeshPayload(): ByteArray {
        // LLSD Array containing 1 submesh with empty Position and TriangleList
        val bos = ByteArrayOutputStream()
        bos.write('['.code)
        bos.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(1).array())
        bos.write('{'.code)
        bos.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(2).array())

        // "Position" binary empty
        writeBinaryKey(bos, "Position", byteArrayOf())
        // "TriangleList" binary empty
        writeBinaryKey(bos, "TriangleList", byteArrayOf())

        bos.write('}'.code)
        bos.write(']'.code)
        return bos.toByteArray()
    }

    private fun writeBinaryKey(bos: ByteArrayOutputStream, key: String, data: ByteArray) {
        bos.write('k'.code)
        val kb = key.toByteArray(Charsets.UTF_8)
        bos.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(kb.size).array())
        bos.write(kb)
        bos.write('b'.code)
        bos.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(data.size).array())
        bos.write(data)
    }

    private fun buildBinaryLlsdMap(vararg entries: Pair<String, Any>): ByteArray {
        val bos = ByteArrayOutputStream()
        bos.write('{'.code)
        bos.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(entries.size).array())
        for ((key, value) in entries) {
            bos.write('k'.code)
            val kb = key.toByteArray(Charsets.UTF_8)
            bos.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(kb.size).array())
            bos.write(kb)
            writeLlsdValue(bos, value)
        }
        bos.write('}'.code)
        return bos.toByteArray()
    }

    private fun writeLlsdValue(bos: ByteArrayOutputStream, value: Any) {
        when (value) {
            is Int -> {
                bos.write('i'.code)
                bos.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(value).array())
            }
            is Map<*, *> -> {
                bos.write('{'.code)
                val mapEntries = value.entries.map { (k, v) -> k.toString() to v!! }
                bos.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(mapEntries.size).array())
                for ((k, v) in mapEntries) {
                    bos.write('k'.code)
                    val kb = k.toByteArray(Charsets.UTF_8)
                    bos.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(kb.size).array())
                    bos.write(kb)
                    writeLlsdValue(bos, v)
                }
                bos.write('}'.code)
            }
        }
    }

    private fun compress(data: ByteArray): ByteArray {
        val deflater = Deflater()
        deflater.setInput(data)
        deflater.finish()
        val bos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        while (!deflater.finished()) {
            val count = deflater.deflate(buffer)
            bos.write(buffer, 0, count)
        }
        deflater.end()
        return bos.toByteArray()
    }
}
