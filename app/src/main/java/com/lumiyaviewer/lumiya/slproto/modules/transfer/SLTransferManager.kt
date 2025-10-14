package com.lumiyaviewer.lumiya.slproto.modules.transfer

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.Maps
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler
import com.lumiyaviewer.lumiya.react.RequestHandler
import com.lumiyaviewer.lumiya.react.ResultHandler
import com.lumiyaviewer.lumiya.res.anim.AnimationCache
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.messages.TransferAbort
import com.lumiyaviewer.lumiya.slproto.messages.TransferInfo
import com.lumiyaviewer.lumiya.slproto.messages.TransferPacket
import com.lumiyaviewer.lumiya.slproto.modules.SLModule
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetData
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetResponseCacher
import java.util.Collections
import java.util.HashMap
import java.util.Map
import java.util.UUID
import javax.annotation.Nonnull

class SLTransferManager : SLModule {
    private Float DEFAULT_PRIORITY = 10000.0f
    /* access modifiers changed from: private */
    BiMap<AssetKey, UUID> activeTransferIds = Maps.synchronizedBiMap(HashBiMap.create())
    /* access modifiers changed from: private */
    Map<UUID, SLTransfer> activeTransfers = Collections.synchronizedMap(HashMap())
    private RequestHandler<AssetKey> assetRequestHandler = AsyncRequestHandler(this.agentCircuit, RequestHandler<AssetKey>() {
        Unit onRequest(@Nonnull AssetKey assetKey) {
            Debug.Printf("Transfer: Requested asset download for %s", assetKey)
            SLTransfer sLTransfer = SLTransfer(SLTransferManager.this.circuitInfo.agentID, SLTransferManager.this.circuitInfo.sessionID, assetKey, SLTransferManager.DEFAULT_PRIORITY)
            SLTransferManager.this.activeTransferIds.forcePut(assetKey, sLTransfer.getTransferUUID())
            SLTransferManager.this.BeginTransfer(sLTransfer)
        }

        Unit onRequestCancelled(@Nonnull AssetKey assetKey) {
            SLTransfer sLTransfer
            UUID uuid = (UUID) SLTransferManager.this.activeTransferIds.remove(assetKey)
            if (uuid != null && (sLTransfer = (SLTransfer) SLTransferManager.this.activeTransfers.get(uuid)) != null) {
                SLTransferManager.this.CancelTransfer(sLTransfer)
            }
        }
    private ResultHandler<AssetKey, AssetData> assetResultHandler
    private UserManager userManager = UserManager.getUserManager(this.agentCircuit.circuitInfo.agentID)

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    SLTransferManager(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit)
        ResultHandler<AssetKey, AssetData> resultHandler = null
        if (this.userManager != null) {
            AnimationCache.getInstance().setAssetResponseCacher(this.userManager.getAssetResponseCacher())
        }
        this.assetResultHandler = this.userManager != null ? this.userManager.getAssetResponseCacher().getRequestSource().attachRequestHandler(this.assetRequestHandler) : resultHandler
    }

    /* access modifiers changed from: private */
    Unit BeginTransfer(SLTransfer sLTransfer) {
        Debug.Printf("Transfer: Starting transfer: assetUUID %s, assetType %d", sLTransfer.getAssetUUID().toString(), Int.valueOf(sLTransfer.getAssetType()))
        this.activeTransfers.put(sLTransfer.getTransferUUID(), sLTransfer)
        this.agentCircuit.SendMessage(sLTransfer.makeTransferRequest())
    }

    /* access modifiers changed from: private */
    Unit CancelTransfer(SLTransfer sLTransfer) {
        this.activeTransfers.remove(sLTransfer.getTransferUUID())
        TransferAbort transferAbort = TransferAbort()
        transferAbort.TransferInfo_Field.TransferID = sLTransfer.getTransferUUID()
        transferAbort.TransferInfo_Field.ChannelType = sLTransfer.getChannelType()
        transferAbort.isReliable = true
        this.agentCircuit.SendMessage(transferAbort)
    }

    /* access modifiers changed from: package-private */
    Unit EndTransfer(SLTransfer sLTransfer) {
        Int status
        this.activeTransfers.remove(sLTransfer.getTransferUUID())
        AssetKey assetKey = (AssetKey) this.activeTransferIds.inverse().remove(sLTransfer.getTransferUUID())
        if (assetKey != null && this.assetResultHandler != null && (status = sLTransfer.getStatus()) != 3 && status != 0) {
            this.assetResultHandler.onResultData(assetKey, AssetData(status, sLTransfer.getData()))
        }
    }

    Unit HandleCloseCircuit() {
        AnimationCache.getInstance().setAssetResponseCacher((AssetResponseCacher) null)
        if (this.userManager != null) {
            this.userManager.getAssetResponseCacher().getRequestSource().detachRequestHandler(this.assetRequestHandler)
        }
        super.HandleCloseCircuit()
    }

    @SLMessageHandler
    Unit HandleTransferInfo(TransferInfo transferInfo) {
        SLTransfer sLTransfer = this.activeTransfers.get(transferInfo.TransferInfoData_Field.TransferID)
        if (sLTransfer != null) {
            Debug.Log(String.format("Transfer: Info recd, status %d, size %d", Any[]{Int.valueOf(transferInfo.TransferInfoData_Field.Status), Int.valueOf(transferInfo.TransferInfoData_Field.Size)}))
            sLTransfer.HandleTransferInfo(this, transferInfo)
        }
    }

    @SLMessageHandler
    Unit HandleTransferPacket(TransferPacket transferPacket) {
        SLTransfer sLTransfer = this.activeTransfers.get(transferPacket.TransferData_Field.TransferID)
        if (sLTransfer != null) {
            Debug.Log(String.format("Transfer: data recd, packet %d, status %d, size %d.", Any[]{Int.valueOf(transferPacket.TransferData_Field.Packet), Int.valueOf(transferPacket.TransferData_Field.Status), Int.valueOf(transferPacket.TransferData_Field.Data.length)}))
            sLTransfer.HandleTransferPacket(this, transferPacket)
        }
    }
}
