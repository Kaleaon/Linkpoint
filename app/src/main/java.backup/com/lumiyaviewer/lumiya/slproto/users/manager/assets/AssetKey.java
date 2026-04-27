package com.lumiyaviewer.lumiya.slproto.users.manager.assets;

import com.google.common.base.Joiner;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AssetKey {
    private static final Joiner toStringJoiner = Joiner.on(':').useForNull("null");

    public static AssetKey createAssetKey(@Nullable UUID uuid, UUID uuid2, UUID uuid3, int i) {
        return new AutoValue_AssetKey(2, 2, uuid3, i, uuid2, uuid, (UUID) null);
    }

    public static AssetKey createInventoryKey(SLInventoryEntry sLInventoryEntry, @Nullable UUID uuid) {
        return new AutoValue_AssetKey(2, 3, sLInventoryEntry.assetUUID, sLInventoryEntry.assetType, sLInventoryEntry.ownerUUID, sLInventoryEntry.uuid, uuid);
    }

    public abstract int assetType();

    @Nullable
    public abstract UUID assetUUID();

    public abstract int channelType();

    @Nullable
    public abstract UUID itemUUID();

    @Nullable
    public abstract UUID ownerUUID();

    public abstract int sourceType();

    @Nullable
    public abstract UUID taskUUID();

    @Nonnull
    public String toString() {
        return toStringJoiner.join(Integer.valueOf(channelType()), Integer.valueOf(sourceType()), assetUUID(), Integer.valueOf(assetType()), ownerUUID(), itemUUID(), taskUUID());
    }
}
