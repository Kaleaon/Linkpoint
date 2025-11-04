// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.transfer;

import com.lumiyaviewer.lumiya.slproto.messages.TransferPacket;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.TransferInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetResponseCacher;
import com.lumiyaviewer.lumiya.slproto.messages.TransferAbort;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.res.anim.AnimationCache;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.Debug;
import javax.annotation.Nonnull;
import com.google.common.collect.Maps;
import com.google.common.collect.HashBiMap;
import java.util.Collections;
import java.util.HashMap;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetData;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import java.util.Map;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey;
import com.google.common.collect.BiMap;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;

public class SLTransferManager extends SLModule
{
    private static final float DEFAULT_PRIORITY = 10000.0f;
    private final BiMap<AssetKey, UUID> activeTransferIds;
    private final Map<UUID, SLTransfer> activeTransfers;
    private final RequestHandler<AssetKey> assetRequestHandler;
    private final ResultHandler<AssetKey, AssetData> assetResultHandler;
    private final UserManager userManager;
    
    public SLTransferManager(final SLAgentCircuit slAgentCircuit) {
        final ResultHandler<AssetKey, AssetData> resultHandler = null;
        super(slAgentCircuit);
        this.activeTransfers = Collections.synchronizedMap(new HashMap<UUID, SLTransfer>());
        this.activeTransferIds = Maps.synchronizedBiMap((BiMap<AssetKey, UUID>)HashBiMap.create());
        this.assetRequestHandler = new AsyncRequestHandler<AssetKey>(this.agentCircuit, new RequestHandler<AssetKey>() {
            @Override
            public void onRequest(@Nonnull final AssetKey assetKey) {
                Debug.Printf("Transfer: Requested asset download for %s", assetKey);
                final SLTransfer slTransfer = new SLTransfer(SLTransferManager.this.circuitInfo.agentID, SLTransferManager.this.circuitInfo.sessionID, assetKey, 10000.0f);
                SLTransferManager.this.activeTransferIds.forcePut(assetKey, slTransfer.getTransferUUID());
                SLTransferManager.this.BeginTransfer(slTransfer);
            }
            
            @Override
            public void onRequestCancelled(@Nonnull final AssetKey assetKey) {
                final UUID uuid = (UUID)SLTransferManager.this.activeTransferIds.remove(assetKey);
                if (uuid != null) {
                    final SLTransfer slTransfer = SLTransferManager.this.activeTransfers.get(uuid);
                    if (slTransfer != null) {
                        SLTransferManager.this.CancelTransfer(slTransfer);
                    }
                }
            }
        });
        this.userManager = UserManager.getUserManager(this.agentCircuit.circuitInfo.agentID);
        if (this.userManager != null) {
            AnimationCache.getInstance().setAssetResponseCacher(this.userManager.getAssetResponseCacher());
        }
        ResultHandler<AssetKey, AssetData> attachRequestHandler = resultHandler;
        if (this.userManager != null) {
            attachRequestHandler = this.userManager.getAssetResponseCacher().getRequestSource().attachRequestHandler(this.assetRequestHandler);
        }
        this.assetResultHandler = attachRequestHandler;
    }
    
    private void BeginTransfer(final SLTransfer slTransfer) {
        Debug.Printf("Transfer: Starting transfer: assetUUID %s, assetType %d", slTransfer.getAssetUUID().toString(), slTransfer.getAssetType());
        this.activeTransfers.put(slTransfer.getTransferUUID(), slTransfer);
        this.agentCircuit.SendMessage(slTransfer.makeTransferRequest());
    }
    
    private void CancelTransfer(final SLTransfer slTransfer) {
        this.activeTransfers.remove(slTransfer.getTransferUUID());
        final TransferAbort transferAbort = new TransferAbort();
        transferAbort.TransferInfo_Field.TransferID = slTransfer.getTransferUUID();
        transferAbort.TransferInfo_Field.ChannelType = slTransfer.getChannelType();
        transferAbort.isReliable = true;
        this.agentCircuit.SendMessage(transferAbort);
    }
    
    void EndTransfer(final SLTransfer slTransfer) {
        this.activeTransfers.remove(slTransfer.getTransferUUID());
        final AssetKey assetKey = this.activeTransferIds.inverse().remove(slTransfer.getTransferUUID());
        if (assetKey != null && this.assetResultHandler != null) {
            final int status = slTransfer.getStatus();
            if (status != 3 && status != 0) {
                this.assetResultHandler.onResultData(assetKey, new AssetData(status, slTransfer.getData()));
            }
        }
    }
    
    @Override
    public void HandleCloseCircuit() {
        AnimationCache.getInstance().setAssetResponseCacher(null);
        if (this.userManager != null) {
            this.userManager.getAssetResponseCacher().getRequestSource().detachRequestHandler(this.assetRequestHandler);
        }
        super.HandleCloseCircuit();
    }
    
    @SLMessageHandler
    public void HandleTransferInfo(final TransferInfo transferInfo) {
        final SLTransfer slTransfer = this.activeTransfers.get(transferInfo.TransferInfoData_Field.TransferID);
        if (slTransfer != null) {
            Debug.Log(String.format("Transfer: Info recd, status %d, size %d", transferInfo.TransferInfoData_Field.Status, transferInfo.TransferInfoData_Field.Size));
            slTransfer.HandleTransferInfo(this, transferInfo);
        }
    }
    
    @SLMessageHandler
    public void HandleTransferPacket(final TransferPacket transferPacket) {
        final SLTransfer slTransfer = this.activeTransfers.get(transferPacket.TransferData_Field.TransferID);
        if (slTransfer != null) {
            Debug.Log(String.format("Transfer: data recd, packet %d, status %d, size %d.", transferPacket.TransferData_Field.Packet, transferPacket.TransferData_Field.Status, transferPacket.TransferData_Field.Data.length));
            slTransfer.HandleTransferPacket(this, transferPacket);
        }
    }
}
