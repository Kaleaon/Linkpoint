package com.linkpoint.slproto.auth

import java.io.IOException

/**
 * Second Life Authentication Handler
 * Modernized from original Lumiya Viewer
 * 
 * This is a wrapper around SLAuthImpl for backward compatibility
 */
class SLAuth {
    private val impl = SLAuthImpl()

    @Throws(IOException::class)
    suspend fun sendLoginRequest(params: SLAuthParams): SLAuthReply {
        return impl.sendLoginRequest(params)
    }
    
    companion object {
        /**
         * Generate MD5 hash for password
         */
        fun md5Hash(input: String): String {
            return SLAuthImpl.md5Hash(input)
        }
    }
}