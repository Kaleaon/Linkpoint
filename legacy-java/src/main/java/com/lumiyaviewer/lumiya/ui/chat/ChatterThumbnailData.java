package com.lumiyaviewer.lumiya.ui.chat;

import android.graphics.Bitmap;
import android.view.View;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class ChatterThumbnailData implements ResourceConsumer {
    private final AtomicReference<Bitmap> bitmapData = new AtomicReference<>();
    private final Subscription subscription;

    @Nullable
    private final WeakReference<View> updateView;

    @Nullable
    private final UserManager userManager;

    public ChatterThumbnailData(@Nonnull ChatterID chatterID, @Nullable View view) {
        this.userManager = chatterID.getUserManager();
        this.updateView = view != null ? new WeakReference<>(view) : null;
        if (this.userManager == null) {
            this.subscription = null;
            return;
        }
        if (chatterID.getChatterType() == ChatterID.ChatterType.Local) {
            this.subscription = this.userManager.getCurrentLocationInfo().subscribe((Subscribable<SubscriptionSingleKey, CurrentLocationInfo>) SubscriptionSingleDataPool.getSingleDataKey(), UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$4T-RyU3GIOc1CH0v3ewFouMG3lk
                private final /* synthetic */ void $m$0(Object obj) {
                    ((ChatterThumbnailData) this).m426com_lumiyaviewer_lumiya_ui_chat_ChatterThumbnailDatamthref0((CurrentLocationInfo) obj);
                }

                @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
                public final void onData(Object obj) {
                    $m$0(obj);
                }
            });
        } else if (chatterID.isValidUUID()) {
            this.subscription = chatterID.getPictureID(this.userManager, UIThreadExecutor.getInstance(), new ChatterID.OnChatterPictureIDListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$4T-RyU3GIOc1CH0v3ewFouMG3lk.1
                private final /* synthetic */ void $m$0(UUID uuid) {
                    ((ChatterThumbnailData) this).m427com_lumiyaviewer_lumiya_ui_chat_ChatterThumbnailDatamthref1(uuid);
                }

                @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterID.OnChatterPictureIDListener
                public final void onChatterPictureID(UUID uuid) {
                    $m$0(uuid);
                }
            });
        } else {
            this.subscription = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onCurrentLocationInfo, reason: merged with bridge method [inline-methods] */
    public void m426com_lumiyaviewer_lumiya_ui_chat_ChatterThumbnailDatamthref0(CurrentLocationInfo currentLocationInfo) {
        View view;
        ParcelData parcelData = currentLocationInfo.parcelData();
        UUID snapshotUUID = parcelData != null ? parcelData.getSnapshotUUID() : null;
        if (snapshotUUID != null && (!Objects.equal(snapshotUUID, UUIDPool.ZeroUUID)) && this.userManager != null) {
            this.userManager.getUserPicBitmapCache().RequestResource(snapshotUUID, this);
            return;
        }
        this.bitmapData.set(null);
        if (this.updateView == null || (view = this.updateView.get()) == null) {
            return;
        }
        view.postInvalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: requestBitmap, reason: merged with bridge method [inline-methods] */
    public void m427com_lumiyaviewer_lumiya_ui_chat_ChatterThumbnailDatamthref1(UUID uuid) {
        if (uuid == null || !(!Objects.equal(uuid, UUIDPool.ZeroUUID)) || this.userManager == null) {
            return;
        }
        this.userManager.getUserPicBitmapCache().RequestResource(uuid, this);
    }

    @Override // com.lumiyaviewer.lumiya.res.ResourceConsumer
    public void OnResourceReady(Object obj, boolean z) {
        View view;
        if (obj instanceof Bitmap) {
            this.bitmapData.set((Bitmap) obj);
            if (this.updateView == null || (view = this.updateView.get()) == null) {
                return;
            }
            view.postInvalidate();
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
