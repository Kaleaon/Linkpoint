// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import javax.annotation.Nullable;
import android.view.View;
import java.lang.ref.WeakReference;
import com.lumiyaviewer.lumiya.react.Subscription;
import android.graphics.Bitmap;
import java.util.concurrent.atomic.AtomicReference;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;

public class ChatterThumbnailData implements ResourceConsumer
{
    private final AtomicReference<Bitmap> bitmapData;
    private final Subscription subscription;
    @Nullable
    private final WeakReference<View> updateView;
    @Nullable
    private final UserManager userManager;
    
    public ChatterThumbnailData(@Nonnull final ChatterID chatterID, @Nullable final View referent) {
        this.bitmapData = new AtomicReference<Bitmap>();
        this.userManager = chatterID.getUserManager();
        WeakReference updateView;
        if (referent != null) {
            updateView = new WeakReference((T)referent);
        }
        else {
            updateView = null;
        }
        this.updateView = updateView;
        if (this.userManager != null) {
            if (chatterID.getChatterType() == ChatterID.ChatterType.Local) {
                this.subscription = this.userManager.getCurrentLocationInfo().subscribe(SubscriptionSingleDataPool.getSingleDataKey(), UIThreadExecutor.getInstance(), new _$Lambda$4T_RyU3GIOc1CH0v3ewFouMG3lk(this));
            }
            else if (chatterID.isValidUUID()) {
                this.subscription = chatterID.getPictureID(this.userManager, UIThreadExecutor.getInstance(), (ChatterID.OnChatterPictureIDListener)new _$Lambda$4T_RyU3GIOc1CH0v3ewFouMG3lk$1(this));
            }
            else {
                this.subscription = null;
            }
        }
        else {
            this.subscription = null;
        }
    }
    
    private void onCurrentLocationInfo(final CurrentLocationInfo currentLocationInfo) {
        final ParcelData parcelData = currentLocationInfo.parcelData();
        UUID snapshotUUID;
        if (parcelData != null) {
            snapshotUUID = parcelData.getSnapshotUUID();
        }
        else {
            snapshotUUID = null;
        }
        if (snapshotUUID != null && (Objects.equal(snapshotUUID, UUIDPool.ZeroUUID) ^ true) && this.userManager != null) {
            ((ResourceMemoryCache<UUID, ResourceType>)this.userManager.getUserPicBitmapCache()).RequestResource(snapshotUUID, this);
        }
        else {
            this.bitmapData.set(null);
            if (this.updateView != null) {
                final View view = this.updateView.get();
                if (view != null) {
                    view.postInvalidate();
                }
            }
        }
    }
    
    private void requestBitmap(final UUID uuid) {
        if (uuid != null && (Objects.equal(uuid, UUIDPool.ZeroUUID) ^ true) && this.userManager != null) {
            ((ResourceMemoryCache<UUID, ResourceType>)this.userManager.getUserPicBitmapCache()).RequestResource(uuid, this);
        }
    }
    
    @Override
    public void OnResourceReady(final Object o, final boolean b) {
        if (o instanceof Bitmap) {
            this.bitmapData.set((Bitmap)o);
            if (this.updateView != null) {
                final View view = this.updateView.get();
                if (view != null) {
                    view.postInvalidate();
                }
            }
        }
    }
    
    public void dispose() {
        if (this.subscription != null) {
            this.subscription.unsubscribe();
        }
        if (this.userManager != null) {
            this.userManager.getUserPicBitmapCache().CancelRequest(this);
        }
        this.bitmapData.set(null);
    }
    
    @Nullable
    public Bitmap getBitmapData() {
        return this.bitmapData.get();
    }
}
