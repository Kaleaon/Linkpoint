// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager.assets;

import javax.annotation.Nullable;
import java.util.UUID;

final class AutoValue_AssetKey extends AssetKey
{
    private final int assetType;
    private final UUID assetUUID;
    private final int channelType;
    private final UUID itemUUID;
    private final UUID ownerUUID;
    private final int sourceType;
    private final UUID taskUUID;
    
    AutoValue_AssetKey(final int channelType, final int sourceType, @Nullable final UUID assetUUID, final int assetType, @Nullable final UUID ownerUUID, @Nullable final UUID itemUUID, @Nullable final UUID taskUUID) {
        this.channelType = channelType;
        this.sourceType = sourceType;
        this.assetUUID = assetUUID;
        this.assetType = assetType;
        this.ownerUUID = ownerUUID;
        this.itemUUID = itemUUID;
        this.taskUUID = taskUUID;
    }
    
    @Override
    public int assetType() {
        return this.assetType;
    }
    
    @Nullable
    @Override
    public UUID assetUUID() {
        return this.assetUUID;
    }
    
    @Override
    public int channelType() {
        return this.channelType;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean equals = true;
        if (o == this) {
            return true;
        }
        if (o instanceof AssetKey) {
            final AssetKey assetKey = (AssetKey)o;
            if (this.channelType != assetKey.channelType() || this.sourceType != assetKey.sourceType()) {
                return false;
            }
            if (this.assetUUID == null) {
                if (assetKey.assetUUID() != null) {
                    return false;
                }
            }
            else if (!this.assetUUID.equals(assetKey.assetUUID())) {
                return false;
            }
            if (this.assetType != assetKey.assetType()) {
                return false;
            }
            if (this.ownerUUID == null) {
                if (assetKey.ownerUUID() != null) {
                    return false;
                }
            }
            else if (!this.ownerUUID.equals(assetKey.ownerUUID())) {
                return false;
            }
            if (this.itemUUID == null) {
                if (assetKey.itemUUID() != null) {
                    return false;
                }
            }
            else if (!this.itemUUID.equals(assetKey.itemUUID())) {
                return false;
            }
            if (this.taskUUID == null) {
                if (assetKey.taskUUID() != null) {
                    equals = false;
                }
            }
            else {
                equals = this.taskUUID.equals(assetKey.taskUUID());
            }
            return equals;
            equals = false;
            return equals;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        final int channelType = this.channelType;
        final int sourceType = this.sourceType;
        int hashCode2;
        if (this.assetUUID == null) {
            hashCode2 = 0;
        }
        else {
            hashCode2 = this.assetUUID.hashCode();
        }
        final int assetType = this.assetType;
        int hashCode3;
        if (this.ownerUUID == null) {
            hashCode3 = 0;
        }
        else {
            hashCode3 = this.ownerUUID.hashCode();
        }
        int hashCode4;
        if (this.itemUUID == null) {
            hashCode4 = 0;
        }
        else {
            hashCode4 = this.itemUUID.hashCode();
        }
        if (this.taskUUID != null) {
            hashCode = this.taskUUID.hashCode();
        }
        return (hashCode4 ^ (hashCode3 ^ ((hashCode2 ^ ((channelType ^ 0xF4243) * 1000003 ^ sourceType) * 1000003) * 1000003 ^ assetType) * 1000003) * 1000003) * 1000003 ^ hashCode;
    }
    
    @Nullable
    @Override
    public UUID itemUUID() {
        return this.itemUUID;
    }
    
    @Nullable
    @Override
    public UUID ownerUUID() {
        return this.ownerUUID;
    }
    
    @Override
    public int sourceType() {
        return this.sourceType;
    }
    
    @Nullable
    @Override
    public UUID taskUUID() {
        return this.taskUUID;
    }
}
