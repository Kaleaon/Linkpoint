package com.linkpoint.render

import android.os.Handler
import android.os.HandlerThread
import java.util.concurrent.CountDownLatch

/**
 * Single-thread dispatcher for render work.
 * Ensures Filament calls happen on a dedicated render thread.
 */
class RenderThreadDispatcher(threadName: String = "RenderThread") {

    private val thread = HandlerThread(threadName).apply { start() }
    private val handler = Handler(thread.looper)

    val renderThreadName: String = thread.name

    fun isRenderThread(): Boolean = Thread.currentThread() === thread

    fun post(task: Runnable) {
        handler.post(task)
    }

    fun postDelayed(task: Runnable, delayMillis: Long) {
        handler.postDelayed(task, delayMillis)
    }

    fun <T> runBlocking(task: () -> T): T {
        if (isRenderThread()) {
            return task()
        }
        val latch = CountDownLatch(1)
        var result: Result<T>? = null
        handler.post {
            result = runCatching { task() }
            latch.countDown()
        }
        latch.await()
        return result!!.getOrThrow()
    }

    fun shutdown() {
        thread.quitSafely()
    }
}
