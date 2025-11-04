// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.text;

import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;

public class DrawableTextCache extends ResourceMemoryCache<DrawableTextParams, DrawableTextBitmap>
{
    private final int fontSize;
    
    public DrawableTextCache(final int fontSize) {
        this.fontSize = fontSize;
    }
    
    @Override
    protected ResourceRequest<DrawableTextParams, DrawableTextBitmap> CreateNewRequest(final DrawableTextParams drawableTextParams, final ResourceManager<DrawableTextParams, DrawableTextBitmap> resourceManager) {
        return new TextGenRequest(drawableTextParams, this.fontSize, resourceManager);
    }
    
    private static class TextGenRequest extends ResourceRequest<DrawableTextParams, DrawableTextBitmap> implements Runnable
    {
        private final int fontSize;
        
        TextGenRequest(final DrawableTextParams drawableTextParams, final int fontSize, final ResourceManager<DrawableTextParams, DrawableTextBitmap> resourceManager) {
            super(drawableTextParams, resourceManager);
            this.fontSize = fontSize;
        }
        
        @Override
        public void execute() {
            PrimComputeExecutor.getInstance().execute(this);
        }
        
        @Override
        public void run() {
            ((ResourceRequest<ResourceParams, DrawableTextBitmap>)this).completeRequest(new DrawableTextBitmap(((ResourceRequest<DrawableTextParams, ResourceType>)this).getParams(), this.fontSize));
        }
    }
}
