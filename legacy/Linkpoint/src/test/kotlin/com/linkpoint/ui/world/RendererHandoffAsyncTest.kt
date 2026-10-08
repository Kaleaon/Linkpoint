package com.linkpoint.ui.world

import com.linkpoint.render.CameraController
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RendererHandoffAsyncTest {

    @Test
    fun `switchBackend executes callbacks sequentially`() = runBlocking {
        val executionOrder = mutableListOf<String>()

        val manager = RendererHandoffManager(
            onPauseActiveBackendDrawing = { backend ->
                executionOrder.add("pause_$backend")
            },
            onFlushPendingRenderUpdates = { backend ->
                executionOrder.add("flush_$backend")
            },
            onDisposeBackendOwnedGpuResources = { backend ->
                executionOrder.add("dispose_$backend")
            },
            onAttachTargetBackendAndReplaySnapshot = { backend, _ ->
                executionOrder.add("attach_$backend")
            },
            snapshotProvider = {
                RendererHandoffManager.SceneStateSnapshot(
                    cameraMode = CameraController.Mode.FOLLOW,
                    cameraYawDeg = 0f,
                    cameraPitchDeg = 0f,
                    cameraFollowDistance = 5f
                )
            }
        )

        // Activate initial backend (Filament)
        assertTrue(manager.activateInitialBackend(RendererHandoffManager.RendererBackend.FILAMENT, "test_init"))
        assertEquals(RendererHandoffManager.State.ACTIVE, manager.currentState())
        assertEquals(RendererHandoffManager.RendererBackend.FILAMENT, manager.currentBackend())

        // Switch backend to Lumiya
        assertTrue(manager.switchBackend(RendererHandoffManager.RendererBackend.LUMIYA, "test_switch"))
        assertEquals(RendererHandoffManager.State.ACTIVE, manager.currentState())
        assertEquals(RendererHandoffManager.RendererBackend.LUMIYA, manager.currentBackend())

        val expectedOrder = listOf(
            "attach_FILAMENT",
            "pause_FILAMENT",
            "flush_FILAMENT",
            "dispose_FILAMENT",
            "attach_LUMIYA"
        )
        assertEquals(expectedOrder, executionOrder)
    }

    @Test
    fun `onHostDisposed executes dispose callback and resets state to IDLE`() = runBlocking {
        var disposedBackend: RendererHandoffManager.RendererBackend? = null

        val manager = RendererHandoffManager(
            onPauseActiveBackendDrawing = {},
            onFlushPendingRenderUpdates = {},
            onDisposeBackendOwnedGpuResources = { backend ->
                disposedBackend = backend
            },
            onAttachTargetBackendAndReplaySnapshot = { _, _ -> },
            snapshotProvider = {
                RendererHandoffManager.SceneStateSnapshot(
                    cameraMode = CameraController.Mode.FOLLOW,
                    cameraYawDeg = 0f,
                    cameraPitchDeg = 0f,
                    cameraFollowDistance = 5f
                )
            }
        )

        manager.activateInitialBackend(RendererHandoffManager.RendererBackend.FILAMENT, "test_init")
        assertEquals(RendererHandoffManager.State.ACTIVE, manager.currentState())

        manager.onHostDisposed()

        assertEquals(RendererHandoffManager.RendererBackend.FILAMENT, disposedBackend)
        assertEquals(RendererHandoffManager.State.IDLE, manager.currentState())
    }
}
