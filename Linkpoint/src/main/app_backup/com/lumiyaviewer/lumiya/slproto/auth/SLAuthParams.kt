package com.lumiyaviewer.lumiya.slproto.auth

import android.content.Intent
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.util.UUID

class SLAuthParams {
    var clientID: UUID? = null
    var gridName: String? = null
    var loginName: String? = null
    var loginURL: String? = null
    var passwordHash: String? = null
    var startLocation: String? = null

    constructor(intent: Intent) {
        this.loginName = intent.getStringExtra("login")
        this.passwordHash = intent.getStringExtra("password")
        val clientIdStr = intent.getStringExtra("client_id")
        this.clientID = if (clientIdStr != null) UUIDPool.getUUID(clientIdStr) else null
        this.startLocation = intent.getStringExtra("start_location")
        this.loginURL = intent.getStringExtra("login_url")
        this.gridName = intent.getStringExtra("grid_name")
    }

    constructor(
        loginName: String?,
        passwordHash: String?,
        clientID: UUID?,
        startLocation: String?,
        loginURL: String?,
        gridName: String?
    ) {
        this.loginName = loginName
        this.passwordHash = passwordHash
        this.clientID = clientID
        this.startLocation = startLocation
        this.loginURL = loginURL
        this.gridName = gridName
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SLAuthParams

        if (loginName != other.loginName) return false
        if (passwordHash != other.passwordHash) return false
        if (clientID != other.clientID) return false
        if (startLocation != other.startLocation) return false
        if (loginURL != other.loginURL) return false
        if (gridName != other.gridName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clientID?.hashCode() ?: 0
        result = 31 * result + (gridName?.hashCode() ?: 0)
        result = 31 * result + (loginName?.hashCode() ?: 0)
        result = 31 * result + (loginURL?.hashCode() ?: 0)
        result = 31 * result + (passwordHash?.hashCode() ?: 0)
        result = 31 * result + (startLocation?.hashCode() ?: 0)
        return result
    }

    fun withLocation(location: String): SLAuthParams {
        return SLAuthParams(loginName, passwordHash, clientID, location, loginURL, gridName)
    }
}
