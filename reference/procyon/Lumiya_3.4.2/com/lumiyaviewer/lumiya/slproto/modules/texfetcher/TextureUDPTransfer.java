// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.texfetcher;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.slproto.messages.RequestImage;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.messages.ImagePacket;
import com.lumiyaviewer.lumiya.slproto.messages.ImageData;
import java.io.IOException;
import com.lumiyaviewer.lumiya.Debug;
import java.util.HashMap;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Map;

public class TextureUDPTransfer
{
    private static final int MAX_RETRIES = 2;
    private static final long PACKET_TIMEOUT = 15000L;
    private boolean completed;
    public SLTextureFetchRequest fetchReq;
    private int gotSize;
    private boolean headerReceived;
    private long lastReceivedPacket;
    private int nextExpectedPacket;
    private Map<Integer, byte[]> outOfOrderPackets;
    private File outputFile;
    private FileOutputStream outputStream;
    private int packets;
    private int retries;
    private int size;
    
    public TextureUDPTransfer(final File outputFile, final SLTextureFetchRequest fetchReq) {
        this.completed = false;
        this.headerReceived = false;
        this.nextExpectedPacket = 0;
        this.gotSize = 0;
        this.retries = 0;
        this.lastReceivedPacket = 0L;
        this.outOfOrderPackets = new HashMap<Integer, byte[]>();
        this.fetchReq = fetchReq;
        this.outputFile = outputFile;
    }
    
    private void HandleDataPacket(final int i, byte[] array) {
        this.lastReceivedPacket = System.currentTimeMillis();
        if (this.headerReceived && this.nextExpectedPacket == i) {
            this.HandleNextDataPacket(array);
            while (true) {
                array = this.outOfOrderPackets.remove(this.nextExpectedPacket);
                if (array == null) {
                    break;
                }
                this.HandleNextDataPacket(array);
            }
        }
        else {
            this.outOfOrderPackets.put(i, array);
        }
    }
    
    private void HandleNextDataPacket(final byte[] b) {
        this.lastReceivedPacket = System.currentTimeMillis();
        try {
            if (this.nextExpectedPacket == 0 && this.outputStream == null) {
                this.outputStream = new FileOutputStream(this.outputFile);
            }
            if (this.outputStream != null) {
                this.outputStream.write(b);
            }
            this.gotSize += b.length;
            if (this.gotSize >= this.size) {
                this.outputStream.close();
                this.completed = true;
                Debug.Log("TextureUDP: completed download, size = " + this.gotSize);
            }
            ++this.nextExpectedPacket;
        }
        catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void HandleImageData(final ImageData imageData) {
        this.lastReceivedPacket = System.currentTimeMillis();
        this.headerReceived = true;
        this.size = imageData.ImageID_Field.Size;
        this.packets = imageData.ImageID_Field.Packets;
        Debug.Log("TextureUDP: header received, size = " + this.size + ", packets = " + this.packets + ", initial size = " + imageData.ImageDataData_Field.Data.length);
        this.HandleDataPacket(0, imageData.ImageDataData_Field.Data);
    }
    
    public void HandleImagePacket(final ImagePacket imagePacket) {
        this.HandleDataPacket(imagePacket.ImageID_Field.Packet, imagePacket.ImageData_Field.Data);
    }
    
    public boolean RetryTransfer(final SLAgentCircuit slAgentCircuit, final SLCircuitInfo slCircuitInfo) {
        ++this.retries;
        if (this.retries > 2) {
            return false;
        }
        this.StartTransfer(slAgentCircuit, slCircuitInfo);
        return true;
    }
    
    public void StartTransfer(final SLAgentCircuit slAgentCircuit, final SLCircuitInfo slCircuitInfo) {
        int type = 0;
        Debug.Log("TextureUDP: starting transfer, image ID = " + this.fetchReq.textureID);
        this.lastReceivedPacket = System.currentTimeMillis();
        final RequestImage requestImage = new RequestImage();
        requestImage.AgentData_Field.AgentID = slCircuitInfo.agentID;
        requestImage.AgentData_Field.SessionID = slCircuitInfo.sessionID;
        final RequestImage.RequestImageData e = new RequestImage.RequestImageData();
        e.Image = this.fetchReq.textureID;
        e.DiscardLevel = 0;
        e.DownloadPriority = 1013000.0f;
        e.Packet = 0;
        if (this.fetchReq.textureClass == TextureClass.Baked) {
            type = 1;
        }
        e.Type = type;
        requestImage.RequestImageData_Fields.add(e);
        requestImage.isReliable = true;
        slAgentCircuit.SendMessage(requestImage);
    }
    
    public File getOutputFile() {
        return this.outputFile;
    }
    
    public boolean hasStalled() {
        return System.currentTimeMillis() > this.lastReceivedPacket + 15000L;
    }
    
    public boolean isCompleted() {
        return this.completed;
    }
}
