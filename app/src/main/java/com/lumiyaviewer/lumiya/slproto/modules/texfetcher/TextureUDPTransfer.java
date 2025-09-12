package com.lumiyaviewer.lumiya.slproto.modules.texfetcher;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.messages.ImageData;
import com.lumiyaviewer.lumiya.slproto.messages.ImagePacket;
import com.lumiyaviewer.lumiya.slproto.messages.RequestImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextureUDPTransfer {
    private static final int MAX_RETRIES = 2;
    private static final long PACKET_TIMEOUT = 15000;
    private boolean completed = false;
    public SLTextureFetchRequest fetchReq;
    private int gotSize = 0;
    private boolean headerReceived = false;
    private long lastReceivedPacket = 0;
    private int nextExpectedPacket = 0;
    private Map<Integer, byte[]> outOfOrderPackets = new HashMap();
    private File outputFile;
    private FileOutputStream outputStream;
    private int packets;
    private int retries = 0;
    private int size;

    public TextureUDPTransfer(File file, SLTextureFetchRequest sLTextureFetchRequest) {
        this.fetchReq = sLTextureFetchRequest;
        this.outputFile = file;
    }

    private void HandleDataPacket(int i, byte[] bArr) {
        this.lastReceivedPacket = System.currentTimeMillis();
        if (!this.headerReceived || this.nextExpectedPacket != i) {
            this.outOfOrderPackets.put(Integer.valueOf(i), bArr);
            return;
        }
        HandleNextDataPacket(bArr);
        while (true) {
            byte[] remove = this.outOfOrderPackets.remove(Integer.valueOf(this.nextExpectedPacket));
            if (remove != null) {
                HandleNextDataPacket(remove);
            } else {
                return;
            }
        }
    }

    private void HandleNextDataPacket(byte[] bArr) {
        this.lastReceivedPacket = System.currentTimeMillis();
        try {
            if (this.nextExpectedPacket == 0 && this.outputStream == null) {
                this.outputStream = new FileOutputStream(this.outputFile);
            }
            if (this.outputStream != null) {
                this.outputStream.write(bArr);
            }
            this.gotSize += bArr.length;
            if (this.gotSize >= this.size) {
                this.outputStream.close();
                this.completed = true;
                Debug.Log("TextureUDP: completed download, size = " + this.gotSize);
            }
            this.nextExpectedPacket++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void HandleImageData(ImageData imageData) {
        this.lastReceivedPacket = System.currentTimeMillis();
        this.headerReceived = true;
        this.size = imageData.ImageID_Field.Size;
        this.packets = imageData.ImageID_Field.Packets;
        Debug.Log("TextureUDP: header received, size = " + this.size + ", packets = " + this.packets + ", initial size = " + imageData.ImageDataData_Field.Data.length);
        HandleDataPacket(0, imageData.ImageDataData_Field.Data);
    }

    public void HandleImagePacket(ImagePacket imagePacket) {
        HandleDataPacket(imagePacket.ImageID_Field.Packet, imagePacket.ImageData_Field.Data);
    }

    public boolean RetryTransfer(SLAgentCircuit sLAgentCircuit, SLCircuitInfo sLCircuitInfo) {
        this.retries++;
        if (this.retries > 2) {
            return false;
        }
        StartTransfer(sLAgentCircuit, sLCircuitInfo);
        return true;
    }

    public void StartTransfer(SLAgentCircuit sLAgentCircuit, SLCircuitInfo sLCircuitInfo) {
        int i = 0;
        Debug.Log("TextureUDP: starting transfer, image ID = " + this.fetchReq.textureID);
        this.lastReceivedPacket = System.currentTimeMillis();
        RequestImage requestImage = new RequestImage();
        requestImage.AgentData_Field.AgentID = sLCircuitInfo.agentID;
        requestImage.AgentData_Field.SessionID = sLCircuitInfo.sessionID;
        RequestImage.RequestImageData requestImageData = new RequestImage.RequestImageData();
        requestImageData.Image = this.fetchReq.textureID;
        requestImageData.DiscardLevel = 0;
        requestImageData.DownloadPriority = 1013000.0f;
        requestImageData.Packet = 0;
        if (this.fetchReq.textureClass == TextureClass.Baked) {
            i = 1;
        }
        requestImageData.Type = i;
        requestImage.RequestImageData_Fields.add(requestImageData);
        requestImage.isReliable = true;
        sLAgentCircuit.SendMessage(requestImage);
    }

    public File getOutputFile() {
        return this.outputFile;
    }

    public boolean hasStalled() {
        return System.currentTimeMillis() > this.lastReceivedPacket + PACKET_TIMEOUT;
    }

    public boolean isCompleted() {
        return this.completed;
    }
}
