// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.texfetcher:
//            SLTextureFetchRequest

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
    private Map outOfOrderPackets;
    private File outputFile;
    private FileOutputStream outputStream;
    private int packets;
    private int retries;
    private int size;

    public TextureUDPTransfer(File file, SLTextureFetchRequest sltexturefetchrequest)
    {
        completed = false;
        headerReceived = false;
        nextExpectedPacket = 0;
        gotSize = 0;
        retries = 0;
        lastReceivedPacket = 0L;
        outOfOrderPackets = new HashMap();
        fetchReq = sltexturefetchrequest;
        outputFile = file;
    }

    private void HandleDataPacket(int i, byte abyte0[])
    {
        lastReceivedPacket = System.currentTimeMillis();
        if (headerReceived && nextExpectedPacket == i)
        {
            HandleNextDataPacket(abyte0);
            do
            {
                abyte0 = (byte[])outOfOrderPackets.remove(Integer.valueOf(nextExpectedPacket));
                if (abyte0 == null)
                {
                    break;
                }
                HandleNextDataPacket(abyte0);
            } while (true);
        } else
        {
            outOfOrderPackets.put(Integer.valueOf(i), abyte0);
        }
    }

    private void HandleNextDataPacket(byte abyte0[])
    {
        lastReceivedPacket = System.currentTimeMillis();
        try
        {
            if (nextExpectedPacket == 0 && outputStream == null)
            {
                outputStream = new FileOutputStream(outputFile);
            }
            if (outputStream != null)
            {
                outputStream.write(abyte0);
            }
            gotSize = gotSize + abyte0.length;
            if (gotSize >= size)
            {
                outputStream.close();
                completed = true;
                Debug.Log((new StringBuilder()).append("TextureUDP: completed download, size = ").append(gotSize).toString());
            }
            nextExpectedPacket = nextExpectedPacket + 1;
            return;
        }
        // Misplaced declaration of an exception variable
        catch (byte abyte0[])
        {
            abyte0.printStackTrace();
        }
    }

    public void HandleImageData(ImageData imagedata)
    {
        lastReceivedPacket = System.currentTimeMillis();
        headerReceived = true;
        size = imagedata.ImageID_Field.Size;
        packets = imagedata.ImageID_Field.Packets;
        Debug.Log((new StringBuilder()).append("TextureUDP: header received, size = ").append(size).append(", packets = ").append(packets).append(", initial size = ").append(imagedata.ImageDataData_Field.Data.length).toString());
        HandleDataPacket(0, imagedata.ImageDataData_Field.Data);
    }

    public void HandleImagePacket(ImagePacket imagepacket)
    {
        HandleDataPacket(imagepacket.ImageID_Field.Packet, imagepacket.ImageData_Field.Data);
    }

    public boolean RetryTransfer(SLAgentCircuit slagentcircuit, SLCircuitInfo slcircuitinfo)
    {
        retries = retries + 1;
        if (retries > 2)
        {
            return false;
        } else
        {
            StartTransfer(slagentcircuit, slcircuitinfo);
            return true;
        }
    }

    public void StartTransfer(SLAgentCircuit slagentcircuit, SLCircuitInfo slcircuitinfo)
    {
        int i = 0;
        Debug.Log((new StringBuilder()).append("TextureUDP: starting transfer, image ID = ").append(fetchReq.textureID).toString());
        lastReceivedPacket = System.currentTimeMillis();
        RequestImage requestimage = new RequestImage();
        requestimage.AgentData_Field.AgentID = slcircuitinfo.agentID;
        requestimage.AgentData_Field.SessionID = slcircuitinfo.sessionID;
        slcircuitinfo = new com.lumiyaviewer.lumiya.slproto.messages.RequestImage.RequestImageData();
        slcircuitinfo.Image = fetchReq.textureID;
        slcircuitinfo.DiscardLevel = 0;
        slcircuitinfo.DownloadPriority = 1013000F;
        slcircuitinfo.Packet = 0;
        if (fetchReq.textureClass == TextureClass.Baked)
        {
            i = 1;
        }
        slcircuitinfo.Type = i;
        requestimage.RequestImageData_Fields.add(slcircuitinfo);
        requestimage.isReliable = true;
        slagentcircuit.SendMessage(requestimage);
    }

    public File getOutputFile()
    {
        return outputFile;
    }

    public boolean hasStalled()
    {
        return System.currentTimeMillis() > lastReceivedPacket + 15000L;
    }

    public boolean isCompleted()
    {
        return completed;
    }
}
