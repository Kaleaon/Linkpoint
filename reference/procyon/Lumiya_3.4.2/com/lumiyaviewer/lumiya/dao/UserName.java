// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import java.util.UUID;

public class UserName
{
    private String displayName;
    private boolean isBadUUID;
    private String userName;
    private UUID uuid;
    
    public UserName() {
    }
    
    public UserName(final UUID uuid) {
        this.uuid = uuid;
    }
    
    public UserName(final UUID uuid, final String userName, final String displayName, final boolean isBadUUID) {
        this.uuid = uuid;
        this.userName = userName;
        this.displayName = displayName;
        this.isBadUUID = isBadUUID;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public boolean getIsBadUUID() {
        return this.isBadUUID;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public UUID getUuid() {
        return this.uuid;
    }
    
    public boolean isComplete() {
        return this.isBadUUID || (!Strings.isNullOrEmpty(this.userName) && (Strings.isNullOrEmpty(this.displayName) ^ true));
    }
    
    public boolean mergeWith(final UserName userName) {
        final boolean b = false;
        if (userName.isBadUUID && (this.isBadUUID ^ true)) {
            return this.isBadUUID = true;
        }
        if (this.isBadUUID) {
            return false;
        }
        boolean b2 = b;
        if (!Strings.isNullOrEmpty(userName.userName)) {
            b2 = b;
            if (Objects.equal(this.userName, userName.userName) ^ true) {
                this.userName = userName.userName;
                b2 = true;
            }
        }
        boolean b3 = b2;
        if (!Strings.isNullOrEmpty(userName.displayName)) {
            b3 = b2;
            if (Objects.equal(this.displayName, userName.displayName) ^ true) {
                this.displayName = userName.displayName;
                b3 = true;
            }
        }
        return b3;
    }
    
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
    
    public void setIsBadUUID(final boolean isBadUUID) {
        this.isBadUUID = isBadUUID;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public void setUuid(final UUID uuid) {
        this.uuid = uuid;
    }
}
