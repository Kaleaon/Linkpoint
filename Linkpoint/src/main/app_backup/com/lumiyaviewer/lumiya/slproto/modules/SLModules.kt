package com.lumiyaviewer.lumiya.slproto.modules

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.SLGridConnection
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher

class SLModules(circuit: SLAgentCircuit, caps: SLCaps, grid: SLGridConnection) {
    val textureFetcher: SLTextureFetcher? = null

    fun handleCircuitReady() {
        // Stub
    }

    fun handleCloseCircuit() {
        // Stub
    }
}
