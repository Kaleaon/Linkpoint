package com.linkpoint.slproto.auth

import android.content.Intent
import com.linkpoint.utils.UUIDPool
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

    constructor(loginName: String?, passwordHash: String?, clientID: UUID?, startLocation: String?, loginURL: String?, gridName: String?) {
        this.loginName = loginName
        this.passwordHash = passwordHash
        this.clientID = clientID
        this.startLocation = startLocation
        this.loginURL = loginURL
        this.gridName = gridName
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        
        val that = other as SLAuthParams
        
        if (loginName != that.loginName) return false
        if (passwordHash != that.passwordHash) return false
        if (clientID != that.clientID) return false
        if (startLocation != that.startLocation) return false
        if (loginURL != that.loginURL) return false
        return gridName == that.gridName
    }

    override fun hashCode(): Int {
        var result = loginURL?.hashCode() ?: 0
        result = 31 * result + (startLocation?.hashCode() ?: 0)
        result = 31 * result + (clientID?.hashCode() ?: 0)
        result = 31 * result + (passwordHash?.hashCode() ?: 0)
        result = 31 * result + (loginName?.hashCode() ?: 0)
        result = 31 * result + (gridName?.hashCode() ?: 0)
        return result
    }

    fun withLocation(newLocation: String?): SLAuthParams {
        return SLAuthParams(this.loginName, this.passwordHash, this.clientID, newLocation, this.loginURL, this.gridName)
    }
}
