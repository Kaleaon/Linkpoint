// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.assets;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetData;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetResponseCacher;
import java.util.UUID;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.assets:
//            SLWearableType, SLWearableData

public class SLWearable
    implements com.lumiyaviewer.lumiya.react.Subscription.OnData, com.lumiyaviewer.lumiya.react.Subscription.OnError
{
    public static interface OnWearableStatusChangeListener
    {

        public abstract void onWearableStatusChanged(SLWearable slwearable);
    }


    public final UUID assetID;
    private final Subscription assetSubscription;
    private String inventoryName;
    private volatile boolean isFailed;
    public final UUID itemID;
    private final OnWearableStatusChangeListener statusChangeListener;
    private volatile SLWearableData wearableData;

    public SLWearable(UserManager usermanager, Executor executor, UUID uuid, UUID uuid1, SLWearableType slwearabletype, OnWearableStatusChangeListener onwearablestatuschangelistener)
    {
        itemID = uuid;
        assetID = uuid1;
        isFailed = false;
        statusChangeListener = onwearablestatuschangelistener;
        Debug.Printf("Wearable: subscribing for wearable %s", new Object[] {
            uuid1
        });
        assetSubscription = usermanager.getAssetResponseCacher().getPool().subscribe(AssetKey.createAssetKey(null, null, uuid1, slwearabletype.getAssetType().getTypeCode()), executor, this, this);
    }

    public void dispose()
    {
        Debug.Printf("Wearable: unsubscribing for wearable %s", new Object[] {
            assetID
        });
        assetSubscription.unsubscribe();
    }

    public boolean getIsFailed()
    {
        return isFailed;
    }

    public boolean getIsValid()
    {
        return wearableData != null;
    }

    public String getName()
    {
        if (inventoryName != null)
        {
            return inventoryName;
        }
        SLWearableData slwearabledata = wearableData;
        if (slwearabledata != null)
        {
            return slwearabledata.name;
        }
        if (isFailed)
        {
            return "(Failed to load)";
        } else
        {
            return "(loading)";
        }
    }

    public SLWearableData getWearableData()
    {
        return wearableData;
    }

    public void onData(AssetData assetdata)
    {
        if (assetdata != null)
        {
            if (assetdata.getStatus() != 1 || assetdata.getData() == null)
            {
                Debug.Printf("Wearable: asset transfer failed for asset %s", new Object[] {
                    assetID
                });
                isFailed = true;
            } else
            {
                try
                {
                    wearableData = new SLWearableData(assetdata.getData());
                    Debug.Printf("Wearable: retrieved wearable data for asset %s", new Object[] {
                        assetID
                    });
                    isFailed = false;
                }
                // Misplaced declaration of an exception variable
                catch (AssetData assetdata)
                {
                    Debug.Printf("Wearable: failed to parse wearable data for asset %s", new Object[] {
                        assetID
                    });
                    isFailed = true;
                }
            }
            if (statusChangeListener != null)
            {
                statusChangeListener.onWearableStatusChanged(this);
            }
        }
    }

    public volatile void onData(Object obj)
    {
        onData((AssetData)obj);
    }

    public void onError(Throwable throwable)
    {
        Debug.Printf("Wearable: got error for asset %s", new Object[] {
            assetID
        });
        isFailed = true;
        if (statusChangeListener != null)
        {
            statusChangeListener.onWearableStatusChanged(this);
        }
    }

    public void setInventoryName(String s)
    {
        inventoryName = s;
    }
}
