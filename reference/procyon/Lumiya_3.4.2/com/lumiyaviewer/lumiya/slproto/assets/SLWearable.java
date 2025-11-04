// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.assets;

import com.lumiyaviewer.lumiya.Debug;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey;
import javax.annotation.Nonnull;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetData;
import com.lumiyaviewer.lumiya.react.Subscription;

public class SLWearable implements OnData<AssetData>, OnError
{
    @Nonnull
    public final UUID assetID;
    private final Subscription<AssetKey, AssetData> assetSubscription;
    private String inventoryName;
    private volatile boolean isFailed;
    @Nonnull
    public final UUID itemID;
    @Nullable
    private final OnWearableStatusChangeListener statusChangeListener;
    @Nullable
    private volatile SLWearableData wearableData;
    
    public SLWearable(@Nonnull final UserManager userManager, @Nullable final Executor executor, @Nonnull final UUID itemID, @Nonnull final UUID assetID, @Nonnull final SLWearableType slWearableType, @Nullable final OnWearableStatusChangeListener statusChangeListener) {
        this.itemID = itemID;
        this.assetID = assetID;
        this.isFailed = false;
        this.statusChangeListener = statusChangeListener;
        Debug.Printf("Wearable: subscribing for wearable %s", assetID);
        this.assetSubscription = userManager.getAssetResponseCacher().getPool().subscribe(AssetKey.createAssetKey(null, null, assetID, slWearableType.getAssetType().getTypeCode()), executor, this, this);
    }
    
    public void dispose() {
        Debug.Printf("Wearable: unsubscribing for wearable %s", this.assetID);
        this.assetSubscription.unsubscribe();
    }
    
    public boolean getIsFailed() {
        return this.isFailed;
    }
    
    public boolean getIsValid() {
        return this.wearableData != null;
    }
    
    public String getName() {
        if (this.inventoryName != null) {
            return this.inventoryName;
        }
        final SLWearableData wearableData = this.wearableData;
        if (wearableData != null) {
            return wearableData.name;
        }
        if (this.isFailed) {
            return "(Failed to load)";
        }
        return "(loading)";
    }
    
    @Nullable
    public SLWearableData getWearableData() {
        return this.wearableData;
    }
    
    public void onData(final AssetData assetData) {
        if (assetData != null) {
            if (assetData.getStatus() != 1 || assetData.getData() == null) {
                Debug.Printf("Wearable: asset transfer failed for asset %s", this.assetID);
                this.isFailed = true;
            }
            else {
                try {
                    this.wearableData = new SLWearableData(assetData.getData());
                    Debug.Printf("Wearable: retrieved wearable data for asset %s", this.assetID);
                    this.isFailed = false;
                }
                catch (final SLWearableData.WearableFormatException ex) {
                    Debug.Printf("Wearable: failed to parse wearable data for asset %s", this.assetID);
                    this.isFailed = true;
                }
            }
            if (this.statusChangeListener != null) {
                this.statusChangeListener.onWearableStatusChanged(this);
            }
        }
    }
    
    @Override
    public void onError(final Throwable t) {
        Debug.Printf("Wearable: got error for asset %s", this.assetID);
        this.isFailed = true;
        if (this.statusChangeListener != null) {
            this.statusChangeListener.onWearableStatusChanged(this);
        }
    }
    
    public void setInventoryName(final String inventoryName) {
        this.inventoryName = inventoryName;
    }
    
    public interface OnWearableStatusChangeListener
    {
        void onWearableStatusChanged(final SLWearable p0);
    }
}
