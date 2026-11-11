package com.linkpoint.render

import android.os.Handler
import android.os.HandlerThread
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Single-thread dispatcher for render work.
 * Ensures Filament calls happen on a dedicated render thread.
 */
class RenderThreadDispatcher(threadName: String = "RenderThread") {

    private val thread = HandlerThread(threadName).apply { start() }
    private val handler = Handler(thread.looper)

    val dispatcher: CoroutineDispatcher = handler.asCoroutineDispatcher("RenderThreadDispatcher")

    val renderThreadName: String = thread.name

    fun isRenderThread(): Boolean = Thread.currentThread() === thread

    suspend fun <T> execute(task: () -> T): T = withContext(dispatcher) {
        task()
    }

    fun post(task: Runnable) {
        handler.post(task)
    }

    fun postAsync(task: Runnable) {
        handler.post(task)
    }

    fun postDelayed(task: Runnable, delayMillis: Long) {
        handler.postDelayed(task, delayMillis)
    }

    fun shutdown() {
        thread.quitSafely()
    }
}
