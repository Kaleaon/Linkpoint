// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class Friend
{
    public static final int GRANT_MAP_LOCATION = 2;
    public static final int GRANT_MODIFY_OBJECTS = 4;
    public static final int GRANT_ONLINE_STATUS = 1;
    private boolean isOnline;
    private int rightsGiven;
    private int rightsHas;
    private UUID uuid;
    
    public Friend() {
    }
    
    public Friend(final UUID uuid) {
        this.uuid = uuid;
    }
    
    public Friend(final UUID uuid, final int rightsGiven, final int rightsHas, final boolean isOnline) {
        this.uuid = uuid;
        this.rightsGiven = rightsGiven;
        this.rightsHas = rightsHas;
        this.isOnline = isOnline;
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
    
    public void setIsOnline(final boolean isOnline) {
        this.isOnline = isOnline;
    }
    
    public void setRightsGiven(final int rightsGiven) {
        this.rightsGiven = rightsGiven;
    }
    
    public void setRightsHas(final int rightsHas) {
        this.rightsHas = rightsHas;
    }
    
    public void setUuid(final UUID uuid) {
        this.uuid = uuid;
    }
}
