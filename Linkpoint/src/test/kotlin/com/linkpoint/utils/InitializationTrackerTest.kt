package com.linkpoint.utils

import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class InitializationTrackerTest {

    @Test
    fun `startPhase marks phase pending`() {
        InitializationTracker.startSession()
        InitializationTracker.startPhase(InitializationTracker.Phase.LOGIN_STARTING, "begin")

        val diagnostics = InitializationTracker.getDiagnostics()

        assertEquals(
            "Current phase should match the started phase",
            InitializationTracker.Phase.LOGIN_STARTING,
            diagnostics.currentPhase
        )
        assertTrue(
            "Started phase should be pending",
            diagnostics.pendingPhases.contains(InitializationTracker.Phase.LOGIN_STARTING)
        )
        assertTrue("No phases should be completed", diagnostics.completedPhases.isEmpty())
        assertTrue("No phases should be failed", diagnostics.failedPhases.isEmpty())
    }

    @Test
    fun `failPhase marks phase failed and not pending`() {
        InitializationTracker.startSession()
        InitializationTracker.startPhase(InitializationTracker.Phase.LOGIN_HTTP_REQUEST, "request")
        InitializationTracker.failPhase(InitializationTracker.Phase.LOGIN_HTTP_REQUEST, "network error")

        val diagnostics = InitializationTracker.getDiagnostics()

        assertTrue(
            "Failed phase should be tracked",
            diagnostics.failedPhases.contains(InitializationTracker.Phase.LOGIN_HTTP_REQUEST)
        )
        assertFalse(
            "Failed phase should not be pending",
            diagnostics.pendingPhases.contains(InitializationTracker.Phase.LOGIN_HTTP_REQUEST)
        )
        assertFalse(
            "Failed phase should not be completed",
            diagnostics.completedPhases.contains(InitializationTracker.Phase.LOGIN_HTTP_REQUEST)
        )
    }

    @Test
    fun `completePhase marks phase complete and not pending`() {
        InitializationTracker.startSession()
        InitializationTracker.startPhase(InitializationTracker.Phase.LOGIN_RESPONSE_PARSING, "parsing")
        InitializationTracker.completePhase(InitializationTracker.Phase.LOGIN_RESPONSE_PARSING, "done")

        val diagnostics = InitializationTracker.getDiagnostics()

        assertTrue(
            "Completed phase should be tracked",
            diagnostics.completedPhases.contains(InitializationTracker.Phase.LOGIN_RESPONSE_PARSING)
        )
        assertFalse(
            "Completed phase should not be pending",
            diagnostics.pendingPhases.contains(InitializationTracker.Phase.LOGIN_RESPONSE_PARSING)
        )
        assertFalse(
            "Completed phase should not be failed",
            diagnostics.failedPhases.contains(InitializationTracker.Phase.LOGIN_RESPONSE_PARSING)
        )
    }

    @Test
    fun `reachPhase marks milestone phase complete without a separate completePhase`() {
        InitializationTracker.startSession()
        InitializationTracker.reachPhase(InitializationTracker.Phase.CAPABILITIES_READY, "caps ready")

        val diagnostics = InitializationTracker.getDiagnostics()

        assertEquals(
            "Current phase should track the reached milestone",
            InitializationTracker.Phase.CAPABILITIES_READY,
            diagnostics.currentPhase
        )
        assertTrue(
            "Reached phase should show as completed",
            diagnostics.completedPhases.contains(InitializationTracker.Phase.CAPABILITIES_READY)
        )
        assertFalse(
            "Reached phase should not show as pending",
            diagnostics.pendingPhases.contains(InitializationTracker.Phase.CAPABILITIES_READY)
        )
        assertFalse(
            "Reached phase should not show as failed",
            diagnostics.failedPhases.contains(InitializationTracker.Phase.CAPABILITIES_READY)
        )
    }
}
