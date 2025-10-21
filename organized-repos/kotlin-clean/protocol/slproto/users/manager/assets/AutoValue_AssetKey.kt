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

    public Int assetType() {
        return this.assetType
    }

    public UUID assetUUID() {
        return this.assetUUID
    }

    public Int channelType() {
        return this.channelType
    }

    public Boolean equals(Object obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof AssetKey)) {
            return false
        }
        AssetKey assetKey = (AssetKey) obj
        if (this.channelType != assetKey.channelType() || this.sourceType != assetKey.sourceType() || (this.assetUUID != null ? !this.assetUUID.equals(assetKey.assetUUID()) : assetKey.assetUUID() != null) || this.assetType != assetKey.assetType() || (this.ownerUUID != null ? !this.ownerUUID.equals(assetKey.ownerUUID()) : assetKey.ownerUUID() != null) || (this.itemUUID != null ? !this.itemUUID.equals(assetKey.itemUUID()) : assetKey.itemUUID() != null)) {
            return false
        }
        return this.taskUUID == null ? assetKey.taskUUID() == null : this.taskUUID.equals(assetKey.taskUUID())
    }

    public Int hashCode() {
        Int i = 0
        Int hashCode = ((this.itemUUID == null ? 0 : this.itemUUID.hashCode()) ^ (((this.ownerUUID == null ? 0 : this.ownerUUID.hashCode()) ^ (((((this.assetUUID == null ? 0 : this.assetUUID.hashCode()) ^ ((((this.channelType ^ 1000003) * 1000003) ^ this.sourceType) * 1000003)) * 1000003) ^ this.assetType) * 1000003)) * 1000003)) * 1000003
        if (this.taskUUID != null) {
            i = this.taskUUID.hashCode()
        }
        return hashCode ^ i
    }

    public UUID itemUUID() {
        return this.itemUUID
    }

    public UUID ownerUUID() {
        return this.ownerUUID
    }

    public Int sourceType() {
        return this.sourceType
    }

    public UUID taskUUID() {
        return this.taskUUID
    }
}
