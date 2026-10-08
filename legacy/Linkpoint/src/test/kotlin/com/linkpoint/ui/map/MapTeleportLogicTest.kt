package com.linkpoint.ui.map

import com.linkpoint.inventory.Landmark
import com.linkpoint.protocol.types.LLVector3
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID
import kotlin.math.sqrt

class MapTeleportLogicTest {

    @Test
    fun testGetLandmarkGridPositionWithRegionHandle() {
        val regionX = 1000L
        val regionY = 2000L
        val regionHandle = ((regionX * 256L) shl 32) or (regionY * 256L)
        val landmark = Landmark(
            itemId = UUID.randomUUID(),
            assetId = UUID.randomUUID(),
            name = "Test Landmark",
            description = "A test location",
            regionName = "TestRegion",
            regionHandle = regionHandle,
            position = LLVector3(128f, 64f, 25f)
        )

        val (gridX, gridY) = getLandmarkGridPosition(landmark, emptyList())
        assertEquals(1000f + 128f / 256f, gridX, 0.001f)
        assertEquals(2000f + 64f / 256f, gridY, 0.001f)
    }

    @Test
    fun testGetLandmarkGridPositionFallbackToRegionNameMatch() {
        val landmark = Landmark(
            itemId = UUID.randomUUID(),
            assetId = UUID.randomUUID(),
            name = "Test Landmark",
            description = "A test location",
            regionName = "Aura",
            regionHandle = 0L,
            position = LLVector3(200f, 100f, 30f)
        )
        val regions = listOf(
            MapRegion(name = "Aura", x = 1050, y = 2050, access = RegionAccess.MODERATE)
        )

        val (gridX, gridY) = getLandmarkGridPosition(landmark, regions)
        assertEquals(1050f + 200f / 256f, gridX, 0.001f)
        assertEquals(2050f + 100f / 256f, gridY, 0.001f)
    }

    @Test
    fun testDistanceCalculationSameRegion() {
        val avatarRegX = 1000
        val avatarRegY = 1000
        val avatarLocalX = 128f
        val avatarLocalY = 128f

        val targetRegX = 1000
        val targetRegY = 1000
        val targetLocalX = 228f
        val targetLocalY = 128f

        val avatarGlobalX = avatarRegX * 256f + avatarLocalX
        val avatarGlobalY = avatarRegY * 256f + avatarLocalY
        val targetGlobalX = targetRegX * 256f + targetLocalX
        val targetGlobalY = targetRegY * 256f + targetLocalY

        val dx = targetGlobalX - avatarGlobalX
        val dy = targetGlobalY - avatarGlobalY
        val distanceMeters = sqrt(dx * dx + dy * dy)

        assertEquals(100f, distanceMeters, 0.01f)
    }

    @Test
    fun testDistanceCalculationCrossRegion() {
        val avatarRegX = 1000
        val avatarRegY = 1000
        val avatarLocalX = 128f
        val avatarLocalY = 128f

        // Target is 2 regions east and 0 regions north
        val targetRegX = 1002
        val targetRegY = 1000
        val targetLocalX = 128f
        val targetLocalY = 128f

        val avatarGlobalX = avatarRegX * 256f + avatarLocalX
        val avatarGlobalY = avatarRegY * 256f + avatarLocalY
        val targetGlobalX = targetRegX * 256f + targetLocalX
        val targetGlobalY = targetRegY * 256f + targetLocalY

        val dx = targetGlobalX - avatarGlobalX
        val dy = targetGlobalY - avatarGlobalY
        val distanceMeters = sqrt(dx * dx + dy * dy)

        assertEquals(512f, distanceMeters, 0.01f)
    }

    @Test
    fun testSubRegionLocalCoordinatesFromTap() {
        val tapGridX = 1000.75f // 1000 region X + 0.75 local offset (192 meters)
        val tapGridY = 2000.25f // 2000 region Y + 0.25 local offset (64 meters)

        val regX = kotlin.math.floor(tapGridX).toInt()
        val regY = kotlin.math.floor(tapGridY).toInt()
        val locX = ((tapGridX - regX) * 256f).coerceIn(0f, 256f)
        val locY = ((tapGridY - regY) * 256f).coerceIn(0f, 256f)

        assertEquals(1000, regX)
        assertEquals(2000, regY)
        assertEquals(192f, locX, 0.01f)
        assertEquals(64f, locY, 0.01f)
    }
}
