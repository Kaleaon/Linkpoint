package com.linkpoint.slproto.auth

import java.util.concurrent.atomic.AtomicReference

/**
 * Stores the latest successful authentication session so reconnects can reuse credentials.
 */
object SessionManager {

    data class Session(
        val params: SLAuthParams,
        val reply: SLAuthReply,
        val createdAtMillis: Long = System.currentTimeMillis(),
    )

    private val activeSession = AtomicReference<Session?>()

    fun store(params: SLAuthParams, reply: SLAuthReply) {
        activeSession.set(Session(params, reply))
    }

    fun current(): Session? = activeSession.get()

    fun clear() {
        activeSession.set(null)
    }
}
