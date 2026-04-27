// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.texfetcher;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.tex.TexturePriority;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.messages.ImageData;
import com.lumiyaviewer.lumiya.slproto.messages.ImageNotInDatabase;
import com.lumiyaviewer.lumiya.slproto.messages.ImagePacket;
import com.lumiyaviewer.lumiya.slproto.modules.SLIdleHandler;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;
import com.lumiyaviewer.lumiya.utils.PriorityBinQueue;
import java.io.File;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.texfetcher:
//            SLTextureFetchRequest, TextureUDPTransfer

public class SLTextureFetcher extends SLModule
    implements SLIdleHandler
{

    private static final int MAX_UDP_TRANSFERS = 2;
    private String agentAppearanceService;
    private String capURL;
    private long lastCheckForStalls;
    private PriorityBinQueue udpQueue;
    private Map udpTransfers;

    public SLTextureFetcher(SLAgentCircuit slagentcircuit, SLCaps slcaps, String s)
    {
        super(slagentcircuit);
        capURL = null;
        agentAppearanceService = null;
        udpTransfers = new ConcurrentHashMap();
        udpQueue = new PriorityBinQueue(TexturePriority.values().length);
        lastCheckForStalls = 0L;
        agentAppearanceService = s;
        capURL = slcaps.getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.GetTexture);
        Debug.Log((new StringBuilder()).append("TextureFetcher: capURL = ").append(capURL).toString());
    }

    private void RunUDPQueue()
    {
        this;
        JVM INSTR monitorenter ;
        SLTextureFetchRequest sltexturefetchrequest;
        if (udpTransfers.size() >= 2)
        {
            break MISSING_BLOCK_LABEL_70;
        }
        sltexturefetchrequest = (SLTextureFetchRequest)udpQueue.poll();
        if (sltexturefetchrequest == null)
        {
            break MISSING_BLOCK_LABEL_70;
        }
        TextureUDPTransfer textureudptransfer = new TextureUDPTransfer(sltexturefetchrequest.destFile, sltexturefetchrequest);
        udpTransfers.put(sltexturefetchrequest.textureID, textureudptransfer);
        textureudptransfer.StartTransfer(agentCircuit, circuitInfo);
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void BeginFetch(SLTextureFetchRequest sltexturefetchrequest)
    {
        SLTextureFetchRequest sltexturefetchrequest1 = null;
        this;
        JVM INSTR monitorenter ;
        File file = sltexturefetchrequest.destFile;
        if (!file.exists()) goto _L2; else goto _L1
_L1:
        sltexturefetchrequest.outputFile = file;
        sltexturefetchrequest1 = sltexturefetchrequest;
_L4:
        this;
        JVM INSTR monitorexit ;
        if (sltexturefetchrequest1 != null && sltexturefetchrequest1.onFetchComplete != null)
        {
            sltexturefetchrequest1.onFetchComplete.OnTextureFetchComplete(sltexturefetchrequest);
        }
        return;
_L2:
        udpQueue.add(sltexturefetchrequest);
        RunUDPQueue();
        if (true) goto _L4; else goto _L3
_L3:
        sltexturefetchrequest;
        throw sltexturefetchrequest;
    }

    public void CancelFetch(SLTextureFetchRequest sltexturefetchrequest)
    {
        this;
        JVM INSTR monitorenter ;
        udpQueue.remove(sltexturefetchrequest);
        udpTransfers.remove(sltexturefetchrequest.textureID);
        RunUDPQueue();
        this;
        JVM INSTR monitorexit ;
        return;
        sltexturefetchrequest;
        throw sltexturefetchrequest;
    }

    public void HandleCloseCircuit()
    {
        StopFetching();
        super.HandleCloseCircuit();
    }

    public void HandleImageData(ImageData imagedata)
    {
        this;
        JVM INSTR monitorenter ;
        TextureUDPTransfer textureudptransfer = (TextureUDPTransfer)udpTransfers.get(imagedata.ImageID_Field.ID);
        if (textureudptransfer == null) goto _L2; else goto _L1
_L1:
        textureudptransfer.HandleImageData(imagedata);
        if (!textureudptransfer.isCompleted()) goto _L2; else goto _L3
_L3:
        udpTransfers.remove(imagedata.ImageID_Field.ID);
        imagedata = textureudptransfer.fetchReq;
        RunUDPQueue();
_L5:
        this;
        JVM INSTR monitorexit ;
        if (imagedata != null && ((SLTextureFetchRequest) (imagedata)).onFetchComplete != null)
        {
            ((SLTextureFetchRequest) (imagedata)).onFetchComplete.OnTextureFetchComplete(imagedata);
        }
        return;
        imagedata;
        throw imagedata;
_L2:
        imagedata = null;
        if (true) goto _L5; else goto _L4
_L4:
    }

    public void HandleImageNotInDatabase(ImageNotInDatabase imagenotindatabase)
    {
        this;
        JVM INSTR monitorenter ;
        Debug.Log((new StringBuilder()).append("TextureUDP: Image not in database: ").append(imagenotindatabase.ImageID_Field.ID).toString());
        imagenotindatabase = (TextureUDPTransfer)udpTransfers.remove(imagenotindatabase.ImageID_Field.ID);
        if (imagenotindatabase == null) goto _L2; else goto _L1
_L1:
        imagenotindatabase = ((TextureUDPTransfer) (imagenotindatabase)).fetchReq;
_L4:
        this;
        JVM INSTR monitorexit ;
        if (imagenotindatabase != null && ((SLTextureFetchRequest) (imagenotindatabase)).onFetchComplete != null)
        {
            ((SLTextureFetchRequest) (imagenotindatabase)).onFetchComplete.OnTextureFetchComplete(imagenotindatabase);
        }
        RunUDPQueue();
        return;
        imagenotindatabase;
        throw imagenotindatabase;
_L2:
        imagenotindatabase = null;
        if (true) goto _L4; else goto _L3
_L3:
    }

    public void HandleImagePacket(ImagePacket imagepacket)
    {
        this;
        JVM INSTR monitorenter ;
        TextureUDPTransfer textureudptransfer = (TextureUDPTransfer)udpTransfers.get(imagepacket.ImageID_Field.ID);
        if (textureudptransfer == null) goto _L2; else goto _L1
_L1:
        textureudptransfer.HandleImagePacket(imagepacket);
        if (!textureudptransfer.isCompleted()) goto _L2; else goto _L3
_L3:
        udpTransfers.remove(imagepacket.ImageID_Field.ID);
        imagepacket = textureudptransfer.fetchReq;
        imagepacket.outputFile = textureudptransfer.getOutputFile();
        RunUDPQueue();
_L5:
        this;
        JVM INSTR monitorexit ;
        if (imagepacket != null && ((SLTextureFetchRequest) (imagepacket)).onFetchComplete != null)
        {
            ((SLTextureFetchRequest) (imagepacket)).onFetchComplete.OnTextureFetchComplete(imagepacket);
        }
        return;
        imagepacket;
        throw imagepacket;
_L2:
        imagepacket = null;
        if (true) goto _L5; else goto _L4
_L4:
    }

    public void ProcessIdle()
    {
        Object obj;
        long l;
        obj = null;
        l = System.currentTimeMillis();
        if (l < lastCheckForStalls + 1000L) goto _L2; else goto _L1
_L1:
        lastCheckForStalls = l;
        Object obj1 = udpTransfers.entrySet().iterator();
_L3:
        java.util.Map.Entry entry;
        do
        {
            if (!((Iterator) (obj1)).hasNext())
            {
                break MISSING_BLOCK_LABEL_155;
            }
            entry = (java.util.Map.Entry)((Iterator) (obj1)).next();
        } while (!((TextureUDPTransfer)entry.getValue()).hasStalled() || ((TextureUDPTransfer)entry.getValue()).RetryTransfer(agentCircuit, circuitInfo));
        Debug.Printf("Cannot retry texture %s", new Object[] {
            ((UUID)entry.getKey()).toString()
        });
        if (obj != null)
        {
            break MISSING_BLOCK_LABEL_136;
        }
        obj = new HashSet();
        ((Set) (obj)).add((UUID)entry.getKey());
          goto _L3
        if (obj == null) goto _L2; else goto _L4
_L4:
        obj = ((Iterable) (obj)).iterator();
_L8:
        if (!((Iterator) (obj)).hasNext()) goto _L6; else goto _L5
_L5:
        obj1 = (UUID)((Iterator) (obj)).next();
        obj1 = (TextureUDPTransfer)udpTransfers.remove(obj1);
        if (obj1 == null) goto _L8; else goto _L7
_L7:
        ConcurrentModificationException concurrentmodificationexception;
        obj1 = ((TextureUDPTransfer) (obj1)).fetchReq;
        obj1.outputFile = null;
        if (((SLTextureFetchRequest) (obj1)).onFetchComplete != null)
        {
            ((SLTextureFetchRequest) (obj1)).onFetchComplete.OnTextureFetchComplete(((SLTextureFetchRequest) (obj1)));
        }
          goto _L8
_L2:
        return;
_L6:
        try
        {
            RunUDPQueue();
            return;
        }
        // Misplaced declaration of an exception variable
        catch (ConcurrentModificationException concurrentmodificationexception)
        {
            Debug.Warning(concurrentmodificationexception);
        }
        if (true) goto _L2; else goto _L9
_L9:
    }

    public void StopFetching()
    {
        udpQueue.clear();
    }

    public void UpdatePriority(SLTextureFetchRequest sltexturefetchrequest)
    {
        udpQueue.updatePriority(sltexturefetchrequest);
    }

    public String getAgentAppearanceService()
    {
        return agentAppearanceService;
    }

    public String getCapURL()
    {
        return capURL;
    }
}
