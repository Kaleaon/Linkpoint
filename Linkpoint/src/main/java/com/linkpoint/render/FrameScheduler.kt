package com.linkpoint.render

import android.view.Choreographer
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Drives frame pacing from a single timing source and fans out to:
 *  1) pre-render callbacks (simulation/camera sampling)
 *  2) the currently active backend dispatcher (Filament or GLES)
 */
class FrameScheduler(
    private val timingSource: FrameTimingSource = ChoreographerTimingSource()
) {

    private val running = AtomicBoolean(false)
    private val preRenderCallbacks = CopyOnWriteArrayList<(Long) -> Unit>()
    @Volatile
    private var backendDispatcher: ((Long) -> Unit)? = null

    private val frameListener: (Long) -> Unit = { frameTimeNanos ->
        if (running.get()) {
            preRenderCallbacks.forEach { it.invoke(frameTimeNanos) }
            backendDispatcher?.invoke(frameTimeNanos)
            if (running.get()) {
                timingSource.requestFrame(frameListener)
            }
        }
    }

    fun setBackendDispatcher(dispatcher: ((Long) -> Unit)?) {
        backendDispatcher = dispatcher
    }

    fun addPreRenderCallback(callback: (Long) -> Unit) {
        preRenderCallbacks.add(callback)
    }

    fun removePreRenderCallback(callback: (Long) -> Unit) {
        preRenderCallbacks.remove(callback)
    }

    fun clearPreRenderCallbacks() {
        preRenderCallbacks.clear()
    }

    fun start() {
        if (running.compareAndSet(false, true)) {
            timingSource.requestFrame(frameListener)
        }
    }

    fun stop() {
        if (running.compareAndSet(true, false)) {
            timingSource.cancelFrame(frameListener)
        }
    }
}

interface FrameTimingSource {
    fun requestFrame(callback: (Long) -> Unit)
    fun cancelFrame(callback: (Long) -> Unit)
}

class ChoreographerTimingSource : FrameTimingSource {
    private val callbackMap = mutableMapOf<(Long) -> Unit, Choreographer.FrameCallback>()

    override fun requestFrame(callback: (Long) -> Unit) {
        val frameCallback = callbackMap.getOrPut(callback) {
            Choreographer.FrameCallback { frameTimeNanos -> callback.invoke(frameTimeNanos) }
        }
        Choreographer.getInstance().postFrameCallback(frameCallback)
    }

    override fun cancelFrame(callback: (Long) -> Unit) {
        val frameCallback = callbackMap.remove(callback) ?: return
        Choreographer.getInstance().removeFrameCallback(frameCallback)
    }
}
