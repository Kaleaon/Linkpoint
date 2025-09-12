package com.lumiyaviewer.lumiya.slproto.auth;

import android.content.Intent;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

public class SLAuthParams {
    public final UUID clientID;
    public final String gridName;
    public final String loginName;
    public final String loginURL;
    public final String passwordHash;
    public final String startLocation;

    public SLAuthParams(Intent intent) {
        this.loginName = intent.getStringExtra("login");
        this.passwordHash = intent.getStringExtra("password");
        this.clientID = UUIDPool.getUUID(intent.getStringExtra("client_id"));
        this.startLocation = intent.getStringExtra("start_location");
        this.loginURL = intent.getStringExtra("login_url");
        this.gridName = intent.getStringExtra("grid_name");
    }

    public SLAuthParams(String str, String str2, UUID uuid, String str3, String str4, String str5) {
        this.loginName = str;
        this.passwordHash = str2;
        this.clientID = uuid;
        this.startLocation = str3;
        this.loginURL = str4;
        this.gridName = str5;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SLAuthParams sLAuthParams = (SLAuthParams) obj;
        if (this.loginName == null ? sLAuthParams.loginName != null : (!this.loginName.equals(sLAuthParams.loginName))) {
            return false;
        }
        if (this.passwordHash == null ? sLAuthParams.passwordHash != null : (!this.passwordHash.equals(sLAuthParams.passwordHash))) {
            return false;
        }
        if (this.clientID == null ? sLAuthParams.clientID != null : (!this.clientID.equals(sLAuthParams.clientID))) {
            return false;
        }
        if (this.startLocation == null ? sLAuthParams.startLocation != null : (!this.startLocation.equals(sLAuthParams.startLocation))) {
            return false;
        }
        if (this.loginURL == null ? sLAuthParams.loginURL != null : (!this.loginURL.equals(sLAuthParams.loginURL))) {
            return false;
        }
        return this.gridName != null ? this.gridName.equals(sLAuthParams.gridName) : sLAuthParams.gridName == null;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((this.loginURL != null ? this.loginURL.hashCode() : 0) + (((this.startLocation != null ? this.startLocation.hashCode() : 0) + (((this.clientID != null ? this.clientID.hashCode() : 0) + (((this.passwordHash != null ? this.passwordHash.hashCode() : 0) + ((this.loginName != null ? this.loginName.hashCode() : 0) * 31)) * 31)) * 31)) * 31)) * 31;
        if (this.gridName != null) {
            i = this.gridName.hashCode();
        }
        return hashCode + i;
    }

    public SLAuthParams withLocation(String str) {
        return new SLAuthParams(this.loginName, this.passwordHash, this.clientID, str, this.loginURL, this.gridName);
    }
}
