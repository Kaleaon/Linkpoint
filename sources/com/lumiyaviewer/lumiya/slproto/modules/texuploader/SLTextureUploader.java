// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.texuploader;

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.texuploader:
//            SLTextureUploadRequest

public class SLTextureUploader extends SLModule
{

    private String capURL;
    private ExecutorService executor;

    public SLTextureUploader(SLAgentCircuit slagentcircuit, SLCaps slcaps)
    {
        super(slagentcircuit);
        capURL = slcaps.getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.UploadBakedTexture);
        if (capURL != null)
        {
            executor = Executors.newSingleThreadExecutor();
        }
    }

    public void BeginUpload(SLTextureUploadRequest sltextureuploadrequest)
    {
        if (executor != null && capURL != null)
        {
            sltextureuploadrequest.setCapURL(capURL);
            executor.execute(sltextureuploadrequest);
        }
    }

    public void HandleCloseCircuit()
    {
        if (executor != null)
        {
            executor.shutdownNow();
            executor = null;
        }
        super.HandleCloseCircuit();
    }
}
