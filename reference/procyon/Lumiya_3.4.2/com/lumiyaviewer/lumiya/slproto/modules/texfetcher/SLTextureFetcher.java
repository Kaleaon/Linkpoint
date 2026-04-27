// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.texfetcher;

import java.util.Iterator;
import java.util.Set;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import com.lumiyaviewer.lumiya.slproto.messages.ImagePacket;
import com.lumiyaviewer.lumiya.slproto.messages.ImageNotInDatabase;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.ImageData;
import java.io.File;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.tex.TexturePriority;
import java.util.concurrent.ConcurrentHashMap;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import java.util.Map;
import com.lumiyaviewer.lumiya.utils.PriorityBinQueue;
import com.lumiyaviewer.lumiya.slproto.modules.SLIdleHandler;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;

public class SLTextureFetcher extends SLModule implements SLIdleHandler
{
    private static final int MAX_UDP_TRANSFERS = 2;
    private String agentAppearanceService;
    private String capURL;
    private long lastCheckForStalls;
    private PriorityBinQueue<SLTextureFetchRequest> udpQueue;
    private Map<UUID, TextureUDPTransfer> udpTransfers;
    
    public SLTextureFetcher(final SLAgentCircuit slAgentCircuit, final SLCaps slCaps, final String agentAppearanceService) {
        super(slAgentCircuit);
        this.capURL = null;
        this.agentAppearanceService = null;
        this.udpTransfers = new ConcurrentHashMap<UUID, TextureUDPTransfer>();
        this.udpQueue = new PriorityBinQueue<SLTextureFetchRequest>(TexturePriority.values().length);
        this.lastCheckForStalls = 0L;
        this.agentAppearanceService = agentAppearanceService;
        this.capURL = slCaps.getCapability(SLCaps.SLCapability.GetTexture);
        Debug.Log("TextureFetcher: capURL = " + this.capURL);
    }
    
    private void RunUDPQueue() {
        synchronized (this) {
            if (this.udpTransfers.size() < 2) {
                final SLTextureFetchRequest slTextureFetchRequest = this.udpQueue.poll();
                if (slTextureFetchRequest != null) {
                    final TextureUDPTransfer textureUDPTransfer = new TextureUDPTransfer(slTextureFetchRequest.destFile, slTextureFetchRequest);
                    this.udpTransfers.put(slTextureFetchRequest.textureID, textureUDPTransfer);
                    textureUDPTransfer.StartTransfer(this.agentCircuit, this.circuitInfo);
                }
            }
        }
    }
    
    public void BeginFetch(final SLTextureFetchRequest slTextureFetchRequest) {
        SLTextureFetchRequest slTextureFetchRequest2 = null;
        synchronized (this) {
            final File destFile = slTextureFetchRequest.destFile;
            if (destFile.exists()) {
                slTextureFetchRequest.outputFile = destFile;
                slTextureFetchRequest2 = slTextureFetchRequest;
            }
            else {
                this.udpQueue.add(slTextureFetchRequest);
                this.RunUDPQueue();
            }
            monitorexit(this);
            if (slTextureFetchRequest2 != null && slTextureFetchRequest2.onFetchComplete != null) {
                slTextureFetchRequest2.onFetchComplete.OnTextureFetchComplete(slTextureFetchRequest);
            }
        }
    }
    
    public void CancelFetch(final SLTextureFetchRequest slTextureFetchRequest) {
        synchronized (this) {
            this.udpQueue.remove(slTextureFetchRequest);
            this.udpTransfers.remove(slTextureFetchRequest.textureID);
            this.RunUDPQueue();
        }
    }
    
    @Override
    public void HandleCloseCircuit() {
        this.StopFetching();
        super.HandleCloseCircuit();
    }
    
    @SLMessageHandler
    public void HandleImageData(final ImageData imageData) {
        while (true) {
            while (true) {
                synchronized (this) {
                    final TextureUDPTransfer textureUDPTransfer = this.udpTransfers.get(imageData.ImageID_Field.ID);
                    if (textureUDPTransfer != null) {
                        textureUDPTransfer.HandleImageData(imageData);
                        if (textureUDPTransfer.isCompleted()) {
                            this.udpTransfers.remove(imageData.ImageID_Field.ID);
                            final SLTextureFetchRequest fetchReq = textureUDPTransfer.fetchReq;
                            this.RunUDPQueue();
                            monitorexit(this);
                            if (fetchReq != null && fetchReq.onFetchComplete != null) {
                                fetchReq.onFetchComplete.OnTextureFetchComplete(fetchReq);
                            }
                            return;
                        }
                    }
                }
                final SLTextureFetchRequest fetchReq = null;
                continue;
            }
        }
    }
    
