// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils;

import com.google.common.base.Strings;
import javax.annotation.Nullable;
import java.util.UUID;

public class UUIDPool extends InternPool<UUID>
{
    public static final UUID ZeroUUID;
    private static final UUIDPool instance;
    
    static {
        instance = new UUIDPool();
        ZeroUUID = new UUID(0L, 0L);
    }
    
    public static final UUIDPool getInstance() {
        return UUIDPool.instance;
    }
    
    public static UUID getUUID(final long mostSigBits, final long leastSigBits) {
        return UUIDPool.instance.intern(new UUID(mostSigBits, leastSigBits));
    }
    
    @Nullable
    public static UUID getUUID(@Nullable final String name) {
        if (!Strings.isNullOrEmpty(name)) {
            return UUIDPool.instance.intern(UUID.fromString(name));
        }
        return null;
    }
    
    public static UUID getUUID(final UUID uuid) {
        return UUIDPool.instance.intern(uuid);
    }
    
    public static UUID setUUID(final UUID uuid, final long mostSigBits, final long leastSigBits) {
        if (uuid != null && uuid.getMostSignificantBits() == mostSigBits && uuid.getLeastSignificantBits() == leastSigBits) {
            return uuid;
        }
        return UUIDPool.instance.intern(new UUID(mostSigBits, leastSigBits));
    }
}
