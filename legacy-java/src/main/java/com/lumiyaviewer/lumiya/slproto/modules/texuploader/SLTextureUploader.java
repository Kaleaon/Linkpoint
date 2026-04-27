package com.lumiyaviewer.lumiya.slproto.modules.texuploader;

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes.dex */
public class SLTextureUploader extends SLModule {
    private String capURL;
    private ExecutorService executor;

    public SLTextureUploader(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps) {
        super(sLAgentCircuit);
        this.capURL = sLCaps.getCapability(SLCaps.SLCapability.UploadBakedTexture);
        if (this.capURL != null) {
            this.executor = Executors.newSingleThreadExecutor();
        }
    }

    public void BeginUpload(SLTextureUploadRequest sLTextureUploadRequest) {
        if (this.executor == null || this.capURL == null) {
            return;
        }
        sLTextureUploadRequest.setCapURL(this.capURL);
        this.executor.execute(sLTextureUploadRequest);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.modules.SLModule
    public void HandleCloseCircuit() {
        if (this.executor != null) {
            this.executor.shutdownNow();
            this.executor = null;
        }
        super.HandleCloseCircuit();
    }
}
