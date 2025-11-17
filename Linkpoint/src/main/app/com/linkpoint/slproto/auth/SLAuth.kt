package com.linkpoint.slproto.auth

import java.io.IOException

/**
 * Thin wrapper over [SLAuthImpl] retained for compatibility with the legacy
 * slproto APIs.
 */
class SLAuth {
    private val impl = SLAuthImpl()

    @Throws(IOException::class)
    suspend fun sendLoginRequest(params: SLAuthParams): SLAuthReply {
        return impl.sendLoginRequest(params)
    }

    companion object {
        fun md5Hash(input: String): String = SLAuthImpl.md5Hash(input)
    }
}
