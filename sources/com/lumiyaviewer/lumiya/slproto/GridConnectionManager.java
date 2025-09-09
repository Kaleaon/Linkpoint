package com.lumiyaviewer.lumiya.slproto;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GridConnectionManager {
    private static final Map<UUID, SLGridConnection> connections = new WeakHashMap();
    private static final Object lock = new Object();

    @Nullable
    public static SLGridConnection getConnection(@Nullable UUID uuid) {
        SLGridConnection sLGridConnection;
        if (uuid == null) {
            return null;
        }
        synchronized (lock) {
            sLGridConnection = connections.get(uuid);
        }
        return sLGridConnection;
    }

    public static void removeConnection(@Nonnull UUID uuid, SLGridConnection sLGridConnection) {
        synchronized (lock) {
            if (connections.get(uuid) == sLGridConnection) {
                connections.remove(uuid);
            }
        }
    }

    public static void setConnection(@Nonnull UUID uuid, SLGridConnection sLGridConnection) {
        synchronized (lock) {
            connections.put(uuid, sLGridConnection);
        }
    }
}
