package com.linkpoint.slproto.auth

import android.content.Intent
import com.linkpoint.utils.UUIDPool
import java.util.UUID

class SLAuthParams {
    UUID clientID
    String gridName
    String loginName
    String loginURL
    String passwordHash
    String startLocation

    constructor(intent: Intent) {
        this.loginName = intent.getStringExtra("login")
        this.passwordHash = intent.getStringExtra("password")
        this.clientID = UUIDPool.getUUID(intent.getStringExtra("client_id"))
        this.startLocation = intent.getStringExtra("start_location")
        this.loginURL = intent.getStringExtra("login_url")
        this.gridName = intent.getStringExtra("grid_name")
    }

    constructor(str: String, str2: String, uuid: UUID, str3: String, str4: String, str5: String) {
        this.loginName = str
        this.passwordHash = str2
        this.clientID = uuid
        this.startLocation = str3
        this.loginURL = str4
        this.gridName = str5
    }

    fun equals(obj: Any): Boolean {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false
        }
        SLAuthParams sLAuthParams = (SLAuthParams) obj
        if (this.loginName == null ? sLAuthParams.loginName != null : (!this.loginName == sLAuthParams.loginName)) {
            return false
        }
        if (this.passwordHash == null ? sLAuthParams.passwordHash != null : (!this.passwordHash == sLAuthParams.passwordHash)) {
            return false
        }
        if (this.clientID == null ? sLAuthParams.clientID != null : (!this.clientID == sLAuthParams.clientID)) {
            return false
        }
        if (this.startLocation == null ? sLAuthParams.startLocation != null : (!this.startLocation == sLAuthParams.startLocation)) {
            return false
        }
        if (this.loginURL == null ? sLAuthParams.loginURL != null : (!this.loginURL == sLAuthParams.loginURL)) {
            return false
        }
        return this.gridName != null ? this.gridName == sLAuthParams.gridName : sLAuthParams.gridName == null
    }

    fun hashCode(): Int {
        Int i = 0
        Int hashCode = ((this.loginURL != null ? this.loginURL.hashCode() : 0) + (((this.startLocation != null ? this.startLocation.hashCode() : 0) + (((this.clientID != null ? this.clientID.hashCode() : 0) + (((this.passwordHash != null ? this.passwordHash.hashCode() : 0) + ((this.loginName != null ? this.loginName.hashCode() : 0) * 31)) * 31)) * 31)) * 31)) * 31
        if (this.gridName != null) {
            i = this.gridName.hashCode()
        }
        return hashCode + i
    }

    fun withLocation(str: String): SLAuthParams {
        return SLAuthParams(this.loginName, this.passwordHash, this.clientID, str, this.loginURL, this.gridName)
    }
}
