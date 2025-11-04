package com.lumiyaviewer.lumiya.utils;

import com.google.common.base.Strings;
import java.util.UUID;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class UUIDPool extends InternPool<UUID> {
    private static final UUIDPool instance = new UUIDPool();
    public static final UUID ZeroUUID = new UUID(0, 0);

    public static final UUIDPool getInstance() {
        return instance;
    }

    public static UUID getUUID(long j, long j2) {
        return instance.intern(new UUID(j, j2));
    }

    @Nullable
    public static UUID getUUID(@Nullable String str) {
        if (Strings.isNullOrEmpty(str)) {
            return null;
        }
        return instance.intern(UUID.fromString(str));
    }

    public static UUID getUUID(UUID uuid) {
        return instance.intern(uuid);
    }

    public static UUID setUUID(UUID uuid, long j, long j2) {
        return (uuid != null && uuid.getMostSignificantBits() == j && uuid.getLeastSignificantBits() == j2) ? uuid : instance.intern(new UUID(j, j2));
    }
}
