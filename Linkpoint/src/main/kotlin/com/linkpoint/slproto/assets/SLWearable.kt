package com.linkpoint.slproto.assets

import com.linkpoint.Debug
import com.linkpoint.react.Subscription
import com.linkpoint.slproto.assets.SLWearableData
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.slproto.users.manager.assets.AssetData
import com.linkpoint.slproto.users.manager.assets.AssetKey
import java.util.UUID
import java.util.concurrent.Executor
import javax.annotation.Nonnull
import javax.annotation.Nullable

class SLWearable : Subscription.OnData<AssetData>, Subscription.OnError {
    val UUID assetID
    private val Subscription<AssetKey, AssetData> assetSubscription
    private String inventoryName
    private volatile Boolean isFailed = false
    val UUID itemID
    private val OnWearableStatusChangeListener statusChangeListener
    private volatile SLWearableData wearableData

    interface OnWearableStatusChangeListener {
         fun onWearableStatusChanged(sLWearable: SLWearable)
    }

    public SLWearable(UserManager userManager, Executor executor, UUID uuid, UUID uuid2, SLWearableType sLWearableType, OnWearableStatusChangeListener onWearableStatusChangeListener) {
        this.itemID = uuid
        this.assetID = uuid2
        this.statusChangeListener = onWearableStatusChangeListener
        Debug.Printf("Wearable: subscribing for wearable %s", uuid2)
        this.assetSubscription = userManager.getAssetResponseCacher().getPool().subscribe(AssetKey.createAssetKey((UUID) null, (UUID) null, uuid2, sLWearableType.getAssetType().getTypeCode()), executor, this, this)
    }

    fun dispose() {
        Debug.Printf("Wearable: unsubscribing for wearable %s", this.assetID)
        this.assetSubscription.unsubscribe()
    }

     public fun getIsFailed(): Boolean {
        return this.isFailed
    }

     public fun getIsValid(): Boolean {
        return this.wearableData != null
    }

     public fun getName(): String {
        if (this.inventoryName != null) {
            return this.inventoryName
        }
        val sLWearableData: SLWearableData = this.wearableData
        return sLWearableData != null ? sLWearableData.name : this.isFailed ? "(Failed to load)" : "(loading)"
    }

     public fun getWearableData(): SLWearableData {
        return this.wearableData
    }

    fun onData(assetData: AssetData) {
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

    fun onError(th: Throwable) {
        Debug.Printf("Wearable: got error for asset %s", this.assetID)
        this.isFailed = true
        if (this.statusChangeListener != null) {
            this.statusChangeListener.onWearableStatusChanged(this)
        }
    }

    fun setInventoryName(str: String) {
        this.inventoryName = str
    }
}
