package com.linkpoint.slproto.users.manager.assets

import java.util.UUID
import javax.annotation.Nullable

final class AutoValue_AssetKey : AssetKey() {
    private val Int assetType
    private val UUID assetUUID
    private val Int channelType
    private val UUID itemUUID
    private val UUID ownerUUID
    private val Int sourceType
    private val UUID taskUUID

    AutoValue_AssetKey(Int i, Int i2, UUID uuid, Int i3, UUID uuid2, UUID uuid3, UUID uuid4) {
        this.channelType = i
        this.sourceType = i2
        this.assetUUID = uuid
        this.assetType = i3
        this.ownerUUID = uuid2
        this.itemUUID = uuid3
        this.taskUUID = uuid4
    }

     public fun assetType(): Int {
        return this.assetType
    }

     public fun assetUUID(): UUID {
        return this.assetUUID
    }

     public fun channelType(): Int {
        return this.channelType
    }

     public fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof AssetKey)) {
            return false
        }
        val assetKey: AssetKey = (AssetKey) obj
        if (this.channelType != assetKey.channelType() || this.sourceType != assetKey.sourceType() || (this.assetUUID != null ? !this.assetUUID.equals(assetKey.assetUUID()) : assetKey.assetUUID() != null) || this.assetType != assetKey.assetType() || (this.ownerUUID != null ? !this.ownerUUID.equals(assetKey.ownerUUID()) : assetKey.ownerUUID() != null) || (this.itemUUID != null ? !this.itemUUID.equals(assetKey.itemUUID()) : assetKey.itemUUID() != null)) {
            return false
        }
        return this.taskUUID == null ? assetKey.taskUUID() == null : this.taskUUID.equals(assetKey.taskUUID())
    }

     public fun hashCode(): Int {
        val i: Int = 0
        val hashCode: Int = ((this.itemUUID == null ? 0 : this.itemUUID.hashCode()) ^ (((this.ownerUUID == null ? 0 : this.ownerUUID.hashCode()) ^ (((((this.assetUUID == null ? 0 : this.assetUUID.hashCode()) ^ ((((this.channelType ^ 1000003) * 1000003) ^ this.sourceType) * 1000003)) * 1000003) ^ this.assetType) * 1000003)) * 1000003)) * 1000003
        if (this.taskUUID != null) {
            i = this.taskUUID.hashCode()
        }
        return hashCode ^ i
    }

     public fun itemUUID(): UUID {
        return this.itemUUID
    }

     public fun ownerUUID(): UUID {
        return this.ownerUUID
    }

     public fun sourceType(): Int {
        return this.sourceType
    }

     public fun taskUUID(): UUID {
        return this.taskUUID
    }
}
