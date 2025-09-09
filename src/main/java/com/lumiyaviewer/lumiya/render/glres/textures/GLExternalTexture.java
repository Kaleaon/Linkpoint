package com.lumiyaviewer.lumiya.render.glres.textures;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.opengl.GLES11;
import android.view.Surface;

@TargetApi(15)
public class GLExternalTexture {
    private final int handle;
    private final int height;
    private final OnFrameAvailableListener onFrameAvailableListener = new OnFrameAvailableListener() {
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        }
    };
    private final Surface surface;
    private final SurfaceTexture surfaceTexture;
    private final int width;

    @TargetApi(15)
    public GLExternalTexture(int i, int i2) {
        this.width = i;
        this.height = i2;
        int[] iArr = new int[1];
        GLES11.glGenTextures(1, iArr, 0);
        this.handle = iArr[0];
        bind();
        GLES11.glTexImage2D(36197, 0, 6408, i, i2, 0, 6408, 5121, null);
        GLES11.glTexParameteri(36197, 10241, 9729);
        GLES11.glTexParameteri(36197, 10240, 9729);
        this.surfaceTexture = new SurfaceTexture(this.handle);
        this.surfaceTexture.setDefaultBufferSize(i, i2);
        this.surfaceTexture.setOnFrameAvailableListener(this.onFrameAvailableListener);
        this.surface = new Surface(this.surfaceTexture);
    }

    @TargetApi(15)
    public void bind() {
        GLES11.glBindTexture(36197, this.handle);
    }

    public Canvas getCanvas() {
        return this.surface.lockCanvas(null);
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public void postCanvas(Canvas canvas) {
        this.surface.unlockCanvasAndPost(canvas);
    }

    @TargetApi(15)
    public void release() {
        int[] iArr = new int[1];
        this.surface.release();
        this.surfaceTexture.release();
        iArr[0] = this.handle;
        GLES11.glDeleteTextures(1, iArr, 0);
    }

    @TargetApi(11)
    public void update(float[] fArr) {
        this.surfaceTexture.updateTexImage();
        this.surfaceTexture.getTransformMatrix(fArr);
    }
}