    @SLMessageHandler
    public void HandleImageNotInDatabase(final ImageNotInDatabase imageNotInDatabase) {
        while (true) {
            while (true) {
                synchronized (this) {
                    Debug.Log("TextureUDP: Image not in database: " + imageNotInDatabase.ImageID_Field.ID);
                    final TextureUDPTransfer textureUDPTransfer = this.udpTransfers.remove(imageNotInDatabase.ImageID_Field.ID);
                    if (textureUDPTransfer != null) {
                        final SLTextureFetchRequest fetchReq = textureUDPTransfer.fetchReq;
                        monitorexit(this);
                        if (fetchReq != null && fetchReq.onFetchComplete != null) {
                            fetchReq.onFetchComplete.OnTextureFetchComplete(fetchReq);
                        }
                        this.RunUDPQueue();
                        return;
                    }
                }
                final SLTextureFetchRequest fetchReq = null;
                continue;
            }
        }
    }
    
    @SLMessageHandler
    public void HandleImagePacket(final ImagePacket imagePacket) {
        while (true) {
            while (true) {
                synchronized (this) {
                    final TextureUDPTransfer textureUDPTransfer = this.udpTransfers.get(imagePacket.ImageID_Field.ID);
                    if (textureUDPTransfer != null) {
                        textureUDPTransfer.HandleImagePacket(imagePacket);
                        if (textureUDPTransfer.isCompleted()) {
                            this.udpTransfers.remove(imagePacket.ImageID_Field.ID);
                            final SLTextureFetchRequest fetchReq = textureUDPTransfer.fetchReq;
                            fetchReq.outputFile = textureUDPTransfer.getOutputFile();
                            this.RunUDPQueue();
                            monitorexit(this);
                            if (fetchReq != null && fetchReq.onFetchComplete != null) {
                                fetchReq.onFetchComplete.OnTextureFetchComplete(fetchReq);
                            }
                            return;
                        }
                    }
                }
                final SLTextureFetchRequest fetchReq = null;
                continue;
            }
        }
    }
    
    @Override
    public void ProcessIdle() {
        Set<Object> set = null;
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis >= this.lastCheckForStalls + 1000L) {
            Iterator<Object> iterator2;
            TextureUDPTransfer textureUDPTransfer;
            SLTextureFetchRequest fetchReq;
            Label_0140_Outer:Label_0157_Outer:
            while (true) {
                this.lastCheckForStalls = currentTimeMillis;
                while (true) {
                    Label_0264: {
                        while (true) {
                            Label_0261: {
                                try {
                                    for (final Map.Entry<K, TextureUDPTransfer> entry : this.udpTransfers.entrySet()) {
                                        if (!entry.getValue().hasStalled() || entry.getValue().RetryTransfer(this.agentCircuit, this.circuitInfo)) {
                                            break Label_0264;
                                        }
                                        Debug.Printf("Cannot retry texture %s", ((UUID)entry.getKey()).toString());
                                        if (set != null) {
                                            break Label_0261;
                                        }
                                        set = (Set<Object>)new HashSet<UUID>();
                                        set.add(entry.getKey());
                                    }
                                    if (set != null) {
                                        iterator2 = set.iterator();
                                        while (iterator2.hasNext()) {
                                            textureUDPTransfer = this.udpTransfers.remove(iterator2.next());
                                            if (textureUDPTransfer != null) {
                                                fetchReq = textureUDPTransfer.fetchReq;
                                                fetchReq.outputFile = null;
                                                if (fetchReq.onFetchComplete == null) {
                                                    continue Label_0140_Outer;
                                                }
                                                fetchReq.onFetchComplete.OnTextureFetchComplete(fetchReq);
                                            }
                                        }
                                        break;
                                    }
                                }
                                catch (final ConcurrentModificationException ex) {
                                    Debug.Warning(ex);
                                }
                                return;
                            }
                            continue Label_0157_Outer;
                        }
                    }
                    continue;
                }
            }
            this.RunUDPQueue();
        }
    }
    
    public void StopFetching() {
        this.udpQueue.clear();
    }
    
    public void UpdatePriority(final SLTextureFetchRequest slTextureFetchRequest) {
        this.udpQueue.updatePriority(slTextureFetchRequest);
    }
    
    public String getAgentAppearanceService() {
        return this.agentAppearanceService;
    }
    
    public String getCapURL() {
        return this.capURL;
    }
}
