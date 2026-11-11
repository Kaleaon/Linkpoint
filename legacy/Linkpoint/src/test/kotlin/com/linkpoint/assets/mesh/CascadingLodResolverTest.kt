package com.linkpoint.assets.mesh

import com.linkpoint.assets.MeshLOD
import com.linkpoint.protocol.llsd.LLSDInteger
import com.linkpoint.protocol.llsd.LLSDMap
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CascadingLodResolverTest {

    private lateinit var resolver: CascadingLodResolver

    @Before
    fun setUp() {
        resolver = CascadingLodResolver()
    }

    @Test
    fun testResolveExactLodMatch() {
        val headerMap = LLSDMap().apply {
            this["high_lod"] = createLodEntry(0, 100)
            this["medium_lod"] = createLodEntry(100, 50)
        }

        val result = resolver.resolveLodMap(headerMap, MeshLOD.HIGHEST)
        assertNotNull(result)
        assertEquals("high_lod", result!!.lodKey)
        assertEquals(MeshLOD.HIGHEST, result.resolvedLod)
        assertEquals(0, result.cascadeDepth)

        val telemetry = resolver.getTelemetry()
        assertEquals(1L, telemetry.totalResolutions)
        assertEquals(0L, telemetry.fallbackCount)
        assertEquals(0, telemetry.maxCascadeDepth)
        assertEquals(0.0, telemetry.averageCascadeDepth, 0.001)
    }

    @Test
    fun testResolveMissingHighestFallsBackToHigh() {
        val headerMap = LLSDMap().apply {
            this["medium_lod"] = createLodEntry(100, 50) // HIGH
            this["low_lod"] = createLodEntry(150, 25)    // MEDIUM
        }

        val result = resolver.resolveLodMap(headerMap, MeshLOD.HIGHEST)
        assertNotNull(result)
        assertEquals("medium_lod", result!!.lodKey)
        assertEquals(MeshLOD.HIGH, result.resolvedLod)
        assertEquals(1, result.cascadeDepth)

        val telemetry = resolver.getTelemetry()
        assertEquals(1L, telemetry.totalResolutions)
        assertEquals(1L, telemetry.fallbackCount)
        assertEquals(1, telemetry.maxCascadeDepth)
        assertEquals(1.0, telemetry.averageCascadeDepth, 0.001)
    }

    @Test
    fun testResolveMissingHighestAndHighFallsBackToMedium() {
        val headerMap = LLSDMap().apply {
            this["low_lod"] = createLodEntry(150, 25) // MEDIUM
        }

        val result = resolver.resolveLodMap(headerMap, MeshLOD.HIGHEST)
        assertNotNull(result)
        assertEquals("low_lod", result!!.lodKey)
        assertEquals(MeshLOD.MEDIUM, result.resolvedLod)
        assertEquals(2, result.cascadeDepth)
    }

    @Test
    fun testResolveMissingLowFallsBackToMedium() {
        val headerMap = LLSDMap().apply {
            this["low_lod"] = createLodEntry(150, 25) // MEDIUM
        }

        val result = resolver.resolveLodMap(headerMap, MeshLOD.LOW)
        assertNotNull(result)
        assertEquals("low_lod", result!!.lodKey)
        assertEquals(MeshLOD.MEDIUM, result.resolvedLod)
        assertEquals(1, result.cascadeDepth)
    }

    @Test
    fun testResolveMissingLowestLodCascadesInPriorityOrder() {
        // Search order for MeshLOD.LOW: lowest_lod -> low_lod -> medium_lod -> high_lod
        val headerMap = LLSDMap().apply {
            this["medium_lod"] = createLodEntry(200, 30) // HIGH
            this["high_lod"] = createLodEntry(0, 100)    // HIGHEST
        }

        val result = resolver.resolveLodMap(headerMap, MeshLOD.LOW)
        assertNotNull(result)
        assertEquals("medium_lod", result!!.lodKey)
        assertEquals(MeshLOD.HIGH, result.resolvedLod)
        assertEquals(2, result.cascadeDepth)
    }

    @Test
    fun testResolveMissingLowLodCascadesInPriorityOrder() {
        // Search order for MeshLOD.MEDIUM: low_lod -> medium_lod -> lowest_lod -> high_lod
        val headerMap = LLSDMap().apply {
            this["lowest_lod"] = createLodEntry(300, 10) // LOW
            this["high_lod"] = createLodEntry(0, 100)    // HIGHEST
        }

        val result = resolver.resolveLodMap(headerMap, MeshLOD.MEDIUM)
        assertNotNull(result)
        assertEquals("lowest_lod", result!!.lodKey)
        assertEquals(MeshLOD.LOW, result.resolvedLod)
        assertEquals(2, result.cascadeDepth)
    }

    @Test
    fun testSkipsInvalidEntryWithZeroSizeOrNegativeOffset() {
        val headerMap = LLSDMap().apply {
            this["high_lod"] = createLodEntry(0, 0) // Invalid size 0
            this["medium_lod"] = createLodEntry(-5, 100) // Invalid offset -5
            this["low_lod"] = createLodEntry(10, 50) // Valid
        }

        val result = resolver.resolveLodMap(headerMap, MeshLOD.HIGHEST)
        assertNotNull(result)
        assertEquals("low_lod", result!!.lodKey)
        assertEquals(MeshLOD.MEDIUM, result.resolvedLod)
        assertEquals(2, result.cascadeDepth)
    }

    @Test
    fun testResolveNoValidEntriesReturnsNull() {
        val headerMap = LLSDMap().apply {
            this["high_lod"] = createLodEntry(0, 0)
        }

        val result = resolver.resolveLodMap(headerMap, MeshLOD.HIGHEST)
        assertNull(result)
    }

    private fun createLodEntry(offset: Int, size: Int): LLSDMap {
        return LLSDMap().apply {
            this["offset"] = LLSDInteger(offset)
            this["size"] = LLSDInteger(size)
        }
    }
}
