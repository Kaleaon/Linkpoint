package com.lumiyaviewer.lumiya.slproto.https

import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class GenericHTTPExecutor private constructor() : ThreadPoolExecutor(
    1,
    3,
    60L,
    TimeUnit.SECONDS,
    LinkedBlockingQueue(),
    ThreadFactory { runnable: Runnable -> Thread(runnable, "HTTPAccess") }
) {
    init {
        allowCoreThreadTimeOut(true)
    }

    companion object {
        private val instance: GenericHTTPExecutor = GenericHTTPExecutor()

        @JvmStatic
        fun getInstance(): ExecutorService = instance
    }
}
