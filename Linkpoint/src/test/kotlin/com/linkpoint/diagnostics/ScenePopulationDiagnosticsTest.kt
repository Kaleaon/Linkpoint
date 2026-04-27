package com.linkpoint.diagnostics

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ScenePopulationDiagnosticsTest {

    @Before
    fun setUp() {
        ScenePopulationDiagnostics.reset()
    }

    @Test
    fun smokeCheckFailsWithoutHandshake() {
        ScenePopulationDiagnostics.markPacketReceived(ScenePopulationDiagnostics.EntityType.OBJECT)

        val result = ScenePopulationDiagnostics.runSmokeCheck(totalObjectsInScene = 1, totalAvatarsInScene = 0)

        assertFalse(result.passed)
        assertTrue(result.reasons.any { it.contains("handshake", ignoreCase = true) })
    }

    @Test
    fun smokeCheckFailsWhenTrafficButNoPopulation() {
        ScenePopulationDiagnostics.markRegionHandshakeComplete()
        ScenePopulationDiagnostics.markPacketReceived(ScenePopulationDiagnostics.EntityType.OBJECT, 2)
        ScenePopulationDiagnostics.markPacketReceived(ScenePopulationDiagnostics.EntityType.AVATAR, 1)

        val result = ScenePopulationDiagnostics.runSmokeCheck(totalObjectsInScene = 0, totalAvatarsInScene = 0)

        assertFalse(result.passed)
        assertTrue(result.reasons.any { it.contains("Object packets", ignoreCase = true) })
        assertTrue(result.reasons.any { it.contains("Avatar packets", ignoreCase = true) })
    }

    @Test
    fun smokeCheckPassesWhenHandshakeAndPopulationPresent() {
        ScenePopulationDiagnostics.markRegionHandshakeComplete()
        ScenePopulationDiagnostics.markPacketReceived(ScenePopulationDiagnostics.EntityType.OBJECT, 2)
        ScenePopulationDiagnostics.markPacketReceived(ScenePopulationDiagnostics.EntityType.AVATAR, 1)

        val result = ScenePopulationDiagnostics.runSmokeCheck(totalObjectsInScene = 2, totalAvatarsInScene = 1)

        assertTrue(result.passed)
    }
}
