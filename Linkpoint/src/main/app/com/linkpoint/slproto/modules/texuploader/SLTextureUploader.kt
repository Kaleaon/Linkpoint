package com.linkpoint.slproto.modules.texuploader

import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.caps.SLCaps
import com.linkpoint.slproto.modules.SLModule
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SLTextureUploader : SLModule {
    private String capURL
    private ExecutorService executor

    SLTextureUploader(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps) {
        super(sLAgentCircuit)
        this.capURL = sLCaps.getCapability(SLCaps.SLCapability.UploadBakedTexture)
        if (this.capURL != null) {
            this.executor = Executors.newSingleThreadExecutor()
        }
    }

    Unit BeginUpload(SLTextureUploadRequest sLTextureUploadRequest) {
        if (this.executor != null && this.capURL != null) {
            sLTextureUploadRequest.setCapURL(this.capURL)
            this.executor.execute(sLTextureUploadRequest)
        }
    }

    Unit HandleCloseCircuit() {
        if (this.executor != null) {
            this.executor.shutdownNow()
            this.executor = null
        }
        super.HandleCloseCircuit()
    }
}
