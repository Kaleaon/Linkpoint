package com.lumiyaviewer.lumiya.dao;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import java.util.UUID;

public class UserName {
    private String displayName;
    private boolean isBadUUID;
    private String userName;
    private UUID uuid;

    public UserName(UUID uuid) {
        this.uuid = uuid;
    }

    public UserName(UUID uuid, String str, String str2, boolean z) {
        this.uuid = uuid;
        this.userName = str;
        this.displayName = str2;
        this.isBadUUID = z;
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
        return !this.isBadUUID ? !Strings.isNullOrEmpty(this.userName) ? Strings.isNullOrEmpty(this.displayName) ^ 1 : false : true;
    }

    public boolean mergeWith(UserName userName) {
        boolean z = false;
        if (userName.isBadUUID && (this.isBadUUID ^ 1) != 0) {
            this.isBadUUID = true;
            return true;
        } else if (this.isBadUUID) {
            return false;
        } else {
            if (!(Strings.isNullOrEmpty(userName.userName) || (Objects.equal(this.userName, userName.userName) ^ 1) == 0)) {
                this.userName = userName.userName;
                z = true;
            }
            if (!(Strings.isNullOrEmpty(userName.displayName) || (Objects.equal(this.displayName, userName.displayName) ^ 1) == 0)) {
                this.displayName = userName.displayName;
                z = true;
            }
            return z;
        }
    }

    public void setDisplayName(String str) {
        this.displayName = str;
    }

    public void setIsBadUUID(boolean z) {
        this.isBadUUID = z;
    }

    public void setUserName(String str) {
        this.userName = str;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
