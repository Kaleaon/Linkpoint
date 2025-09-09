package com.lumiyaviewer.lumiya.utils;

import com.google.common.base.Strings;
import java.util.UUID;
import javax.annotation.Nullable;

public class UUIDPool extends InternPool<UUID> {
    public static final UUID ZeroUUID = new UUID(0, 0);
    private static final UUIDPool instance = new UUIDPool();

    public static final UUIDPool getInstance() {
        return instance;
    }

    public static UUID getUUID(long j, long j2) {
        return (UUID) instance.intern(new UUID(j, j2));
    }

    @Nullable
    public static UUID getUUID(@Nullable String str) {
        if (!Strings.isNullOrEmpty(str)) {
            return (UUID) instance.intern(UUID.fromString(str));
        }
        return null;
    }

    public static UUID getUUID(UUID uuid) {
        return (UUID) instance.intern(uuid);
    }

    public static UUID setUUID(UUID uuid, long j, long j2) {
        return (uuid != null && uuid.getMostSignificantBits() == j && uuid.getLeastSignificantBits() == j2) ? uuid : (UUID) instance.intern(new UUID(j, j2));
    }
}
