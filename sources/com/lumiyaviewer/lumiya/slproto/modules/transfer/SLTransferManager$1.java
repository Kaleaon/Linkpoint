// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.transfer;

import com.google.common.collect.BiMap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey;
import java.util.Map;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.transfer:
//            SLTransferManager, SLTransfer

class this._cls0
    implements RequestHandler
{

    final SLTransferManager this$0;

    public void onRequest(AssetKey assetkey)
    {
        Debug.Printf("Transfer: Requested asset download for %s", new Object[] {
            assetkey
        });
        SLTransfer sltransfer = new SLTransfer(SLTransferManager._2D_get2(SLTransferManager.this).agentID, SLTransferManager._2D_get2(SLTransferManager.this).sessionID, assetkey, 10000F);
        SLTransferManager._2D_get0(SLTransferManager.this).forcePut(assetkey, sltransfer.getTransferUUID());
        SLTransferManager._2D_wrap0(SLTransferManager.this, sltransfer);
    }

    public volatile void onRequest(Object obj)
    {
        onRequest((AssetKey)obj);
    }

    public void onRequestCancelled(AssetKey assetkey)
    {
        assetkey = (UUID)SLTransferManager._2D_get0(SLTransferManager.this).remove(assetkey);
        if (assetkey != null)
        {
            assetkey = (SLTransfer)SLTransferManager._2D_get1(SLTransferManager.this).get(assetkey);
            if (assetkey != null)
            {
                SLTransferManager._2D_wrap1(SLTransferManager.this, assetkey);
            }
        }
    }

    public volatile void onRequestCancelled(Object obj)
    {
        onRequestCancelled((AssetKey)obj);
    }

    ()
    {
        this$0 = SLTransferManager.this;
        super();
    }
}
