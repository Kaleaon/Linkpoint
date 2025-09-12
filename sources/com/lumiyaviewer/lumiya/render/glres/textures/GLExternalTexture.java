// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres.textures;

import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11;
import android.view.Surface;

public class GLExternalTexture
{

    private final int handle;
    private final int height;
    private final android.graphics.SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener = new android.graphics.SurfaceTexture.OnFrameAvailableListener() {

        final GLExternalTexture this$0;

        public void onFrameAvailable(SurfaceTexture surfacetexture)
        {
        }

            
            {
                this$0 = GLExternalTexture.this;
                super();
            }
    };
    private final Surface surface;
    private final SurfaceTexture surfaceTexture;
    private final int width;

    public GLExternalTexture(int i, int j)
    {
        width = i;
        height = j;
        int ai[] = new int[1];
        GLES11.glGenTextures(1, ai, 0);
        handle = ai[0];
        bind();
        GLES11.glTexImage2D(36197, 0, 6408, i, j, 0, 6408, 5121, null);
        GLES11.glTexParameteri(36197, 10241, 9729);
        GLES11.glTexParameteri(36197, 10240, 9729);
        surfaceTexture = new SurfaceTexture(handle);
        surfaceTexture.setDefaultBufferSize(i, j);
        surfaceTexture.setOnFrameAvailableListener(onFrameAvailableListener);
        surface = new Surface(surfaceTexture);
    }

    public void bind()
    {
        GLES11.glBindTexture(36197, handle);
    }

    public Canvas getCanvas()
    {
        return surface.lockCanvas(null);
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    public void postCanvas(Canvas canvas)
    {
        surface.unlockCanvasAndPost(canvas);
    }

    public void release()
    {
        surface.release();
        surfaceTexture.release();
        GLES11.glDeleteTextures(1, new int[] {
            handle
        }, 0);
    }

    public void update(float af[])
    {
        surfaceTexture.updateTexImage();
        surfaceTexture.getTransformMatrix(af);
    }
}
