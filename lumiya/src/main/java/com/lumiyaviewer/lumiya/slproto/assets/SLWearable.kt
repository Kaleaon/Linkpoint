package com.lumiyaviewer.lumiya.slproto.assets

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.react.Subscription
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetData
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey
import java.util.UUID
import java.util.concurrent.Executor

class SLWearable(
    userManager: UserManager,
    executor: Executor?,
    itemId: UUID,
    assetId: UUID,
    wearableType: SLWearableType,
    statusChangeListener: OnWearableStatusChangeListener?
) : Subscription.OnData<AssetData>, Subscription.OnError {
    
    val itemID: UUID = itemId
    val assetID: UUID = assetId
    private var assetSubscription: Subscription<AssetKey, AssetData>? = null
    private var inventoryName: String? = null
    @Volatile private var isFailed = false
    private var statusChangeListener: OnWearableStatusChangeListener? = statusChangeListener
    @Volatile private var wearableData: SLWearableData? = null

    interface OnWearableStatusChangeListener {
        fun onWearableStatusChanged(sLWearable: SLWearable)
    }

    init {
        Debug.Printf("Wearable: subscribing for wearable %s", assetId)
        assetSubscription = userManager.getAssetResponseCacher().getPool().subscribe(
            AssetKey.createAssetKey(null, null, assetId, wearableType.getAssetType().getTypeCode()),
            executor,
            this,
            this
        )
    }

    fun dispose() {
        Debug.Printf("Wearable: unsubscribing for wearable %s", assetID)
        assetSubscription?.unsubscribe()
    }

    fun getIsFailed(): Boolean = isFailed

    fun getIsValid(): Boolean = wearableData != null

    fun getName(): String {
        inventoryName?.let { return it }
        wearableData?.let { return it.name }
        return if (isFailed) "(Failed to load)" else "(loading)"
    }

    fun getWearableData(): SLWearableData? = wearableData

    override fun onData(assetData: AssetData?) {
        assetData?.let { data ->
            if (data.getStatus() != 1 || data.getData() == null) {
                Debug.Printf("Wearable: asset transfer failed for asset %s", assetID)
                isFailed = true
            } else {
                try {
                    wearableData = SLWearableData(data.getData()!!)
                    Debug.Printf("Wearable: retrieved wearable data for asset %s", assetID)
                    isFailed = false
                } catch (e: SLWearableData.WearableFormatException) {
                    Debug.Printf("Wearable: failed to parse wearable data for asset %s", assetID)
                    isFailed = true
                }
            }
            statusChangeListener?.onWearableStatusChanged(this)
        }
    }

    override fun onError(th: Throwable) {
        Debug.Printf("Wearable: got error for asset %s", assetID)
        isFailed = true
        statusChangeListener?.onWearableStatusChanged(this)
    }

    fun setInventoryName(name: String) {
        inventoryName = name
    }
}
