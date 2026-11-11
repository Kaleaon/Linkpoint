package com.google.vr.cardboard;

import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Process;
import com.google.vr.ndk.base.GvrApi;
import com.google.vr.ndk.base.GvrLayout;
import com.google.vr.ndk.base.GvrSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
/* loaded from: classes.dex */
public class ScanlineRacingRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "ScanlineRacingRenderer";
    private final GvrApi gvrApi;
    private GvrSurfaceView gvrSurfaceView;
    private final ExternalSurfaceManager surfaceManager;

    public ScanlineRacingRenderer(GvrApi gvrApi) {
        if (gvrApi == null) {
            throw new IllegalArgumentException("GvrApi must be supplied for proper scanline rendering");
        }
        this.gvrApi = gvrApi;
        this.surfaceManager = new ExternalSurfaceManager(gvrApi);
    }

    public GvrLayout.ExternalSurfaceManager getExternalSurfaceManager() {
        return this.surfaceManager;
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onDrawFrame(GL10 gl10) {
        this.surfaceManager.consumerUpdateManagedSurfaces();
        Point renderReprojectionThread = this.gvrApi.renderReprojectionThread();
        if (renderReprojectionThread == null) {
            return;
        }
        setSurfaceSize(renderReprojectionThread.x, renderReprojectionThread.y);
    }

    public void onPause() {
        this.gvrApi.onPauseReprojectionThread();
        this.surfaceManager.consumerDetachFromCurrentGLContext();
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceChanged(GL10 gl10, int i, int i2) {
        this.surfaceManager.consumerAttachToCurrentGLContext();
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
        Thread.currentThread().setPriority(10);
        AndroidNCompat.setVrThread(Process.myTid());
        this.gvrApi.onSurfaceCreatedReprojectionThread();
    }

    public void setSurfaceSize(final int i, final int i2) {
        ThreadUtils.runOnUiThread(new Runnable() { // from class: com.google.vr.cardboard.ScanlineRacingRenderer.1
            @Override // java.lang.Runnable
            public void run() {
                if (i > 0 && i2 > 0) {
                    ScanlineRacingRenderer.this.gvrSurfaceView.getHolder().setFixedSize(i, i2);
                } else {
                    ScanlineRacingRenderer.this.gvrSurfaceView.getHolder().setSizeFromLayout();
                }
            }
        });
    }

    public void setSurfaceView(GvrSurfaceView gvrSurfaceView) {
        if (gvrSurfaceView == null) {
            throw new IllegalArgumentException("GvrSurfaceView must be supplied for proper scanline rendering");
        }
        this.gvrSurfaceView = gvrSurfaceView;
    }

    public void shutdown() {
        this.surfaceManager.shutdown();
    }
}
