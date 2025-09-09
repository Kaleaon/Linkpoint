package com.lumiyaviewer.lumiya.dao;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import java.util.UUID;

public class UserName {
    private String displayName;
    private boolean isBadUUID;
    private String userName;
    private UUID uuid;

    public UserName() {
    }

    public UserName(UUID uuid2) {
        this.uuid = uuid2;
    }

    public UserName(UUID uuid2, String str, String str2, boolean z) {
        this.uuid = uuid2;
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
        if (this.isBadUUID) {
            return true;
        }
        if (!Strings.isNullOrEmpty(this.userName)) {
            return !Strings.isNullOrEmpty(this.displayName);
        }
        return false;
    }

    public boolean mergeWith(UserName userName2) {
        boolean z = false;
        if (userName2.isBadUUID && (!this.isBadUUID)) {
            this.isBadUUID = true;
            return true;
        } else if (this.isBadUUID) {
            return false;
        } else {
            if (!Strings.isNullOrEmpty(userName2.userName) && (!Objects.equal(this.userName, userName2.userName))) {
                this.userName = userName2.userName;
                z = true;
            }
            if (Strings.isNullOrEmpty(userName2.displayName) || !(!Objects.equal(this.displayName, userName2.displayName))) {
                return z;
            }
            this.displayName = userName2.displayName;
            return true;
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

    public void setUuid(UUID uuid2) {
        this.uuid = uuid2;
    }
}
