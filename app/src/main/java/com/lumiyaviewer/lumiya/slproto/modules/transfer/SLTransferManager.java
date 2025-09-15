package com.lumiyaviewer.lumiya.slproto.modules.transfer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.res.anim.AnimationCache;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
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
import javax.annotation.Nonnull;

public class SLTransferManager extends SLModule {
    private static final float DEFAULT_PRIORITY = 10000.0f;
    /* access modifiers changed from: private */
    public final BiMap<AssetKey, UUID> activeTransferIds = Maps.synchronizedBiMap(HashBiMap.create());
    /* access modifiers changed from: private */
    public final Map<UUID, SLTransfer> activeTransfers = Collections.synchronizedMap(new HashMap());
    private final RequestHandler<AssetKey> assetRequestHandler = new AsyncRequestHandler(this.agentCircuit, new RequestHandler<AssetKey>() {
        public void onRequest(@Nonnull AssetKey assetKey) {
            Debug.Printf("Transfer: Requested asset download for %s", assetKey);
            SLTransfer sLTransfer = new SLTransfer(SLTransferManager.this.circuitInfo.agentID, SLTransferManager.this.circuitInfo.sessionID, assetKey, SLTransferManager.DEFAULT_PRIORITY);
            SLTransferManager.this.activeTransferIds.forcePut(assetKey, sLTransfer.getTransferUUID());
            SLTransferManager.this.BeginTransfer(sLTransfer);
        }

        public void onRequestCancelled(@Nonnull AssetKey assetKey) {
            SLTransfer sLTransfer;
            UUID uuid = (UUID) SLTransferManager.this.activeTransferIds.remove(assetKey);
            if (uuid != null && (sLTransfer = (SLTransfer) SLTransferManager.this.activeTransfers.get(uuid)) != null) {
                SLTransferManager.this.CancelTransfer(sLTransfer);
            }
        }
    private final ResultHandler<AssetKey, AssetData> assetResultHandler;
    private final UserManager userManager = UserManager.getUserManager(this.agentCircuit.circuitInfo.agentID);

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SLTransferManager(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit);
        ResultHandler<AssetKey, AssetData> resultHandler = null;
        if (this.userManager != null) {
            AnimationCache.getInstance().setAssetResponseCacher(this.userManager.getAssetResponseCacher());
        }
        this.assetResultHandler = this.userManager != null ? this.userManager.getAssetResponseCacher().getRequestSource().attachRequestHandler(this.assetRequestHandler) : resultHandler;
    }

    /* access modifiers changed from: private */
    public void BeginTransfer(SLTransfer sLTransfer) {
        Debug.Printf("Transfer: Starting transfer: assetUUID %s, assetType %d", sLTransfer.getAssetUUID().toString(), Integer.valueOf(sLTransfer.getAssetType()));
        this.activeTransfers.put(sLTransfer.getTransferUUID(), sLTransfer);
        this.agentCircuit.SendMessage(sLTransfer.makeTransferRequest());
    }

    /* access modifiers changed from: private */
    public void CancelTransfer(SLTransfer sLTransfer) {
        this.activeTransfers.remove(sLTransfer.getTransferUUID());
        TransferAbort transferAbort = new TransferAbort();
        transferAbort.TransferInfo_Field.TransferID = sLTransfer.getTransferUUID();
        transferAbort.TransferInfo_Field.ChannelType = sLTransfer.getChannelType();
        transferAbort.isReliable = true;
        this.agentCircuit.SendMessage(transferAbort);
    }

    /* access modifiers changed from: package-private */
    public void EndTransfer(SLTransfer sLTransfer) {
        int status;
        this.activeTransfers.remove(sLTransfer.getTransferUUID());
        AssetKey assetKey = (AssetKey) this.activeTransferIds.inverse().remove(sLTransfer.getTransferUUID());
        if (assetKey != null && this.assetResultHandler != null && (status = sLTransfer.getStatus()) != 3 && status != 0) {
            this.assetResultHandler.onResultData(assetKey, new AssetData(status, sLTransfer.getData()));
        }
    }

    public void HandleCloseCircuit() {
        AnimationCache.getInstance().setAssetResponseCacher((AssetResponseCacher) null);
        if (this.userManager != null) {
            this.userManager.getAssetResponseCacher().getRequestSource().detachRequestHandler(this.assetRequestHandler);
        }
        super.HandleCloseCircuit();
    }

    @SLMessageHandler
    public void HandleTransferInfo(TransferInfo transferInfo) {
        SLTransfer sLTransfer = this.activeTransfers.get(transferInfo.TransferInfoData_Field.TransferID);
        if (sLTransfer != null) {
            Debug.Log(String.format("Transfer: Info recd, status %d, size %d", new Object[]{Integer.valueOf(transferInfo.TransferInfoData_Field.Status), Integer.valueOf(transferInfo.TransferInfoData_Field.Size)}));
            sLTransfer.HandleTransferInfo(this, transferInfo);
        }
    }

    @SLMessageHandler
    public void HandleTransferPacket(TransferPacket transferPacket) {
        SLTransfer sLTransfer = this.activeTransfers.get(transferPacket.TransferData_Field.TransferID);
        if (sLTransfer != null) {
            Debug.Log(String.format("Transfer: data recd, packet %d, status %d, size %d.", new Object[]{Integer.valueOf(transferPacket.TransferData_Field.Packet), Integer.valueOf(transferPacket.TransferData_Field.Status), Integer.valueOf(transferPacket.TransferData_Field.Data.length)}));
            sLTransfer.HandleTransferPacket(this, transferPacket);
        }
    }
}
