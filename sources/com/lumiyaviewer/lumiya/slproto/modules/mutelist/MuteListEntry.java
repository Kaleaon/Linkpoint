package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
import javax.annotation.concurrent.Immutable;

@Immutable
public class MuteListEntry {
    public static final int flagAll = 15;
    public static final int flagObjectSounds = 8;
    public static final int flagParticles = 4;
    public static final int flagTextChat = 1;
    public static final int flagVoiceChat = 2;
    public final int flags;
    public final String name;
    public final MuteType type;
    public final UUID uuid;

    public MuteListEntry(MuteType muteType, UUID uuid2, String str, int i) {
        this.type = muteType;
        this.uuid = UUIDPool.getUUID(uuid2);
        this.name = str;
        this.flags = i;
    }
}
