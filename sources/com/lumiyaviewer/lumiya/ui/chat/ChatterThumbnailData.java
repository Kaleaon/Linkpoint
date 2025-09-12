// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.graphics.Bitmap;
import android.view.View;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserPicBitmapCache;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ChatterThumbnailData
    implements ResourceConsumer
{

    private final AtomicReference bitmapData = new AtomicReference();
    private final Subscription subscription;
    private final WeakReference updateView;
    private final UserManager userManager;

    public ChatterThumbnailData(ChatterID chatterid, View view)
    {
        userManager = chatterid.getUserManager();
        if (view != null)
        {
            view = new WeakReference(view);
        } else
        {
            view = null;
        }
        updateView = view;
        if (userManager != null)
        {
            if (chatterid.getChatterType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local)
            {
                subscription = userManager.getCurrentLocationInfo().subscribe(SubscriptionSingleDataPool.getSingleDataKey(), UIThreadExecutor.getInstance(), new _2D_.Lambda._cls4T_2D_RyU3GIOc1CH0v3ewFouMG3lk(this));
                return;
            }
            if (chatterid.isValidUUID())
            {
                subscription = chatterid.getPictureID(userManager, UIThreadExecutor.getInstance(), new _2D_.Lambda._cls4T_2D_RyU3GIOc1CH0v3ewFouMG3lk._cls1(this));
                return;
            } else
            {
                subscription = null;
                return;
            }
        } else
        {
            subscription = null;
            return;
        }
    }

    private void onCurrentLocationInfo(CurrentLocationInfo currentlocationinfo)
    {
        currentlocationinfo = currentlocationinfo.parcelData();
        if (currentlocationinfo != null)
        {
            currentlocationinfo = currentlocationinfo.getSnapshotUUID();
        } else
        {
            currentlocationinfo = null;
        }
        if (currentlocationinfo != null && Objects.equal(currentlocationinfo, UUIDPool.ZeroUUID) ^ true && userManager != null)
        {
            userManager.getUserPicBitmapCache().RequestResource(currentlocationinfo, this);
        } else
        {
            bitmapData.set(null);
            if (updateView != null)
            {
                currentlocationinfo = (View)updateView.get();
                if (currentlocationinfo != null)
                {
                    currentlocationinfo.postInvalidate();
                    return;
                }
            }
        }
    }

    private void requestBitmap(UUID uuid)
    {
        if (uuid != null && Objects.equal(uuid, UUIDPool.ZeroUUID) ^ true && userManager != null)
        {
            userManager.getUserPicBitmapCache().RequestResource(uuid, this);
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_ChatterThumbnailData_2D_mthref_2D_0(CurrentLocationInfo currentlocationinfo)
    {
        onCurrentLocationInfo(currentlocationinfo);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_ChatterThumbnailData_2D_mthref_2D_1(UUID uuid)
    {
        requestBitmap(uuid);
    }

    public void OnResourceReady(Object obj, boolean flag)
    {
        if (obj instanceof Bitmap)
        {
            bitmapData.set((Bitmap)obj);
            if (updateView != null)
            {
                obj = (View)updateView.get();
                if (obj != null)
                {
                    ((View) (obj)).postInvalidate();
                }
            }
        }
    }

    public void dispose()
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
        if (userManager != null)
        {
            userManager.getUserPicBitmapCache().CancelRequest(this);
        }
        bitmapData.set(null);
    }

    public Bitmap getBitmapData()
    {
        return (Bitmap)bitmapData.get();
    }
}
