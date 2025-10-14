package com.lumiyaviewer.lumiya.slproto.modules.texfetcher

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.tex.TexturePriority
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.messages.ImageData
import com.lumiyaviewer.lumiya.slproto.messages.ImageNotInDatabase
import com.lumiyaviewer.lumiya.slproto.messages.ImagePacket
import com.lumiyaviewer.lumiya.slproto.modules.SLIdleHandler
import com.lumiyaviewer.lumiya.slproto.modules.SLModule
import com.lumiyaviewer.lumiya.utils.PriorityBinQueue
import java.io.File
import java.util.ConcurrentModificationException
import java.util.HashSet
import java.util.Map
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class SLTextureFetcher : SLModule : SLIdleHandler {
    private Int MAX_UDP_TRANSFERS = 2
    private String agentAppearanceService = null
    private String capURL = null
    private Long lastCheckForStalls = 0
    private PriorityBinQueue<SLTextureFetchRequest> udpQueue = PriorityBinQueue<>(TexturePriority.values().length)
    private Map<UUID, TextureUDPTransfer> udpTransfers = ConcurrentHashMap()

    SLTextureFetcher(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps, String str) {
        super(sLAgentCircuit)
        this.agentAppearanceService = str
        this.capURL = sLCaps.getCapability(SLCaps.SLCapability.GetTexture)
        Debug.Log("TextureFetcher: capURL = " + this.capURL)
    }

    private synchronized Unit RunUDPQueue() {
        SLTextureFetchRequest poll
        if (this.udpTransfers.size() < 2 && (poll = this.udpQueue.poll()) != null) {
            TextureUDPTransfer textureUDPTransfer = TextureUDPTransfer(poll.destFile, poll)
            this.udpTransfers.put(poll.textureID, textureUDPTransfer)
            textureUDPTransfer.StartTransfer(this.agentCircuit, this.circuitInfo)
        }
    }

    Unit BeginFetch(SLTextureFetchRequest sLTextureFetchRequest) {
        SLTextureFetchRequest sLTextureFetchRequest2 = null
        synchronized (this) {
            File file = sLTextureFetchRequest.destFile
            if (file.exists()) {
                sLTextureFetchRequest.outputFile = file
                sLTextureFetchRequest2 = sLTextureFetchRequest
            } else {
                this.udpQueue.add(sLTextureFetchRequest)
                RunUDPQueue()
            }
        }
        if (sLTextureFetchRequest2 != null && sLTextureFetchRequest2.onFetchComplete != null) {
            sLTextureFetchRequest2.onFetchComplete.OnTextureFetchComplete(sLTextureFetchRequest)
        }
    }

    synchronized Unit CancelFetch(SLTextureFetchRequest sLTextureFetchRequest) {
        this.udpQueue.remove(sLTextureFetchRequest)
        this.udpTransfers.remove(sLTextureFetchRequest.textureID)
        RunUDPQueue()
    }

    Unit HandleCloseCircuit() {
        StopFetching()
        super.HandleCloseCircuit()
    }

    @SLMessageHandler
    Unit HandleImageData(ImageData imageData) {
        SLTextureFetchRequest sLTextureFetchRequest
        synchronized (this) {
            TextureUDPTransfer textureUDPTransfer = this.udpTransfers.get(imageData.ImageID_Field.ID)
            if (textureUDPTransfer != null) {
                textureUDPTransfer.HandleImageData(imageData)
                if (textureUDPTransfer.isCompleted()) {
                    this.udpTransfers.remove(imageData.ImageID_Field.ID)
                    sLTextureFetchRequest = textureUDPTransfer.fetchReq
                    RunUDPQueue()
                }
            }
            sLTextureFetchRequest = null
        }
        if (sLTextureFetchRequest != null && sLTextureFetchRequest.onFetchComplete != null) {
            sLTextureFetchRequest.onFetchComplete.OnTextureFetchComplete(sLTextureFetchRequest)
        }
    }

    @SLMessageHandler
    Unit HandleImageNotInDatabase(ImageNotInDatabase imageNotInDatabase) {
        SLTextureFetchRequest sLTextureFetchRequest
        synchronized (this) {
            Debug.Log("TextureUDP: Image not in database: " + imageNotInDatabase.ImageID_Field.ID)
            TextureUDPTransfer remove = this.udpTransfers.remove(imageNotInDatabase.ImageID_Field.ID)
            sLTextureFetchRequest = remove != null ? remove.fetchReq : null
        }
        if (!(sLTextureFetchRequest == null || sLTextureFetchRequest.onFetchComplete == null)) {
            sLTextureFetchRequest.onFetchComplete.OnTextureFetchComplete(sLTextureFetchRequest)
        }
        RunUDPQueue()
    }

    @SLMessageHandler
    Unit HandleImagePacket(ImagePacket imagePacket) {
        SLTextureFetchRequest sLTextureFetchRequest
        synchronized (this) {
            TextureUDPTransfer textureUDPTransfer = this.udpTransfers.get(imagePacket.ImageID_Field.ID)
            if (textureUDPTransfer != null) {
                textureUDPTransfer.HandleImagePacket(imagePacket)
                if (textureUDPTransfer.isCompleted()) {
                    this.udpTransfers.remove(imagePacket.ImageID_Field.ID)
                    SLTextureFetchRequest sLTextureFetchRequest2 = textureUDPTransfer.fetchReq
                    sLTextureFetchRequest2.outputFile = textureUDPTransfer.getOutputFile()
                    RunUDPQueue()
                    sLTextureFetchRequest = sLTextureFetchRequest2
                }
            }
            sLTextureFetchRequest = null
        }
        if (sLTextureFetchRequest != null && sLTextureFetchRequest.onFetchComplete != null) {
            sLTextureFetchRequest.onFetchComplete.OnTextureFetchComplete(sLTextureFetchRequest)
        }
    }

    Unit ProcessIdle() {
        HashSet hashSet
        HashSet<UUID> hashSet2 = null
        Long currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis >= this.lastCheckForStalls + 1000) {
            this.lastCheckForStalls = currentTimeMillis
            try {
                for (Map.Entry entry : this.udpTransfers.entrySet()) {
                    if (!((TextureUDPTransfer) entry.getValue()).hasStalled() || ((TextureUDPTransfer) entry.getValue()).RetryTransfer(this.agentCircuit, this.circuitInfo)) {
                        hashSet = hashSet2
                    } else {
                        Debug.Printf("Cannot retry texture %s", ((UUID) entry.getKey()).toString())
                        HashSet hashSet3 = hashSet2 == null ? HashSet() : hashSet2
                        hashSet3.add((UUID) entry.getKey())
                        hashSet = hashSet3
                    }
                    hashSet2 = hashSet
                }
                if (hashSet2 != null) {
                    for (UUID remove : hashSet2) {
                        TextureUDPTransfer remove2 = this.udpTransfers.remove(remove)
                        if (remove2 != null) {
                            SLTextureFetchRequest sLTextureFetchRequest = remove2.fetchReq
                            sLTextureFetchRequest.outputFile = null
                            if (sLTextureFetchRequest.onFetchComplete != null) {
                                sLTextureFetchRequest.onFetchComplete.OnTextureFetchComplete(sLTextureFetchRequest)
                            }
                        }
                    }
                    RunUDPQueue()
                }
            } catch (ConcurrentModificationException e) {
                Debug.Warning(e)
            }
        }
    }

    Unit StopFetching() {
        this.udpQueue.clear()
    }

    Unit UpdatePriority(SLTextureFetchRequest sLTextureFetchRequest) {
        this.udpQueue.updatePriority(sLTextureFetchRequest)
    }

    String getAgentAppearanceService() {
        return this.agentAppearanceService
    }

    String getCapURL() {
        return this.capURL
    }
}
