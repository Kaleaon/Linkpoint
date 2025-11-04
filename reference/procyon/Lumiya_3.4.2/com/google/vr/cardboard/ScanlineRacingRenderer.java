// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.os.Process;
import javax.microedition.khronos.egl.EGLConfig;
import android.graphics.Point;
import javax.microedition.khronos.opengles.GL10;
import com.google.vr.ndk.base.GvrLayout;
import com.google.vr.ndk.base.GvrSurfaceView;
import com.google.vr.ndk.base.GvrApi;
import android.opengl.GLSurfaceView$Renderer;

public class ScanlineRacingRenderer implements GLSurfaceView$Renderer
{
    private static final String TAG = "ScanlineRacingRenderer";
    private final GvrApi gvrApi;
    private GvrSurfaceView gvrSurfaceView;
    private final ExternalSurfaceManager surfaceManager;
    
    public ScanlineRacingRenderer(final GvrApi gvrApi) {
        if (gvrApi != null) {
            this.gvrApi = gvrApi;
            this.surfaceManager = new ExternalSurfaceManager(gvrApi);
            return;
        }
        throw new IllegalArgumentException("GvrApi must be supplied for proper scanline rendering");
    }
    
    public GvrLayout.ExternalSurfaceManager getExternalSurfaceManager() {
        return this.surfaceManager;
    }
    
    public void onDrawFrame(final GL10 gl10) {
        this.surfaceManager.consumerUpdateManagedSurfaces();
        final Point renderReprojectionThread = this.gvrApi.renderReprojectionThread();
        if (renderReprojectionThread != null) {
            this.setSurfaceSize(renderReprojectionThread.x, renderReprojectionThread.y);
        }
    }
    
    public void onPause() {
        this.gvrApi.onPauseReprojectionThread();
        this.surfaceManager.consumerDetachFromCurrentGLContext();
    }
    
    public void onSurfaceChanged(final GL10 gl10, final int n, final int n2) {
        this.surfaceManager.consumerAttachToCurrentGLContext();
    }
    
    public void onSurfaceCreated(final GL10 gl10, final EGLConfig eglConfig) {
        Thread.currentThread().setPriority(10);
        AndroidNCompat.setVrThread(Process.myTid());
        this.gvrApi.onSurfaceCreatedReprojectionThread();
    }
    
    public void setSurfaceSize(final int n, final int n2) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (n > 0 && n2 > 0) {
                    ScanlineRacingRenderer.this.gvrSurfaceView.getHolder().setFixedSize(n, n2);
                    return;
                }
                ScanlineRacingRenderer.this.gvrSurfaceView.getHolder().setSizeFromLayout();
            }
        });
    }
    
    public void setSurfaceView(final GvrSurfaceView gvrSurfaceView) {
        if (gvrSurfaceView != null) {
            this.gvrSurfaceView = gvrSurfaceView;
            return;
        }
        throw new IllegalArgumentException("GvrSurfaceView must be supplied for proper scanline rendering");
    }
    
    public void shutdown() {
        this.surfaceManager.shutdown();
    }
}
