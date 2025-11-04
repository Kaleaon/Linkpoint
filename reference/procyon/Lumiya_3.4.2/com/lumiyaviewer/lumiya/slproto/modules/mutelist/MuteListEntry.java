// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
import javax.annotation.concurrent.Immutable;

@Immutable
public class MuteListEntry
{
    public static final int flagAll = 15;
    public static final int flagObjectSounds = 8;
    public static final int flagParticles = 4;
    public static final int flagTextChat = 1;
    public static final int flagVoiceChat = 2;
    public final int flags;
    public final String name;
    public final MuteType type;
    public final UUID uuid;
    
    public MuteListEntry(final MuteType type, final UUID uuid, final String name, final int flags) {
        this.type = type;
        this.uuid = UUIDPool.getUUID(uuid);
        this.name = name;
        this.flags = flags;
    }
}
