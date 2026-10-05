package com.linkpoint.render.shadow

import com.linkpoint.render.driver.GpuCapabilities
import com.linkpoint.render.driver.GraphicsDriverProbe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdaptiveShadowPipelineTest {

    @Test
    fun testGpuTierClassifier_HighEndHardware_ClassifiesTier1() {
        val driverProfile = GraphicsDriverProbe.DriverProfile.from(
            glesMajor = 3,
            glesMinor = 2,
            vulkanLevel = 1,
            vulkanVersion = 1,
            hardware = "qcom",
            manufacturer = "samsung"
        )
        val caps = GpuCapabilities.EMPTY.copy(
            glVersion = 32,
            rendererString = "Adreno (TM) 750",
            vendorString = "Qualcomm",
            maxTextureSize = 8192
        )

        val tier = GpuTierClassifier.classifyTier(driverProfile, caps)
        assertEquals(GpuTier.TIER_1, tier)
    }

    @Test
    fun testGpuTierClassifier_MidRangeHardware_ClassifiesTier2() {
        val driverProfile = GraphicsDriverProbe.DriverProfile.from(
            glesMajor = 3,
            glesMinor = 1,
            vulkanLevel = 1,
            hardware = "qcom",
            manufacturer = "xiaomi"
        )
        val caps = GpuCapabilities.EMPTY.copy(
            glVersion = 31,
            rendererString = "Adreno (TM) 650",
            vendorString = "Qualcomm",
            maxTextureSize = 4096
        )

        val tier = GpuTierClassifier.classifyTier(driverProfile, caps)
        assertEquals(GpuTier.TIER_2, tier)
    }

    @Test
    fun testGpuTierClassifier_BudgetHardware_ClassifiesTier3() {
        val driverProfile = GraphicsDriverProbe.DriverProfile.from(
            glesMajor = 3,
            glesMinor = 0,
            vulkanLevel = -1,
            hardware = "mt6762",
            manufacturer = "motorola"
        )
        val caps = GpuCapabilities.EMPTY.copy(
            glVersion = 30,
            rendererString = "Mali-G52",
            vendorString = "ARM",
            maxTextureSize = 2048
        )

        val tier = GpuTierClassifier.classifyTier(driverProfile, caps)
        assertEquals(GpuTier.TIER_3, tier)
    }

    @Test
    fun testTierSpecifications_AcceptanceCriteria() {
        val tier1Config = GpuTier.TIER_1.defaultConfig
        assertEquals(1024, tier1Config.mapSize)
        assertEquals(2, tier1Config.cascadeCount)
        assertEquals(35.0f, tier1Config.shadowDistance, 0.01f)
        assertTrue(tier1Config.enableDynamicShadowMaps)
        assertTrue(tier1Config.hasDynamicShadowMapDrawCalls)

        val tier2Config = GpuTier.TIER_2.defaultConfig
        assertEquals(512, tier2Config.mapSize)
        assertEquals(1, tier2Config.cascadeCount)
        assertEquals(15.0f, tier2Config.shadowDistance, 0.01f)
        assertTrue(tier2Config.enableDynamicShadowMaps)
        assertTrue(tier2Config.hasDynamicShadowMapDrawCalls)

        val tier3Config = GpuTier.TIER_3.defaultConfig
        assertEquals(0, tier3Config.mapSize)
        assertEquals(0, tier3Config.cascadeCount)
        assertFalse(tier3Config.enableDynamicShadowMaps)
        assertFalse(tier3Config.hasDynamicShadowMapDrawCalls)
        assertTrue(tier3Config.screenSpaceContactShadows)
        assertTrue(tier3Config.contactShadowStepCount <= 4)
    }

    @Test
    fun testShadowLodCuller_ScreenAreaBelow2Percent_ExcludedFromShadowPass() {
        // Small prim (0.2m x 0.2m x 0.2m) far from camera (50m away)
        val shouldCast = ShadowLodCuller.shouldCastShadow(
            extentsX = 0.2f, extentsY = 0.2f, extentsZ = 0.2f,
            distanceToCamera = 50.0f,
            fovDegrees = 60.0f,
            viewportWidth = 1080,
            viewportHeight = 1920,
            minScreenAreaPercent = 2.0f
        )
        assertFalse("Small object below 2% screen area must be excluded from shadow pass", shouldCast)
    }

    @Test
    fun testShadowLodCuller_ScreenAreaAbove2Percent_IncludedInShadowPass() {
        // Large object / avatar close to camera (2.0m x 2.0m x 2.0m at 5m away)
        val shouldCast = ShadowLodCuller.shouldCastShadow(
            extentsX = 2.0f, extentsY = 2.0f, extentsZ = 2.0f,
            distanceToCamera = 5.0f,
            fovDegrees = 60.0f,
            viewportWidth = 1080,
            viewportHeight = 1920,
            minScreenAreaPercent = 2.0f
        )
        assertTrue("Large object >= 2% screen area must be included in shadow pass", shouldCast)
    }

    @Test
    fun testAdaptiveShadowController_FrameTimeSpikes_TriggersDownscalingWithin3Frames() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)

        val controller = AdaptiveShadowController(
            bootTier = GpuTier.TIER_1,
            targetFps = 30, // Frame budget ~33.3ms
            scope = testScope
        )
        testScheduler.advanceUntilIdle()

        assertEquals(GpuTier.TIER_1, controller.currentConfig.tier)

        // Simulate 3 consecutive frame spikes exceeding 33.3ms budget (e.g. 45ms = 45_000_000ns)
        controller.recordFrameTime(45_000_000L)
        controller.recordFrameTime(48_000_000L)
        controller.recordFrameTime(50_000_000L)

        testScheduler.advanceUntilIdle()

        assertEquals("Frame spikes must trigger downscaling to Tier 2 within 3 frame cycles", GpuTier.TIER_2, controller.currentConfig.tier)
    }

    @Test
    fun testAdaptiveShadowController_UserOverride_OverridesDynamicScaling() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)

        val controller = AdaptiveShadowController(
            bootTier = GpuTier.TIER_1,
            targetFps = 30,
            scope = testScope
        )
        testScheduler.advanceUntilIdle()

        // Set manual user override in settings to TIER_3
        controller.userTierOverride = GpuTier.TIER_3
        testScheduler.advanceUntilIdle()

        assertEquals(GpuTier.TIER_3, controller.currentConfig.tier)

        // Record frame time spikes — manual override should remain unchanged
        controller.recordFrameTime(50_000_000L)
        controller.recordFrameTime(50_000_000L)
        controller.recordFrameTime(50_000_000L)
        testScheduler.advanceUntilIdle()

        assertEquals(GpuTier.TIER_3, controller.currentConfig.tier)
    }

    @Test
    fun testContactShadowPass_StepCountClamping_FitsTimeBudget() {
        val tier3Opts = ScreenSpaceContactShadowPass.createShadowOptions(GpuTier.TIER_3.defaultConfig)
        assertTrue(tier3Opts.screenSpaceContactShadows)
        assertTrue("Tier 3 contact shadow step count must be <= 4 for <=0.5ms budget", tier3Opts.stepCount <= 4)
    }
}
