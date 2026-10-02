package com.linkpoint.assets

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDReal
import com.linkpoint.protocol.types.LLVector3
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class LLMeshFetcherTest {

    private lateinit var fetcher: LLMeshFetcher
    private lateinit var camera: LLMeshFetcher.CameraParams

    @Before
    fun setUp() {
        fetcher = LLMeshFetcher()
        camera = LLMeshFetcher.CameraParams(
            position = LLVector3(0f, 0f, 0f),
            fovRad = Math.toRadians(60.0).toFloat(), // 60 degrees FOV
            screenHeightPx = 1080
        )
    }

    @Test
    fun testCalculateProjectedPixelCoverage() {
        // Distance = 10m, Radius = 1.0m, FOV = 60 deg (tan(30 deg) ≈ 0.57735)
        // Coverage = (1.0 * 1080) / (10.0 * 0.57735) ≈ 187.05 px
        val px = fetcher.calculateProjectedPixelCoverage(
            boundingRadius = 1.0f,
            distanceMeters = 10.0f,
            fovRad = camera.fovRad,
            screenHeightPx = camera.screenHeightPx
        )
        assertEquals(187.05f, px, 1.0f)
    }

    @Test
    fun testCalculateProjectedPixelCoverageEdgeCases() {
        // Zero or negative distance should be clamped safely to avoid division by zero
        val pxZeroDist = fetcher.calculateProjectedPixelCoverage(
            boundingRadius = 1.0f,
            distanceMeters = 0.0f,
            fovRad = camera.fovRad,
            screenHeightPx = camera.screenHeightPx
        )
        assertTrue("Coverage should be positive for zero distance", pxZeroDist > 0f)

        // Zero or negative radius should be clamped to positive default
        val pxZeroRadius = fetcher.calculateProjectedPixelCoverage(
            boundingRadius = 0.0f,
            distanceMeters = 10.0f,
            fovRad = camera.fovRad,
            screenHeightPx = camera.screenHeightPx
        )
        assertTrue("Coverage should be positive for zero radius", pxZeroRadius > 0f)
    }

    @Test
    fun testSelectLodByDistanceAndCoverage() {
        val meshId = UUID.randomUUID()

        // Close object (2m) -> projected > 200px -> HIGHEST (LOD0)
        val closeSelection = fetcher.selectLod(
            meshId = meshId,
            objectPos = LLVector3(2f, 0f, 0f),
            boundingRadius = 1.0f,
            camera = camera
        )
        assertEquals(MeshLOD.HIGHEST, closeSelection.targetLod)
        assertFalse(closeSelection.isDeferred)

        // Medium distance (10m) -> projected ~187px -> HIGH (LOD1)
        val medSelection = fetcher.selectLod(
            meshId = meshId,
            objectPos = LLVector3(10f, 0f, 0f),
            boundingRadius = 1.0f,
            camera = camera
        )
        assertEquals(MeshLOD.HIGH, medSelection.targetLod)
        assertFalse(medSelection.isDeferred)

        // Far distance (25m) -> projected ~74.8px -> MEDIUM (LOD2)
        val farSelection = fetcher.selectLod(
            meshId = meshId,
            objectPos = LLVector3(25f, 0f, 0f),
            boundingRadius = 1.0f,
            camera = camera
        )
        assertEquals(MeshLOD.MEDIUM, farSelection.targetLod)
        assertFalse(farSelection.isDeferred)

        // Very far distance (100m) -> projected ~18.7px -> LOW (LOD3)
        val veryFarSelection = fetcher.selectLod(
            meshId = meshId,
            objectPos = LLVector3(100f, 0f, 0f),
            boundingRadius = 1.0f,
            camera = camera
        )
        assertEquals(MeshLOD.LOW, veryFarSelection.targetLod)
        assertFalse(veryFarSelection.isDeferred)
    }

    @Test
    fun testDistantAvatarThrottling() {
        val avatarMeshId = UUID.randomUUID()

        // Avatar within 50m (e.g. 20m) with large bounding radius -> HIGHEST (LOD0) allowed
        val nearAvatar = fetcher.selectLod(
            meshId = avatarMeshId,
            objectPos = LLVector3(20f, 0f, 0f),
            boundingRadius = 3.0f, // projected ~561px
            camera = camera,
            isAvatar = true
        )
        assertEquals(MeshLOD.HIGHEST, nearAvatar.targetLod)
        assertFalse(nearAvatar.isDistantAvatarGated)

        // Avatar beyond 50m (e.g. 60m) -> gated to MEDIUM (LOD2)
        val distantAvatar = fetcher.selectLod(
            meshId = avatarMeshId,
            objectPos = LLVector3(60f, 0f, 0f),
            boundingRadius = 3.0f, // projected ~187px, which normally would be HIGH (LOD1)
            camera = camera,
            isAvatar = true
        )
        assertEquals(MeshLOD.MEDIUM, distantAvatar.targetLod)
        assertTrue(distantAvatar.isDistantAvatarGated)
    }

    @Test
    fun testSubpixelAttachmentDeferral() {
        val attachmentMeshId = UUID.randomUUID()

        // Small attachment far away (50m, radius 0.1m) -> projected ~1.87px < 8.0px -> deferred
        val farAttachment = fetcher.selectLod(
            meshId = attachmentMeshId,
            objectPos = LLVector3(50f, 0f, 0f),
            boundingRadius = 0.1f,
            camera = camera,
            isAttachment = true
        )
        assertTrue(farAttachment.isDeferred)

        // Small attachment close (5m, radius 0.1m) -> projected ~18.7px >= 8.0px -> not deferred
        val nearAttachment = fetcher.selectLod(
            meshId = attachmentMeshId,
            objectPos = LLVector3(5f, 0f, 0f),
            boundingRadius = 0.1f,
            camera = camera,
            isAttachment = true
        )
        assertFalse(nearAttachment.isDeferred)
    }

    @Test
    fun testSubpixelObjectDeferral() {
        val objectMeshId = UUID.randomUUID()

        // Small scene object far away (150m, radius 0.1m) -> projected ~1.25px < 2.0px -> deferred
        val farObject = fetcher.selectLod(
            meshId = objectMeshId,
            objectPos = LLVector3(150f, 0f, 0f),
            boundingRadius = 0.1f,
            camera = camera,
            isAvatar = false,
            isAttachment = false
        )
        assertTrue(farObject.isDeferred)
    }

    @Test
    fun testCustomMeshHeaderThresholds() {
        val meshId = UUID.randomUUID()

        // Create LLSD header map with custom lod_pixel_angles
        val anglesMap = LLSDMap(mutableMapOf(
            "high_lod" to LLSDReal(500.0),
            "medium_lod" to LLSDReal(200.0),
            "low_lod" to LLSDReal(50.0),
            "lowest_lod" to LLSDReal(10.0)
        ))
        val headerMap = LLSDMap(mutableMapOf("lod_pixel_angles" to anglesMap))

        // Distance 10m -> projected ~187px
        // With custom thresholds (high requires 500, med requires 200), 187px falls into MEDIUM (requires 50)
        val customSelection = fetcher.selectLod(
            meshId = meshId,
            objectPos = LLVector3(10f, 0f, 0f),
            boundingRadius = 1.0f,
            camera = camera,
            header = headerMap
        )
        assertEquals(MeshLOD.MEDIUM, customSelection.targetLod)
    }

    @Test
    fun testCorruptedHeaderFallback() {
        val meshId = UUID.randomUUID()

        // Corrupted header map (invalid negative threshold order)
        val badAnglesMap = LLSDMap(mutableMapOf(
            "high_lod" to LLSDReal(10.0),
            "medium_lod" to LLSDReal(500.0) // wrong order
        ))
        val corruptedHeader = LLSDMap(mutableMapOf("lod_pixel_angles" to badAnglesMap))

        // Should handle gracefully and fall back to default thresholds
        val fallbackSelection = fetcher.selectLod(
            meshId = meshId,
            objectPos = LLVector3(10f, 0f, 0f),
            boundingRadius = 1.0f,
            camera = camera,
            header = corruptedHeader
        )
        assertNotNull(fallbackSelection)
        assertEquals(MeshLOD.HIGH, fallbackSelection.targetLod)
    }

    @Test
    fun testShouldReevaluate() {
        assertTrue(fetcher.shouldReevaluate(10f, 12f, 0.15f)) // 20% delta -> true
        assertFalse(fetcher.shouldReevaluate(10f, 10.5f, 0.15f)) // 5% delta -> false
    }
}
