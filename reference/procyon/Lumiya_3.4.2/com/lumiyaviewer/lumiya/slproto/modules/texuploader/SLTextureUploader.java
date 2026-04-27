// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.texuploader;

import java.util.concurrent.Executors;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.concurrent.ExecutorService;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;

public class SLTextureUploader extends SLModule
{
    private String capURL;
    private ExecutorService executor;
    
    public SLTextureUploader(final SLAgentCircuit slAgentCircuit, final SLCaps slCaps) {
        super(slAgentCircuit);
        this.capURL = slCaps.getCapability(SLCaps.SLCapability.UploadBakedTexture);
        if (this.capURL != null) {
            this.executor = Executors.newSingleThreadExecutor();
        }
    }
    
    public void BeginUpload(final SLTextureUploadRequest slTextureUploadRequest) {
        if (this.executor != null && this.capURL != null) {
            slTextureUploadRequest.setCapURL(this.capURL);
            this.executor.execute(slTextureUploadRequest);
        }
    }
    
    @Override
    public void HandleCloseCircuit() {
        if (this.executor != null) {
            this.executor.shutdownNow();
            this.executor = null;
        }
        super.HandleCloseCircuit();
    }
}
