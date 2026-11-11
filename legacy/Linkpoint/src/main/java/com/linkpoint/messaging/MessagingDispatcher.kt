package com.linkpoint.messaging

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * Single-threaded dispatcher for serialized messaging work.
 *
 * All inbound chat/IM/group processing should use this dispatcher to keep message
 * handling isolated from Default/IO and to preserve ordering.
 */
object MessagingDispatcher {
    private val executor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "MessageThread").apply { isDaemon = true }
    }

    val dispatcher: CoroutineDispatcher = executor.asCoroutineDispatcher()
}
