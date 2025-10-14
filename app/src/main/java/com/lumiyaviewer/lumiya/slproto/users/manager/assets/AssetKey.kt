package com.lumiyaviewer.lumiya.slproto.users.manager.assets

import com.google.common.base.Joiner
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class AssetKey {
    private Joiner toStringJoiner = Joiner.on(':').useForNull("null")

    AssetKey createAssetKey(@Nullable UUID uuid, UUID uuid2, UUID uuid3, Int i) {
        return AutoValue_AssetKey(2, 2, uuid3, i, uuid2, uuid, (UUID) null)
    }

    AssetKey createInventoryKey(SLInventoryEntry sLInventoryEntry, @Nullable UUID uuid) {
        return AutoValue_AssetKey(2, 3, sLInventoryEntry.assetUUID, sLInventoryEntry.assetType, sLInventoryEntry.ownerUUID, sLInventoryEntry.uuid, uuid)
    }

    abstract Int assetType()

    @Nullable
    abstract UUID assetUUID()

    abstract Int channelType()

    @Nullable
    abstract UUID itemUUID()

    @Nullable
    abstract UUID ownerUUID()

    abstract Int sourceType()

    @Nullable
    abstract UUID taskUUID()

    @Nonnull
    String toString() {
        return toStringJoiner.join(Int.valueOf(channelType()), Int.valueOf(sourceType()), assetUUID(), Int.valueOf(assetType()), ownerUUID(), itemUUID(), taskUUID())
    }
}
