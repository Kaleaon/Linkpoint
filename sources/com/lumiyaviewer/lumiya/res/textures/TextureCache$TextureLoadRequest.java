// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.textures;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor;
import com.lumiyaviewer.lumiya.res.executors.Startable;
import com.lumiyaviewer.lumiya.res.executors.StartingExecutor;
import java.io.File;
import java.io.IOException;

// Referenced classes of package com.lumiyaviewer.lumiya.res.textures:
//            TextureCache

private class rawFile extends ResourceRequest
    implements Runnable, Startable
{

    private final File rawFile;
    final TextureCache this$0;

    public void cancelRequest()
    {
        TextureCache._2D_get1(TextureCache.this).cancelRequest(this);
        LoaderExecutor.getInstance().remove(this);
        super.cancelRequest();
    }

    public void execute()
    {
        if (((DrawableTextureParams)getParams()).textureClass() == TextureClass.Prim)
        {
            TextureCache._2D_get1(TextureCache.this).queueRequest(this);
            return;
        } else
        {
            LoaderExecutor.getInstance().execute(this);
            return;
        }
    }

    public void run()
    {
        try
        {
            completeRequest(new OpenJPEG(rawFile.getAbsoluteFile(), ((DrawableTextureParams)getParams()).textureClass(), com.lumiyaviewer.lumiya.openjpeg.lass, true));
        }
        catch (IOException ioexception)
        {
            Debug.Warning(ioexception);
            completeRequest(null);
        }
        TextureCache._2D_get1(TextureCache.this).completeRequest(this);
    }

    public void start()
    {
        LoaderExecutor.getInstance().execute(this);
    }

    public (DrawableTextureParams drawabletextureparams, ResourceManager resourcemanager, File file)
    {
        this$0 = TextureCache.this;
        super(drawabletextureparams, resourcemanager);
        rawFile = file;
    }
}
