package com.lumiyaviewer.lumiya.slproto.modules.texfetcher

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.messages.ImageData
import com.lumiyaviewer.lumiya.slproto.messages.ImageNotInDatabase
import com.lumiyaviewer.lumiya.slproto.messages.ImagePacket
import com.lumiyaviewer.lumiya.slproto.modules.SLIdleHandler
import com.lumiyaviewer.lumiya.slproto.modules.SLModule
import java.io.File
import java.util.UUID

class SLTextureFetcher(
    agentCircuit: SLAgentCircuit,
    caps: SLCaps,
    val agentAppearanceService: String?
) : SLModule(agentCircuit), SLIdleHandler {

    companion object {
        private var instance: SLTextureFetcher? = null
        fun getInstance(): SLTextureFetcher? = instance
    }

    private val MAX_UDP_TRANSFERS: Int = 2
    val capURL: String? = caps.getCapability(SLCaps.SLCapability.GetTexture)
    private var lastCheckForStalls: Long = 0

    init {
        instance = this
        Debug.Log("TextureFetcher: capURL = $capURL")
    }

    fun BeginFetch(request: SLTextureFetchRequest) {
        // Stub
    }

    fun CancelFetch(request: SLTextureFetchRequest) {
        // Stub
    }

    override fun HandleCloseCircuit() {
        StopFetching()
        super.HandleCloseCircuit()
    }

    @SLMessageHandler
    fun HandleImageData(imageData: ImageData) {
        // Stub
    }

    @SLMessageHandler
    fun HandleImageNotInDatabase(imageNotInDatabase: ImageNotInDatabase) {
        // Stub
    }

    @SLMessageHandler
    fun HandleImagePacket(imagePacket: ImagePacket) {
        // Stub
    }

    override fun ProcessIdle() {
        // Stub
    }

    fun StopFetching() {
        // Stub
    }

    fun UpdatePriority(request: SLTextureFetchRequest) {
        // Stub
    }
    
    // Convenience method for ModernTextureManager
    fun fetchTexture(textureId: String): ByteArray? {
        return null
    }
    
    fun fetchTextureToFile(textureId: UUID): File? {
        // Stub
        return null
    }
    
    fun fetchTextureViaUDP(textureId: UUID): File? {
        // Stub
        return null
    }
}
