package com.linkpoint.slproto.users.manager.assets

import java.util.UUID
import androidx.annotation.Nullable

class AutoValue_AssetKey : AssetKey {
    private Int assetType
    private UUID assetUUID
    private Int channelType
    private UUID itemUUID
    private UUID ownerUUID
    private Int sourceType
    private UUID taskUUID

    AutoValue_AssetKey(Int i, Int i2, @Nullable UUID uuid, Int i3, @Nullable UUID uuid2, @Nullable UUID uuid3, @Nullable UUID uuid4) {
        this.channelType = i
        this.sourceType = i2
        this.assetUUID = uuid
        this.assetType = i3
        this.ownerUUID = uuid2
        this.itemUUID = uuid3
        this.taskUUID = uuid4
    }

    fun assetType(): Int {
        return this.assetType
    }

    @Nullable
    fun assetUUID(): UUID {
        return this.assetUUID
    }

    fun channelType(): Int {
        return this.channelType
    }

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj is AssetKey)) {
            return false
        }
        AssetKey assetKey = (AssetKey) obj
        if (this.channelType != assetKey.channelType() || this.sourceType != assetKey.sourceType() || (this.assetUUID != null ? !this.assetUUID.equals(assetKey.assetUUID()) : assetKey.assetUUID() != null) || this.assetType != assetKey.assetType() || (this.ownerUUID != null ? !this.ownerUUID.equals(assetKey.ownerUUID()) : assetKey.ownerUUID() != null) || (this.itemUUID != null ? !this.itemUUID.equals(assetKey.itemUUID()) : assetKey.itemUUID() != null)) {
            return false
        }
        return this.taskUUID == null ? assetKey.taskUUID() == null : this.taskUUID.equals(assetKey.taskUUID())
    }

    fun hashCode(): Int {
        var i: Int = 0
        var hashCode: Int = ((this.itemUUID == null ? 0 : this.itemUUID.hashCode()) ^ (((this.ownerUUID == null ? 0 : this.ownerUUID.hashCode()) ^ (((((this.assetUUID == null ? 0 : this.assetUUID.hashCode()) ^ ((((this.channelType ^ 1000003) * 1000003) ^ this.sourceType) * 1000003)) * 1000003) ^ this.assetType) * 1000003)) * 1000003)) * 1000003
        if (this.taskUUID != null) {
            i = this.taskUUID.hashCode()
        }
        return hashCode ^ i
    }

    @Nullable
    fun itemUUID(): UUID {
        return this.itemUUID
    }

    @Nullable
    fun ownerUUID(): UUID {
        return this.ownerUUID
    }

    fun sourceType(): Int {
        return this.sourceType
    }

    @Nullable
    fun taskUUID(): UUID {
        return this.taskUUID
    }
}
