package com.linkpoint.slproto.https

import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class GenericHTTPExecutor private constructor() : ThreadPoolExecutor(
    1,
    3,
    60,
    TimeUnit.SECONDS,
    LinkedBlockingQueue(),
    { runnable -> Thread(runnable, "HTTPAccess") }
) {
    init {
        allowCoreThreadTimeOut(true)
    }

    companion object {
        @JvmStatic
        val instance: ExecutorService by lazy { GenericHTTPExecutor() }
    }
}