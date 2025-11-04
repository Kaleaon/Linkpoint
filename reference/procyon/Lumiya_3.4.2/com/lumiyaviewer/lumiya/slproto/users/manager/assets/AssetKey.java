// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager.assets;

import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import javax.annotation.Nullable;
import java.util.UUID;
import com.google.common.base.Joiner;

public abstract class AssetKey
{
    private static final Joiner toStringJoiner;
    
    static {
        toStringJoiner = Joiner.on(':').useForNull("null");
    }
    
    public static AssetKey createAssetKey(@Nullable final UUID uuid, final UUID uuid2, final UUID uuid3, final int n) {
        return new AutoValue_AssetKey(2, 2, uuid3, n, uuid2, uuid, null);
    }
    
    public static AssetKey createInventoryKey(final SLInventoryEntry slInventoryEntry, @Nullable final UUID uuid) {
        return new AutoValue_AssetKey(2, 3, slInventoryEntry.assetUUID, slInventoryEntry.assetType, slInventoryEntry.ownerUUID, slInventoryEntry.uuid, uuid);
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
    @Override
    public String toString() {
        return AssetKey.toStringJoiner.join(this.channelType(), this.sourceType(), this.assetUUID(), this.assetType(), this.ownerUUID(), this.itemUUID(), this.taskUUID());
    }
}
