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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public class SLTransferManager extends SLModule {
    private static final float DEFAULT_PRIORITY = 10000.0f;
    private final BiMap<AssetKey, UUID> activeTransferIds;
    private final Map<UUID, SLTransfer> activeTransfers;
    private final RequestHandler<AssetKey> assetRequestHandler;
    private final ResultHandler<AssetKey, AssetData> assetResultHandler;
    private final UserManager userManager;

    public SLTransferManager(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit);
        this.activeTransfers = Collections.synchronizedMap(new HashMap());
        this.activeTransferIds = Maps.synchronizedBiMap(HashBiMap.create());
        this.assetRequestHandler = new AsyncRequestHandler(this.agentCircuit, new RequestHandler<AssetKey>() { // from class: com.lumiyaviewer.lumiya.slproto.modules.transfer.SLTransferManager.1
            @Override // com.lumiyaviewer.lumiya.react.RequestHandler
            public void onRequest(@Nonnull AssetKey assetKey) {
                Debug.Printf("Transfer: Requested asset download for %s", assetKey);
                SLTransfer sLTransfer = new SLTransfer(SLTransferManager.this.circuitInfo.agentID, SLTransferManager.this.circuitInfo.sessionID, assetKey, SLTransferManager.DEFAULT_PRIORITY);
                SLTransferManager.this.activeTransferIds.forcePut(assetKey, sLTransfer.getTransferUUID());
                SLTransferManager.this.BeginTransfer(sLTransfer);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.lumiyaviewer.lumiya.react.RequestHandler
            public void onRequestCancelled(@Nonnull AssetKey assetKey) {
                SLTransfer sLTransfer;
                UUID uuid = (UUID) SLTransferManager.this.activeTransferIds.remove(assetKey);
                if (uuid == null || (sLTransfer = (SLTransfer) SLTransferManager.this.activeTransfers.get(uuid)) == null) {
                    return;
                }
                SLTransferManager.this.CancelTransfer(sLTransfer);
            }
        });
        this.userManager = UserManager.getUserManager(this.agentCircuit.circuitInfo.agentID);
        if (this.userManager != null) {
            AnimationCache.getInstance().setAssetResponseCacher(this.userManager.getAssetResponseCacher());
        }
        this.assetResultHandler = this.userManager != null ? this.userManager.getAssetResponseCacher().getRequestSource().attachRequestHandler(this.assetRequestHandler) : null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void BeginTransfer(SLTransfer sLTransfer) {
        Debug.Printf("Transfer: Starting transfer: assetUUID %s, assetType %d", sLTransfer.getAssetUUID().toString(), Integer.valueOf(sLTransfer.getAssetType()));
        this.activeTransfers.put(sLTransfer.getTransferUUID(), sLTransfer);
        this.agentCircuit.SendMessage(sLTransfer.makeTransferRequest());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void CancelTransfer(SLTransfer sLTransfer) {
        this.activeTransfers.remove(sLTransfer.getTransferUUID());
        TransferAbort transferAbort = new TransferAbort();
        transferAbort.TransferInfo_Field.TransferID = sLTransfer.getTransferUUID();
        transferAbort.TransferInfo_Field.ChannelType = sLTransfer.getChannelType();
        transferAbort.isReliable = true;
        this.agentCircuit.SendMessage(transferAbort);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void EndTransfer(SLTransfer sLTransfer) {
        int status;
        this.activeTransfers.remove(sLTransfer.getTransferUUID());
        AssetKey remove = this.activeTransferIds.inverse().remove(sLTransfer.getTransferUUID());
        if (remove == null || this.assetResultHandler == null || (status = sLTransfer.getStatus()) == 3 || status == 0) {
            return;
        }
        this.assetResultHandler.onResultData(remove, new AssetData(status, sLTransfer.getData()));
    }

    @Override // com.lumiyaviewer.lumiya.slproto.modules.SLModule
    public void HandleCloseCircuit() {
        AnimationCache.getInstance().setAssetResponseCacher(null);
        if (this.userManager != null) {
            this.userManager.getAssetResponseCacher().getRequestSource().detachRequestHandler(this.assetRequestHandler);
        }
        super.HandleCloseCircuit();
    }

    @SLMessageHandler
    public void HandleTransferInfo(TransferInfo transferInfo) {
        SLTransfer sLTransfer = this.activeTransfers.get(transferInfo.TransferInfoData_Field.TransferID);
        if (sLTransfer != null) {
            Debug.Log(String.format("Transfer: Info recd, status %d, size %d", Integer.valueOf(transferInfo.TransferInfoData_Field.Status), Integer.valueOf(transferInfo.TransferInfoData_Field.Size)));
            sLTransfer.HandleTransferInfo(this, transferInfo);
        }
    }

    @SLMessageHandler
    public void HandleTransferPacket(TransferPacket transferPacket) {
        SLTransfer sLTransfer = this.activeTransfers.get(transferPacket.TransferData_Field.TransferID);
        if (sLTransfer != null) {
            Debug.Log(String.format("Transfer: data recd, packet %d, status %d, size %d.", Integer.valueOf(transferPacket.TransferData_Field.Packet), Integer.valueOf(transferPacket.TransferData_Field.Status), Integer.valueOf(transferPacket.TransferData_Field.Data.length)));
            sLTransfer.HandleTransferPacket(this, transferPacket);
        }
    }
}
