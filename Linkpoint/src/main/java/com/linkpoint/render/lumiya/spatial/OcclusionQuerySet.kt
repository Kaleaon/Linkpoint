package com.linkpoint.render.lumiya.spatial

import android.opengl.GLES32
import android.util.Log

/**
 * GL ES 3.0+ hardware occlusion-query pool keyed by drawer-supplied tags.
 *
 * Usage per frame, on the GL thread:
 * ```
 *   for (entity in scene) {
 *     if (!queries.shouldDraw(entity.id)) continue   // last frame: 0 samples
 *     queries.beginQuery(entity.id)
 *     drawEntity(entity)
 *     queries.endQuery()
 *   }
 *   queries.harvest()                                // poll results
 * ```
 *
 * Design notes:
 *  - Uses `GL_ANY_SAMPLES_PASSED_CONSERVATIVE` (cheap on tile-based GPUs).
 *  - Results are read with `GL_QUERY_RESULT_AVAILABLE` first to avoid
 *    GPU stalls; if not ready we skip the harvest until next frame and
 *    keep the previous decision (fail-open: assume visible).
 *  - Pool growth is unbounded but bounded in practice by the live prim
 *    count; queries are reused once their result is collected.
 *
 * Lineage: Lumiya `BoundingBoxProgram` + `GLQuery` (ES 3 only path).
 */
class OcclusionQuerySet {

    companion object {
        private const val TAG = "OcclusionQuerySet"
        // Query target chosen for tile-based mobile GPUs. Conservative
        // gives slightly conservative (≥ true) sample count which is
        // exactly what we want for visibility — false negatives would
        // pop geometry, false positives just waste a draw.
        private const val QUERY_TARGET = GLES32.GL_ANY_SAMPLES_PASSED_CONSERVATIVE
    }

    /**
     * Per-entity occlusion state. Holds the GL query handle plus the
     * decision the renderer should honour next frame.
     */
    private data class Slot(
        var queryHandle: Int,
        /** True if last completed query reported any samples passed. */
        var visibleLastFrame: Boolean = true,
        /** True if a query is currently in flight (begun, not yet harvested). */
        var inFlight: Boolean = false
    )

    private val slots = HashMap<Long, Slot>()
    private var currentEntity: Long = -1L
    private var enabled: Boolean = true

    /** Toggle the entire system off (reverts to draw-everything). */
    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    fun isEnabled(): Boolean = enabled

    /**
     * Decide whether to issue a draw call for [entityId] this frame.
     * Returns true on first encounter (no prior decision) so we don't
     * pop geometry.
     */
    fun shouldDraw(entityId: Long): Boolean {
        if (!enabled) return true
        val slot = slots[entityId] ?: return true
        return slot.visibleLastFrame
    }

    /** Begin recording samples for [entityId]. Pairs with [endQuery]. */
    fun beginQuery(entityId: Long) {
        if (!enabled) return
        val slot = slots.getOrPut(entityId) { Slot(queryHandle = createHandle()) }
        if (slot.inFlight) {
            // Already begun this frame — defensively end the prior one
            // to keep the GL state machine consistent.
            try { GLES32.glEndQuery(QUERY_TARGET) } catch (_: Throwable) {}
            slot.inFlight = false
        }
        GLES32.glBeginQuery(QUERY_TARGET, slot.queryHandle)
        slot.inFlight = true
        currentEntity = entityId
    }

    fun endQuery() {
        if (!enabled || currentEntity < 0L) return
        GLES32.glEndQuery(QUERY_TARGET)
        // Slot stays in-flight; harvest() flips it back to false.
        currentEntity = -1L
    }

    /**
     * Poll completed queries and update their visibleLastFrame flag.
     * Skips entries whose result isn't ready yet — those keep their
     * previous decision so we don't stall the GPU.
     */
    fun harvest() {
        if (!enabled) return
        val out = IntArray(1)
        for (slot in slots.values) {
            if (!slot.inFlight) continue
            GLES32.glGetQueryObjectuiv(slot.queryHandle, GLES32.GL_QUERY_RESULT_AVAILABLE, out, 0)
            if (out[0] == 0) continue   // not ready yet
            GLES32.glGetQueryObjectuiv(slot.queryHandle, GLES32.GL_QUERY_RESULT, out, 0)
            slot.visibleLastFrame = out[0] != 0
            slot.inFlight = false
        }
    }

    /** Drop a no-longer-tracked entity. Frees its GL query handle. */
    fun forget(entityId: Long) {
        slots.remove(entityId)?.let { slot ->
            if (slot.queryHandle != 0) {
                GLES32.glDeleteQueries(1, intArrayOf(slot.queryHandle), 0)
            }
        }
    }

    /** Free every query handle. Must be called on the GL thread. */
    fun destroy() {
        if (slots.isEmpty()) return
        val handles = IntArray(slots.size)
        var i = 0
        for (slot in slots.values) {
            handles[i++] = slot.queryHandle
        }
        GLES32.glDeleteQueries(handles.size, handles, 0)
        slots.clear()
    }

    fun size(): Int = slots.size

    private fun createHandle(): Int {
        val buf = IntArray(1)
        GLES32.glGenQueries(1, buf, 0)
        if (buf[0] == 0) {
            Log.w(TAG, "glGenQueries returned 0 — driver out of query objects?")
        }
        return buf[0]
    }
}
