package com.linkpoint.slproto.users.manager.assets

import com.google.common.base.Joiner
import com.linkpoint.slproto.inventory.SLInventoryEntry
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class AssetKey {
    private const val Joiner toStringJoiner = Joiner.on(':').useForNull("null")

    @JvmStatic
     fun createAssetKey(uuid: UUID, uuid2: UUID, uuid3: UUID, i: Int): AssetKey {
        return AutoValue_AssetKey(2, 2, uuid3, i, uuid2, uuid, (UUID) null)
    }

    @JvmStatic
     fun createInventoryKey(sLInventoryEntry: SLInventoryEntry, uuid: UUID): AssetKey {
        return AutoValue_AssetKey(2, 3, sLInventoryEntry.assetUUID, sLInventoryEntry.assetType, sLInventoryEntry.ownerUUID, sLInventoryEntry.uuid, uuid)
    }

    public abstract Int assetType()

    public abstract UUID assetUUID()

    public abstract Int channelType()

    public abstract UUID itemUUID()

    public abstract UUID ownerUUID()

    public abstract Int sourceType()

    public abstract UUID taskUUID()

     public fun toString(): String {
        return toStringJoiner.join(Integer.valueOf(channelType()), Integer.valueOf(sourceType()), assetUUID(), Integer.valueOf(assetType()), ownerUUID(), itemUUID(), taskUUID())
    }
}
