package com.lumiyaviewer.lumiya.slproto.users.manager.assets;

import java.util.UUID;
import javax.annotation.Nullable;

final class AutoValue_AssetKey extends AssetKey {
    private final int assetType;
    private final UUID assetUUID;
    private final int channelType;
    private final UUID itemUUID;
    private final UUID ownerUUID;
    private final int sourceType;
    private final UUID taskUUID;

    AutoValue_AssetKey(int i, int i2, @Nullable UUID uuid, int i3, @Nullable UUID uuid2, @Nullable UUID uuid3, @Nullable UUID uuid4) {
        this.channelType = i;
        this.sourceType = i2;
        this.assetUUID = uuid;
        this.assetType = i3;
        this.ownerUUID = uuid2;
        this.itemUUID = uuid3;
        this.taskUUID = uuid4;
    }

    public int assetType() {
        return this.assetType;
    }

    @Nullable
    public UUID assetUUID() {
        return this.assetUUID;
    }

    public int channelType() {
        return this.channelType;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AssetKey)) {
            return false;
        }
        AssetKey assetKey = (AssetKey) obj;
        if (this.channelType != assetKey.channelType() || this.sourceType != assetKey.sourceType() || (this.assetUUID != null ? !this.assetUUID.equals(assetKey.assetUUID()) : assetKey.assetUUID() != null) || this.assetType != assetKey.assetType() || (this.ownerUUID != null ? !this.ownerUUID.equals(assetKey.ownerUUID()) : assetKey.ownerUUID() != null) || (this.itemUUID != null ? !this.itemUUID.equals(assetKey.itemUUID()) : assetKey.itemUUID() != null)) {
            return false;
        }
        return this.taskUUID == null ? assetKey.taskUUID() == null : this.taskUUID.equals(assetKey.taskUUID());
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((this.itemUUID == null ? 0 : this.itemUUID.hashCode()) ^ (((this.ownerUUID == null ? 0 : this.ownerUUID.hashCode()) ^ (((((this.assetUUID == null ? 0 : this.assetUUID.hashCode()) ^ ((((this.channelType ^ 1000003) * 1000003) ^ this.sourceType) * 1000003)) * 1000003) ^ this.assetType) * 1000003)) * 1000003)) * 1000003;
        if (this.taskUUID != null) {
            i = this.taskUUID.hashCode();
        }
        return hashCode ^ i;
    }

    @Nullable
    public UUID itemUUID() {
        return this.itemUUID;
    }

    @Nullable
    public UUID ownerUUID() {
        return this.ownerUUID;
    }

    public int sourceType() {
        return this.sourceType;
    }

    @Nullable
    public UUID taskUUID() {
        return this.taskUUID;
    }
}
