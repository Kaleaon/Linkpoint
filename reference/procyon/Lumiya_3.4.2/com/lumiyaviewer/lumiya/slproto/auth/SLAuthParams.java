// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.auth;

import com.lumiyaviewer.lumiya.utils.UUIDPool;
import android.content.Intent;
import java.util.UUID;

public class SLAuthParams
{
    public final UUID clientID;
    public final String gridName;
    public final String loginName;
    public final String loginURL;
    public final String passwordHash;
    public final String startLocation;
    
    public SLAuthParams(final Intent intent) {
        this.loginName = intent.getStringExtra("login");
        this.passwordHash = intent.getStringExtra("password");
        this.clientID = UUIDPool.getUUID(intent.getStringExtra("client_id"));
        this.startLocation = intent.getStringExtra("start_location");
        this.loginURL = intent.getStringExtra("login_url");
        this.gridName = intent.getStringExtra("grid_name");
    }
    
    public SLAuthParams(final String loginName, final String passwordHash, final UUID clientID, final String startLocation, final String loginURL, final String gridName) {
        this.loginName = loginName;
        this.passwordHash = passwordHash;
        this.clientID = clientID;
        this.startLocation = startLocation;
        this.loginURL = loginURL;
        this.gridName = gridName;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean equals = true;
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final SLAuthParams slAuthParams = (SLAuthParams)o;
        Label_0063: {
            if (this.loginName != null) {
                if (!(this.loginName.equals(slAuthParams.loginName) ^ true)) {
                    break Label_0063;
                }
            }
            else if (slAuthParams.loginName == null) {
                break Label_0063;
            }
            return false;
        }
        Label_0095: {
            if (this.passwordHash != null) {
                if (!(this.passwordHash.equals(slAuthParams.passwordHash) ^ true)) {
                    break Label_0095;
                }
            }
            else if (slAuthParams.passwordHash == null) {
                break Label_0095;
            }
            return false;
        }
        Label_0127: {
            if (this.clientID != null) {
                if (!(this.clientID.equals(slAuthParams.clientID) ^ true)) {
                    break Label_0127;
                }
            }
            else if (slAuthParams.clientID == null) {
                break Label_0127;
            }
            return false;
        }
        Label_0159: {
            if (this.startLocation != null) {
                if (!(this.startLocation.equals(slAuthParams.startLocation) ^ true)) {
                    break Label_0159;
                }
            }
            else if (slAuthParams.startLocation == null) {
                break Label_0159;
            }
            return false;
        }
        Label_0191: {
            if (this.loginURL != null) {
                if (!(this.loginURL.equals(slAuthParams.loginURL) ^ true)) {
                    break Label_0191;
                }
            }
            else if (slAuthParams.loginURL == null) {
                break Label_0191;
            }
            return false;
        }
        if (this.gridName != null) {
            equals = this.gridName.equals(slAuthParams.gridName);
        }
        else if (slAuthParams.gridName != null) {
            equals = false;
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        int hashCode2;
        if (this.loginName != null) {
            hashCode2 = this.loginName.hashCode();
        }
        else {
            hashCode2 = 0;
        }
        int hashCode3;
        if (this.passwordHash != null) {
            hashCode3 = this.passwordHash.hashCode();
        }
        else {
            hashCode3 = 0;
        }
        int hashCode4;
        if (this.clientID != null) {
            hashCode4 = this.clientID.hashCode();
        }
        else {
            hashCode4 = 0;
        }
        int hashCode5;
        if (this.startLocation != null) {
            hashCode5 = this.startLocation.hashCode();
        }
        else {
            hashCode5 = 0;
        }
        int hashCode6;
        if (this.loginURL != null) {
            hashCode6 = this.loginURL.hashCode();
        }
        else {
            hashCode6 = 0;
        }
        if (this.gridName != null) {
            hashCode = this.gridName.hashCode();
        }
        return (hashCode6 + (hashCode5 + (hashCode4 + (hashCode3 + hashCode2 * 31) * 31) * 31) * 31) * 31 + hashCode;
    }
    
    public SLAuthParams withLocation(final String s) {
        return new SLAuthParams(this.loginName, this.passwordHash, this.clientID, s, this.loginURL, this.gridName);
    }
}
