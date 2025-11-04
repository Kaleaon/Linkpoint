// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.glres.textures;

import android.graphics.Rect;
import android.graphics.Canvas;
import java.nio.Buffer;
import android.opengl.GLES11;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.graphics.SurfaceTexture$OnFrameAvailableListener;
import android.annotation.TargetApi;

@TargetApi(15)
public class GLExternalTexture
{
    private final int handle;
    private final int height;
    private final SurfaceTexture$OnFrameAvailableListener onFrameAvailableListener;
    private final Surface surface;
    private final SurfaceTexture surfaceTexture;
    private final int width;
    
    @TargetApi(15)
    public GLExternalTexture(final int width, final int height) {
        this.onFrameAvailableListener = (SurfaceTexture$OnFrameAvailableListener)new SurfaceTexture$OnFrameAvailableListener() {
            public void onFrameAvailable(final SurfaceTexture surfaceTexture) {
            }
        };
        this.width = width;
        this.height = height;
        final int[] array = { 0 };
        GLES11.glGenTextures(1, array, 0);
        this.handle = array[0];
        this.bind();
        GLES11.glTexImage2D(36197, 0, 6408, width, height, 0, 6408, 5121, (Buffer)null);
        GLES11.glTexParameteri(36197, 10241, 9729);
        GLES11.glTexParameteri(36197, 10240, 9729);
        (this.surfaceTexture = new SurfaceTexture(this.handle)).setDefaultBufferSize(width, height);
        this.surfaceTexture.setOnFrameAvailableListener(this.onFrameAvailableListener);
        this.surface = new Surface(this.surfaceTexture);
    }
    
    @TargetApi(15)
    public void bind() {
        GLES11.glBindTexture(36197, this.handle);
    }
    
    public Canvas getCanvas() {
        return this.surface.lockCanvas((Rect)null);
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public void postCanvas(final Canvas canvas) {
        this.surface.unlockCanvasAndPost(canvas);
    }
    
    @TargetApi(15)
    public void release() {
        this.surface.release();
        this.surfaceTexture.release();
        GLES11.glDeleteTextures(1, new int[] { this.handle }, 0);
    }
    
    @TargetApi(11)
    public void update(final float[] array) {
        this.surfaceTexture.updateTexImage();
        this.surfaceTexture.getTransformMatrix(array);
    }
}
