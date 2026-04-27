package com.linkpoint.ui.world

import android.util.Log
import com.linkpoint.render.CameraController

/**
 * Serial orchestration for renderer backend handoff.
 *
 * Handoff sequence:
 * 1) Pause active backend drawing.
 * 2) Flush pending render updates.
 * 3) Dispose backend-owned GPU resources.
 * 4) Attach target backend and replay scene snapshot.
 */
class RendererHandoffManager(
    private val onPauseActiveBackendDrawing: (RendererBackend) -> Unit,
    private val onFlushPendingRenderUpdates: (RendererBackend) -> Unit,
    private val onDisposeBackendOwnedGpuResources: (RendererBackend) -> Unit,
    private val onAttachTargetBackendAndReplaySnapshot: (RendererBackend, SceneStateSnapshot) -> Unit,
    private val snapshotProvider: () -> SceneStateSnapshot
) {

    companion object {
        private const val TAG = "RendererHandoffManager"
    }

    enum class State {
        IDLE,
        SWITCHING,
        ACTIVE
    }

    enum class RendererBackend {
        FILAMENT,
        LUMIYA
    }

    data class SceneStateSnapshot(
        val cameraMode: CameraController.Mode,
        val cameraYawDeg: Float,
        val cameraPitchDeg: Float,
        val cameraFollowDistance: Float
    )

    private val lock = Any()

    @Volatile
    private var state: State = State.IDLE

    @Volatile
    private var activeBackend: RendererBackend? = null

    fun currentState(): State = state

    fun currentBackend(): RendererBackend? = activeBackend

    fun activateInitialBackend(targetBackend: RendererBackend, reason: String): Boolean {
        synchronized(lock) {
            if (state == State.SWITCHING) {
                Log.w(TAG, "Ignoring initial activation while switching (reason=$reason)")
                return false
            }
            if (activeBackend == targetBackend && state == State.ACTIVE) {
                Log.d(TAG, "Initial activation skipped; backend already active: $targetBackend")
                return false
            }
            state = State.SWITCHING
        }

        val snapshot = snapshotProvider()
        return try {
            onAttachTargetBackendAndReplaySnapshot(targetBackend, snapshot)
            synchronized(lock) {
                activeBackend = targetBackend
                state = State.ACTIVE
            }
            Log.i(TAG, "Initial backend activation complete: $targetBackend (reason=$reason)")
            true
        } catch (t: Throwable) {
            synchronized(lock) {
                state = if (activeBackend == null) State.IDLE else State.ACTIVE
            }
            Log.e(TAG, "Initial backend activation failed for $targetBackend", t)
            throw t
        }
    }

    fun switchBackend(targetBackend: RendererBackend, reason: String): Boolean {
        val sourceBackend: RendererBackend?
        synchronized(lock) {
            if (state == State.SWITCHING) {
                Log.w(TAG, "Ignoring switch request while switching (target=$targetBackend, reason=$reason)")
                return false
            }
            if (state == State.ACTIVE && activeBackend == targetBackend) {
                Log.d(TAG, "Switch skipped; backend already active: $targetBackend")
                return false
            }
            state = State.SWITCHING
            sourceBackend = activeBackend
        }

        val snapshot = snapshotProvider()
        return try {
            sourceBackend?.let { backend ->
                onPauseActiveBackendDrawing(backend)
                onFlushPendingRenderUpdates(backend)
                onDisposeBackendOwnedGpuResources(backend)
            }
            onAttachTargetBackendAndReplaySnapshot(targetBackend, snapshot)
            synchronized(lock) {
                activeBackend = targetBackend
                state = State.ACTIVE
            }
            Log.i(TAG, "Renderer backend switch complete: $sourceBackend -> $targetBackend (reason=$reason)")
            true
        } catch (t: Throwable) {
            synchronized(lock) {
                state = if (activeBackend == null) State.IDLE else State.ACTIVE
            }
            Log.e(TAG, "Renderer backend switch failed: $sourceBackend -> $targetBackend", t)
            throw t
        }
    }
}
