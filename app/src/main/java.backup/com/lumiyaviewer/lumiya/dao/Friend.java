package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class Friend {
    public static final int GRANT_MAP_LOCATION = 2;
    public static final int GRANT_MODIFY_OBJECTS = 4;
    public static final int GRANT_ONLINE_STATUS = 1;
    private boolean isOnline;
    private int rightsGiven;
    private int rightsHas;
    private UUID uuid;

    public Friend(UUID uuid) {
        this.uuid = uuid;
    }

    public Friend(UUID uuid, int i, int i2, boolean z) {
        this.uuid = uuid;
        this.rightsGiven = i;
        this.rightsHas = i2;
        this.isOnline = z;
    }

    public boolean getIsOnline() {
        return this.isOnline;
    }

    public int getRightsGiven() {
        return this.rightsGiven;
    }

    public int getRightsHas() {
        return this.rightsHas;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setIsOnline(boolean z) {
        this.isOnline = z;
    }

    public void setRightsGiven(int i) {
        this.rightsGiven = i;
    }

    public void setRightsHas(int i) {
        this.rightsHas = i;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
