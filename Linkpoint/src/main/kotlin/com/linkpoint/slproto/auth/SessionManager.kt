package com.linkpoint.slproto.auth

import java.util.concurrent.atomic.AtomicReference

/**
 * Stores the most recent successful authentication session so that networking
 * layers can reuse session identifiers and credentials across reconnects.
 */
object SessionManager {

    data class Session(
        val params: SLAuthParams,
        val reply: SLAuthReply,
        val createdAtMillis: Long = System.currentTimeMillis()
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
