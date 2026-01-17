package com.lumiyaviewer.lumiya.slproto.users.manager.assets;

import java.util.UUID;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
final class AutoValue_AssetKey extends AssetKey {
    private final int assetType;
    private final UUID assetUUID;
    private final int channelType;
    private final UUID itemUUID;
    private final UUID ownerUUID;
    private final int sourceType;
    private final UUID taskUUID;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_AssetKey(int i, int i2, @Nullable UUID uuid, int i3, @Nullable UUID uuid2, @Nullable UUID uuid3, @Nullable UUID uuid4) {
        this.channelType = i;
        this.sourceType = i2;
        this.assetUUID = uuid;
        this.assetType = i3;
        this.ownerUUID = uuid2;
        this.itemUUID = uuid3;
        this.taskUUID = uuid4;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey
    public int assetType() {
        return this.assetType;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey
    @Nullable
    public UUID assetUUID() {
        return this.assetUUID;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey
    public int channelType() {
        return this.channelType;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AssetKey) {
            AssetKey assetKey = (AssetKey) obj;
            if (this.channelType != assetKey.channelType() || this.sourceType != assetKey.sourceType() || (this.assetUUID != null ? !this.assetUUID.equals(assetKey.assetUUID()) : assetKey.assetUUID() != null) || this.assetType != assetKey.assetType() || (this.ownerUUID != null ? !this.ownerUUID.equals(assetKey.ownerUUID()) : assetKey.ownerUUID() != null) || (this.itemUUID != null ? !this.itemUUID.equals(assetKey.itemUUID()) : assetKey.itemUUID() != null)) {
                return false;
            }
            return this.taskUUID == null ? assetKey.taskUUID() == null : this.taskUUID.equals(assetKey.taskUUID());
        }
        return false;
    }

    public int hashCode() {
        return (((this.itemUUID == null ? 0 : this.itemUUID.hashCode()) ^ (((this.ownerUUID == null ? 0 : this.ownerUUID.hashCode()) ^ (((((this.assetUUID == null ? 0 : this.assetUUID.hashCode()) ^ ((((this.channelType ^ 1000003) * 1000003) ^ this.sourceType) * 1000003)) * 1000003) ^ this.assetType) * 1000003)) * 1000003)) * 1000003) ^ (this.taskUUID != null ? this.taskUUID.hashCode() : 0);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey
    @Nullable
    public UUID itemUUID() {
        return this.itemUUID;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey
    @Nullable
    public UUID ownerUUID() {
        return this.ownerUUID;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey
    public int sourceType() {
        return this.sourceType;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey
    @Nullable
    public UUID taskUUID() {
        return this.taskUUID;
    }
}
