package com.linkpoint.render.driver

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure-data tests for [GraphicsDriverProbe.DriverProfile]. The
 * production probe path needs an Android [Context] which isn't
 * available under the JVM unit test runner, but the derived
 * properties (vendor inference, GLES floor, summary) are pure
 * functions on the data class and easy to exercise.
 */
class DriverProfileTest {

    @Test
    fun `hasVulkan reflects level not version`() {
        val noVk = GraphicsDriverProbe.DriverProfile.from(glesMajor = 3, glesMinor = 1)
        assertFalse(noVk.hasVulkan)

        val level0 = GraphicsDriverProbe.DriverProfile.from(3, 1, vulkanLevel = 0)
        assertTrue("Vulkan level 0 still counts as having a driver", level0.hasVulkan)

        val level1 = GraphicsDriverProbe.DriverProfile.from(3, 1, vulkanLevel = 1)
        assertTrue(level1.hasVulkan)
    }

    @Test
    fun `meetsMinimumGles requires GLES 3_0`() {
        assertFalse(GraphicsDriverProbe.DriverProfile.from(2, 0).meetsMinimumGles)
        assertTrue(GraphicsDriverProbe.DriverProfile.from(3, 0).meetsMinimumGles)
        assertTrue(GraphicsDriverProbe.DriverProfile.from(3, 2).meetsMinimumGles)
        assertTrue(GraphicsDriverProbe.DriverProfile.from(4, 0).meetsMinimumGles)
    }

    @Test
    fun `vendor family inference - Adreno`() {
        val p = GraphicsDriverProbe.DriverProfile.from(
            glesMajor = 3, glesMinor = 2,
            hardware = "qcom", manufacturer = "Qualcomm"
        )
        assertEquals(GraphicsDriverProbe.VendorFamily.ADRENO, p.vendorFamily)
    }

    @Test
    fun `vendor family inference - Mali`() {
        val p = GraphicsDriverProbe.DriverProfile.from(
            glesMajor = 3, glesMinor = 2,
            hardware = "exynos", manufacturer = "ARM (Mali)"
        )
        assertEquals(GraphicsDriverProbe.VendorFamily.MALI, p.vendorFamily)
    }

    @Test
    fun `vendor family inference - PowerVR`() {
        val p = GraphicsDriverProbe.DriverProfile.from(
            glesMajor = 3, glesMinor = 1,
            hardware = "mediatek", manufacturer = "Imagination Technologies"
        )
        assertEquals(GraphicsDriverProbe.VendorFamily.POWERVR, p.vendorFamily)
    }

    @Test
    fun `vendor family inference - Xclipse Samsung`() {
        val p = GraphicsDriverProbe.DriverProfile.from(
            glesMajor = 3, glesMinor = 2,
            hardware = "exynos2200", manufacturer = "samsung"
        )
        assertEquals(GraphicsDriverProbe.VendorFamily.XCLIPSE, p.vendorFamily)
    }

    @Test
    fun `vendor family inference - unknown`() {
        val p = GraphicsDriverProbe.DriverProfile.from(
            glesMajor = 3, glesMinor = 0,
            hardware = "novel-soc", manufacturer = "future-corp"
        )
        assertEquals(GraphicsDriverProbe.VendorFamily.UNKNOWN, p.vendorFamily)
    }

    @Test
    fun `summary mentions Vulkan presence and AEP`() {
        val s = GraphicsDriverProbe.DriverProfile.from(
            glesMajor = 3, glesMinor = 2,
            vulkanLevel = 1, vulkanVersion = 0x40300, vulkanDeqp = 132317184,
            aep = true,
            hardware = "qcom", manufacturer = "qualcomm"
        ).summary()
        assertTrue(s, s.contains("GLES 3.2"))
        assertTrue(s, s.contains("AEP"))
        assertTrue(s, s.contains("Vulkan"))
        assertTrue(s, s.contains("ADRENO"))
    }

    @Test
    fun `UNKNOWN profile fails minimum check`() {
        assertFalse(GraphicsDriverProbe.DriverProfile.UNKNOWN.meetsMinimumGles)
        assertFalse(GraphicsDriverProbe.DriverProfile.UNKNOWN.hasVulkan)
    }
}
