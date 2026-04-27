// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.WeakHashMap;
import java.util.UUID;
import java.util.Map;

public class GridConnectionManager
{
    private static final Map<UUID, SLGridConnection> connections;
    private static final Object lock;
    
    static {
        lock = new Object();
        connections = new WeakHashMap<UUID, SLGridConnection>();
    }
    
    @Nullable
    public static SLGridConnection getConnection(@Nullable final UUID uuid) {
        if (uuid == null) {
            return null;
        }
        synchronized (GridConnectionManager.lock) {
            return GridConnectionManager.connections.get(uuid);
        }
    }
    
    public static void removeConnection(@Nonnull final UUID uuid, final SLGridConnection slGridConnection) {
        synchronized (GridConnectionManager.lock) {
            if (GridConnectionManager.connections.get(uuid) == slGridConnection) {
                GridConnectionManager.connections.remove(uuid);
            }
        }
    }
    
    public static void setConnection(@Nonnull final UUID uuid, final SLGridConnection slGridConnection) {
        synchronized (GridConnectionManager.lock) {
            GridConnectionManager.connections.put(uuid, slGridConnection);
        }
    }
}
