package com.linkpoint.slproto.modules.texfetcher

import com.linkpoint.Debug
import com.linkpoint.render.tex.TextureClass
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLCircuitInfo
import com.linkpoint.slproto.messages.ImageData
import com.linkpoint.slproto.messages.ImagePacket
import com.linkpoint.slproto.messages.RequestImage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.HashMap
import java.util.Map

class TextureUDPTransfer {
    private val MAX_RETRIES: Int = 2
    private val PACKET_TIMEOUT: Long = 15000
    private Boolean completed = false
    SLTextureFetchRequest fetchReq
    private Int gotSize = 0
    private Boolean headerReceived = false
    private Long lastReceivedPacket = 0
    private Int nextExpectedPacket = 0
    private Map<Int, ByteArray> outOfOrderPackets = HashMap()
    private File outputFile
    private FileOutputStream outputStream
    private Int packets
    private Int retries = 0
    private Int size

    TextureUDPTransfer(File file, SLTextureFetchRequest sLTextureFetchRequest) {
        this.fetchReq = sLTextureFetchRequest
        this.outputFile = file
    }

    private Unit HandleDataPacket(Int i, ByteArray bArr) {
        this.lastReceivedPacket = System.currentTimeMillis()
        if (!this.headerReceived || this.nextExpectedPacket != i) {
            this.outOfOrderPackets.put(Int.valueOf(i), bArr)
            return
        }
        HandleNextDataPacket(bArr)
        while (true) {
            ByteArray remove = this.outOfOrderPackets.remove(Int.valueOf(this.nextExpectedPacket))
            if (remove != null) {
                HandleNextDataPacket(remove)
            } else {
                return
            }
        }
    }

    private Unit HandleNextDataPacket(ByteArray bArr) {
        this.lastReceivedPacket = System.currentTimeMillis()
        try {
            if (this.nextExpectedPacket == 0 && this.outputStream == null) {
                this.outputStream = FileOutputStream(this.outputFile)
            }
            if (this.outputStream != null) {
                this.outputStream.write(bArr)
            }
            this.gotSize += bArr.length
            if (this.gotSize >= this.size) {
                this.outputStream.close()
                this.completed = true
                Debug.Log("TextureUDP: completed download, size = " + this.gotSize)
            }
            this.nextExpectedPacket++
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    fun HandleImageData(ImageData imageData): Unit {
        this.lastReceivedPacket = System.currentTimeMillis()
        this.headerReceived = true
        this.size = imageData.ImageID_Field.Size
        this.packets = imageData.ImageID_Field.Packets
        Debug.Log("TextureUDP: header received, size = " + this.size + ", packets = " + this.packets + ", initial size = " + imageData.ImageDataData_Field.Data.length)
        HandleDataPacket(0, imageData.ImageDataData_Field.Data)
    }

    fun HandleImagePacket(ImagePacket imagePacket): Unit {
        HandleDataPacket(imagePacket.ImageID_Field.Packet, imagePacket.ImageData_Field.Data)
    }

    fun RetryTransfer(SLAgentCircuit sLAgentCircuit, SLCircuitInfo sLCircuitInfo): Boolean {
        this.retries++
        if (this.retries > 2) {
            return false
        }
        StartTransfer(sLAgentCircuit, sLCircuitInfo)
        return true
    }

    fun StartTransfer(SLAgentCircuit sLAgentCircuit, SLCircuitInfo sLCircuitInfo): Unit {
        Int i = 0
        Debug.Log("TextureUDP: starting transfer, image ID = " + this.fetchReq.textureID)
        this.lastReceivedPacket = System.currentTimeMillis()
        RequestImage requestImage = RequestImage()
        requestImage.AgentData_Field.AgentID = sLCircuitInfo.agentID
        requestImage.AgentData_Field.SessionID = sLCircuitInfo.sessionID
        RequestImage.RequestImageData requestImageData = RequestImage.RequestImageData()
        requestImageData.Image = this.fetchReq.textureID
        requestImageData.DiscardLevel = 0
        requestImageData.DownloadPriority = 1013000.0f
        requestImageData.Packet = 0
        if (this.fetchReq.textureClass == TextureClass.Baked) {
            i = 1
        }
        requestImageData.Type = i
        requestImage.RequestImageData_Fields.add(requestImageData)
        requestImage.isReliable = true
        sLAgentCircuit.SendMessage(requestImage)
    }

    fun getOutputFile(): File {
        return this.outputFile
    }

    fun hasStalled(): Boolean {
        return System.currentTimeMillis() > this.lastReceivedPacket + PACKET_TIMEOUT
    }

    fun isCompleted(): Boolean {
        return this.completed
    }
}
