package com.lumiyaviewer.lumiya.slproto.assets

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.react.Subscription
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableData
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetData
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey
import java.util.UUID
import java.util.concurrent.Executor
import javax.annotation.Nonnull
import javax.annotation.Nullable

class SLWearable implements Subscription.OnData<AssetData>, Subscription.OnError {
    @Nonnull
    UUID assetID
    private Subscription<AssetKey, AssetData> assetSubscription
    private String inventoryName
    private volatile boolean isFailed = false
    @Nonnull
    UUID itemID
    @Nullable
    private OnWearableStatusChangeListener statusChangeListener
    @Nullable
    private volatile SLWearableData wearableData

    interface OnWearableStatusChangeListener {
        fun onWearableStatusChanged(sLWearable: SLWearable): Unit
    }

    SLWearable(@Nonnull UserManager userManager, @Nullable Executor executor, @Nonnull UUID uuid, @Nonnull UUID uuid2, @Nonnull SLWearableType sLWearableType, @Nullable OnWearableStatusChangeListener onWearableStatusChangeListener) {
        this.itemID = uuid
        this.assetID = uuid2
        this.statusChangeListener = onWearableStatusChangeListener
        Debug.Printf("Wearable: subscribing for wearable %s", uuid2)
        this.assetSubscription = userManager.getAssetResponseCacher().getPool().subscribe(AssetKey.createAssetKey((UUID) null, (UUID) null, uuid2, sLWearableType.getAssetType().getTypeCode()), executor, this, this)
    }

    void dispose() {
        Debug.Printf("Wearable: unsubscribing for wearable %s", this.assetID)
        this.assetSubscription.unsubscribe()
    }

    boolean getIsFailed() {
        return this.isFailed
    }

    boolean getIsValid() {
        return this.wearableData != null
    }

    String getName() {
        if (this.inventoryName != null) {
            return this.inventoryName
        }
        SLWearableData sLWearableData = this.wearableData
        return sLWearableData != null ? sLWearableData.name : this.isFailed ? "(Failed to load)" : "(loading)"
    }

    @Nullable
    SLWearableData getWearableData() {
        return this.wearableData
    }

    void onData(AssetData assetData) {
        if (assetData != null) {
            if (assetData.getStatus() != 1 || assetData.getData() == null) {
                Debug.Printf("Wearable: asset transfer failed for asset %s", this.assetID)
                this.isFailed = true
            } else {
                try {
                    this.wearableData = SLWearableData(assetData.getData())
                    Debug.Printf("Wearable: retrieved wearable data for asset %s", this.assetID)
                    this.isFailed = false
                } catch (SLWearableData.WearableFormatException e) {
                    Debug.Printf("Wearable: failed to parse wearable data for asset %s", this.assetID)
                    this.isFailed = true
                }
            }
            if (this.statusChangeListener != null) {
                this.statusChangeListener.onWearableStatusChanged(this)
            }
        }
    }

    void onError(Throwable th) {
        Debug.Printf("Wearable: got error for asset %s", this.assetID)
        this.isFailed = true
        if (this.statusChangeListener != null) {
            this.statusChangeListener.onWearableStatusChanged(this)
        }
    }

    void setInventoryName(String str) {
        this.inventoryName = str
    }
}
