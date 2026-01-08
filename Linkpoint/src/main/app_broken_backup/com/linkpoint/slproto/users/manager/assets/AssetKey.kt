package com.linkpoint.slproto.users.manager.assets

import com.google.common.base.Joiner
import com.linkpoint.slproto.inventory.SLInventoryEntry
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

abstract class AssetKey {
    private Joiner toStringJoiner = Joiner.on(':').useForNull("null")

    fun createAssetKey(@Nullable UUID uuid, UUID uuid2, UUID uuid3, Int i): AssetKey {
        return AutoValue_AssetKey(2, 2, uuid3, i, uuid2, uuid, (UUID) null)
    }

    fun createInventoryKey(SLInventoryEntry sLInventoryEntry, @Nullable UUID uuid): AssetKey {
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

    @NonNull
    fun toString(): String {
        return toStringJoiner.join(Int.valueOf(channelType()), Int.valueOf(sourceType()), assetUUID(), Int.valueOf(assetType()), ownerUUID(), itemUUID(), taskUUID())
    }
}
