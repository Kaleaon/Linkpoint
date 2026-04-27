package com.linkpoint.slproto.modules.texfetcher

import com.linkpoint.Debug
import com.linkpoint.render.tex.TexturePriority
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.caps.SLCaps
import com.linkpoint.slproto.handler.SLMessageHandler
import com.linkpoint.slproto.messages.ImageData
import com.linkpoint.slproto.messages.ImageNotInDatabase
import com.linkpoint.slproto.messages.ImagePacket
import com.linkpoint.slproto.modules.SLIdleHandler
import com.linkpoint.slproto.modules.SLModule
import com.linkpoint.utils.PriorityBinQueue
import java.io.File
import java.util.ConcurrentModificationException
import java.util.HashSet
import java.util.Map
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class SLTextureFetcher : SLModule(), SLIdleHandler {
    private const val MAX_UDP_TRANSFERS: Int = 2
    private String agentAppearanceService = null
    private String capURL = null
    private Long lastCheckForStalls = 0
    private PriorityBinQueue<SLTextureFetchRequest> udpQueue = PriorityBinQueue<>(TexturePriority.values().length)
    private Map<UUID, TextureUDPTransfer> udpTransfers = ConcurrentHashMap()

    public SLTextureFetcher(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps, String str) {
        super(sLAgentCircuit)
        this.agentAppearanceService = str
        this.capURL = sLCaps.getCapability(SLCaps.SLCapability.GetTexture)
        Debug.Log("TextureFetcher: capURL = " + this.capURL)
    }

    private synchronized Unit RunUDPQueue() {
        SLTextureFetchRequest poll
        if (this.udpTransfers.size() < 2 && (poll = this.udpQueue.poll()) != null) {
            val textureUDPTransfer: TextureUDPTransfer = TextureUDPTransfer(poll.destFile, poll)
            this.udpTransfers.put(poll.textureID, textureUDPTransfer)
            textureUDPTransfer.StartTransfer(this.agentCircuit, this.circuitInfo)
        }
    }

    fun BeginFetch(sLTextureFetchRequest: SLTextureFetchRequest) {
        val sLTextureFetchRequest2: SLTextureFetchRequest = null
        synchronized (this) {
            val file: File = sLTextureFetchRequest.destFile
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

    public synchronized Unit CancelFetch(SLTextureFetchRequest sLTextureFetchRequest) {
        this.udpQueue.remove(sLTextureFetchRequest)
        this.udpTransfers.remove(sLTextureFetchRequest.textureID)
        RunUDPQueue()
    }

    fun HandleCloseCircuit() {
        StopFetching()
        super.HandleCloseCircuit()
    }

    @SLMessageHandler
    fun HandleImageData(imageData: ImageData) {
        SLTextureFetchRequest sLTextureFetchRequest
        synchronized (this) {
            val textureUDPTransfer: TextureUDPTransfer = this.udpTransfers.get(imageData.ImageID_Field.ID)
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
    fun HandleImageNotInDatabase(imageNotInDatabase: ImageNotInDatabase) {
        SLTextureFetchRequest sLTextureFetchRequest
        synchronized (this) {
            Debug.Log("TextureUDP: Image not in database: " + imageNotInDatabase.ImageID_Field.ID)
            val remove: TextureUDPTransfer = this.udpTransfers.remove(imageNotInDatabase.ImageID_Field.ID)
            sLTextureFetchRequest = remove != null ? remove.fetchReq : null
        }
        if (!(sLTextureFetchRequest == null || sLTextureFetchRequest.onFetchComplete == null)) {
            sLTextureFetchRequest.onFetchComplete.OnTextureFetchComplete(sLTextureFetchRequest)
        }
        RunUDPQueue()
    }

    @SLMessageHandler
    fun HandleImagePacket(imagePacket: ImagePacket) {
        SLTextureFetchRequest sLTextureFetchRequest
        synchronized (this) {
            val textureUDPTransfer: TextureUDPTransfer = this.udpTransfers.get(imagePacket.ImageID_Field.ID)
            if (textureUDPTransfer != null) {
                textureUDPTransfer.HandleImagePacket(imagePacket)
                if (textureUDPTransfer.isCompleted()) {
                    this.udpTransfers.remove(imagePacket.ImageID_Field.ID)
                    val sLTextureFetchRequest2: SLTextureFetchRequest = textureUDPTransfer.fetchReq
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

    fun ProcessIdle() {
        HashSet hashSet
        val hashSet2: HashSet<UUID> = null
        val currentTimeMillis: Long = System.currentTimeMillis()
        if (currentTimeMillis >= this.lastCheckForStalls + 1000) {
            this.lastCheckForStalls = currentTimeMillis
            try {
                for (Map.Entry entry : this.udpTransfers.entrySet()) {
                    if (!((TextureUDPTransfer) entry.getValue()).hasStalled() || ((TextureUDPTransfer) entry.getValue()).RetryTransfer(this.agentCircuit, this.circuitInfo)) {
                        hashSet = hashSet2
                    } else {
                        Debug.Printf("Cannot retry texture %s", ((UUID) entry.getKey()).toString())
                        val hashSet3: HashSet = hashSet2 == null ? HashSet() : hashSet2
                        hashSet3.add((UUID) entry.getKey())
                        hashSet = hashSet3
                    }
                    hashSet2 = hashSet
                }
                if (hashSet2 != null) {
                    for (UUID remove : hashSet2) {
                        val remove2: TextureUDPTransfer = this.udpTransfers.remove(remove)
                        if (remove2 != null) {
                            val sLTextureFetchRequest: SLTextureFetchRequest = remove2.fetchReq
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

    fun StopFetching() {
        this.udpQueue.clear()
    }

    fun UpdatePriority(sLTextureFetchRequest: SLTextureFetchRequest) {
        this.udpQueue.updatePriority(sLTextureFetchRequest)
    }

     public fun getAgentAppearanceService(): String {
        return this.agentAppearanceService
    }

     public fun getCapURL(): String {
        return this.capURL
    }
}
