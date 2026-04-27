// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.transfer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.res.anim.AnimationCache;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.messages.TransferAbort;
import com.lumiyaviewer.lumiya.slproto.messages.TransferInfo;
import com.lumiyaviewer.lumiya.slproto.messages.TransferPacket;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetData;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetResponseCacher;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.transfer:
//            SLTransfer

public class SLTransferManager extends SLModule
{

    private static final float DEFAULT_PRIORITY = 10000F;
    private final BiMap activeTransferIds = Maps.synchronizedBiMap(HashBiMap.create());
    private final Map activeTransfers = Collections.synchronizedMap(new HashMap());
    private final RequestHandler assetRequestHandler;
    private final ResultHandler assetResultHandler;
    private final UserManager userManager;

    static BiMap _2D_get0(SLTransferManager sltransfermanager)
    {
        return sltransfermanager.activeTransferIds;
    }

    static Map _2D_get1(SLTransferManager sltransfermanager)
    {
        return sltransfermanager.activeTransfers;
    }

    static SLCircuitInfo _2D_get2(SLTransferManager sltransfermanager)
    {
        return sltransfermanager.circuitInfo;
    }

    static void _2D_wrap0(SLTransferManager sltransfermanager, SLTransfer sltransfer)
    {
        sltransfermanager.BeginTransfer(sltransfer);
    }

    static void _2D_wrap1(SLTransferManager sltransfermanager, SLTransfer sltransfer)
    {
        sltransfermanager.CancelTransfer(sltransfer);
    }

    public SLTransferManager(SLAgentCircuit slagentcircuit)
    {
        Object obj = null;
        super(slagentcircuit);
        assetRequestHandler = new AsyncRequestHandler(agentCircuit, new RequestHandler() {

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

            public volatile void onRequest(Object obj1)
            {
                onRequest((AssetKey)obj1);
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

            public volatile void onRequestCancelled(Object obj1)
            {
                onRequestCancelled((AssetKey)obj1);
            }

            
            {
                this$0 = SLTransferManager.this;
                super();
            }
        });
        userManager = UserManager.getUserManager(agentCircuit.circuitInfo.agentID);
        if (userManager != null)
        {
            AnimationCache.getInstance().setAssetResponseCacher(userManager.getAssetResponseCacher());
        }
        slagentcircuit = obj;
        if (userManager != null)
        {
            slagentcircuit = userManager.getAssetResponseCacher().getRequestSource().attachRequestHandler(assetRequestHandler);
        }
        assetResultHandler = slagentcircuit;
    }

    private void BeginTransfer(SLTransfer sltransfer)
    {
        Debug.Printf("Transfer: Starting transfer: assetUUID %s, assetType %d", new Object[] {
            sltransfer.getAssetUUID().toString(), Integer.valueOf(sltransfer.getAssetType())
        });
        activeTransfers.put(sltransfer.getTransferUUID(), sltransfer);
        sltransfer = sltransfer.makeTransferRequest();
        agentCircuit.SendMessage(sltransfer);
    }

    private void CancelTransfer(SLTransfer sltransfer)
    {
        activeTransfers.remove(sltransfer.getTransferUUID());
        TransferAbort transferabort = new TransferAbort();
        transferabort.TransferInfo_Field.TransferID = sltransfer.getTransferUUID();
        transferabort.TransferInfo_Field.ChannelType = sltransfer.getChannelType();
        transferabort.isReliable = true;
        agentCircuit.SendMessage(transferabort);
    }

    void EndTransfer(SLTransfer sltransfer)
    {
        activeTransfers.remove(sltransfer.getTransferUUID());
        AssetKey assetkey = (AssetKey)activeTransferIds.inverse().remove(sltransfer.getTransferUUID());
        if (assetkey != null && assetResultHandler != null)
        {
            int i = sltransfer.getStatus();
            if (i != 3 && i != 0)
            {
                assetResultHandler.onResultData(assetkey, new AssetData(i, sltransfer.getData()));
            }
        }
    }

    public void HandleCloseCircuit()
    {
        AnimationCache.getInstance().setAssetResponseCacher(null);
        if (userManager != null)
        {
            userManager.getAssetResponseCacher().getRequestSource().detachRequestHandler(assetRequestHandler);
        }
        super.HandleCloseCircuit();
    }

    public void HandleTransferInfo(TransferInfo transferinfo)
    {
        SLTransfer sltransfer = (SLTransfer)activeTransfers.get(transferinfo.TransferInfoData_Field.TransferID);
        if (sltransfer != null)
        {
            Debug.Log(String.format("Transfer: Info recd, status %d, size %d", new Object[] {
                Integer.valueOf(transferinfo.TransferInfoData_Field.Status), Integer.valueOf(transferinfo.TransferInfoData_Field.Size)
            }));
            sltransfer.HandleTransferInfo(this, transferinfo);
        }
    }

    public void HandleTransferPacket(TransferPacket transferpacket)
    {
        SLTransfer sltransfer = (SLTransfer)activeTransfers.get(transferpacket.TransferData_Field.TransferID);
        if (sltransfer != null)
        {
            Debug.Log(String.format("Transfer: data recd, packet %d, status %d, size %d.", new Object[] {
                Integer.valueOf(transferpacket.TransferData_Field.Packet), Integer.valueOf(transferpacket.TransferData_Field.Status), Integer.valueOf(transferpacket.TransferData_Field.Data.length)
            }));
            sltransfer.HandleTransferPacket(this, transferpacket);
        }
    }
}
