package com.linkpoint.slproto.auth

import android.content.Intent
import com.linkpoint.utils.UUIDPool
import java.util.UUID

/**
 * Immutable parameters required to authenticate against the Second Life grid.
 */
data class SLAuthParams(
    val loginName: String,
    val passwordHash: String,
    val clientId: UUID,
    val startLocation: String,
    val loginUrl: String,
    val gridName: String
) {

    constructor(intent: Intent) : this(
        loginName = intent.getStringExtra(EXTRA_LOGIN)?.takeIf { it.isNotBlank() }
            ?: DEFAULT_LOGIN,
        passwordHash = intent.getStringExtra(EXTRA_PASSWORD)?.takeIf { it.isNotBlank() }
            ?: DEFAULT_PASSWORD,
        clientId = UUIDPool.getUUID(intent.getStringExtra(EXTRA_CLIENT_ID)),
        startLocation = intent.getStringExtra(EXTRA_START_LOCATION)?.takeIf { it.isNotBlank() }
            ?: DEFAULT_START_LOCATION,
        loginUrl = intent.getStringExtra(EXTRA_LOGIN_URL)?.takeIf { it.isNotBlank() }
            ?: DEFAULT_LOGIN_URL,
        gridName = intent.getStringExtra(EXTRA_GRID_NAME)?.takeIf { it.isNotBlank() }
            ?: DEFAULT_GRID_NAME
    )

    fun withLocation(location: String): SLAuthParams = copy(startLocation = location)

    companion object {
        private const val EXTRA_LOGIN = "login"
        private const val EXTRA_PASSWORD = "password"
        private const val EXTRA_CLIENT_ID = "client_id"
        private const val EXTRA_START_LOCATION = "start_location"
        private const val EXTRA_LOGIN_URL = "login_url"
        private const val EXTRA_GRID_NAME = "grid_name"

        private const val DEFAULT_LOGIN = ""
        private const val DEFAULT_PASSWORD = ""
        private const val DEFAULT_START_LOCATION = "last"
        private const val DEFAULT_LOGIN_URL = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
        private const val DEFAULT_GRID_NAME = "Second Life"
    }
}
