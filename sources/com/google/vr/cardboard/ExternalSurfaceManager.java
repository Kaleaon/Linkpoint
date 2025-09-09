package com.google.vr.cardboard;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import com.google.vr.ndk.base.GvrApi;
import com.google.vr.ndk.base.GvrLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExternalSurfaceManager implements GvrLayout.ExternalSurfaceManager {
    private static final String TAG = ExternalSurfaceManager.class.getSimpleName();
    private final GvrApi gvrApi;
    private int nextID = 1;
    private volatile ExternalSurfaceData surfaceData = new ExternalSurfaceData();
    private final Object surfaceDataUpdateLock = new Object();

    private static class ExternalSurface {
        /* access modifiers changed from: private */
        public final ExternalSurfaceCallback callback;
        private final int[] glTextureId = new int[1];
        /* access modifiers changed from: private */
        public final AtomicBoolean hasNewFrame = new AtomicBoolean(false);
        private final int id;
        private boolean isAttached = false;
        /* access modifiers changed from: private */
        public final AtomicBoolean released = new AtomicBoolean(false);
        private volatile Surface surface;
        private volatile SurfaceTexture surfaceTexture;
        private final float[] transformMatrix = new float[16];

        ExternalSurface(int i, ExternalSurfaceCallback externalSurfaceCallback) {
            this.id = i;
            this.callback = externalSurfaceCallback;
            Matrix.setIdentityM(this.transformMatrix, 0);
        }

        /* access modifiers changed from: package-private */
        public Surface getSurface() {
            return this.surface;
        }

        /* access modifiers changed from: package-private */
        public void maybeAttachToCurrentGLContext() {
            if (!this.isAttached) {
                GLES20.glGenTextures(1, this.glTextureId, 0);
                if (this.surfaceTexture != null) {
                    this.surfaceTexture.attachToGLContext(this.glTextureId[0]);
                } else {
                    this.surfaceTexture = new SurfaceTexture(this.glTextureId[0]);
                    this.surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                            ExternalSurface.this.hasNewFrame.set(true);
                            if (ExternalSurface.this.callback != null) {
                                ExternalSurface.this.callback.postOnFrameAvailable();
                            }
                        }
                    });
                    this.surface = new Surface(this.surfaceTexture);
                }
                this.isAttached = true;
                if (this.callback != null) {
                    this.callback.postOnAvailable(this.surface);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void maybeDetachFromCurrentGLContext() {
            if (this.isAttached) {
                this.surfaceTexture.detachFromGLContext();
                this.isAttached = false;
            }
        }

        /* access modifiers changed from: package-private */
        public void shutdown(GvrApi gvrApi) {
            if (!this.released.getAndSet(true)) {
                if (this.surfaceTexture != null) {
                    this.surfaceTexture.release();
                    this.surfaceTexture = null;
                    this.surface = null;
                }
                gvrApi.updateSurfaceReprojectionThread(this.id, 0, 0, this.transformMatrix);
            }
        }

        /* access modifiers changed from: package-private */
        public void updateSurfaceTexture(GvrApi gvrApi) {
            if (this.hasNewFrame.getAndSet(false)) {
                this.surfaceTexture.updateTexImage();
                this.surfaceTexture.getTransformMatrix(this.transformMatrix);
                GvrApi gvrApi2 = gvrApi;
                gvrApi2.updateSurfaceReprojectionThread(this.id, this.glTextureId[0], this.surfaceTexture.getTimestamp(), this.transformMatrix);
            }
        }
    }

    private static class ExternalSurfaceCallback {
        private final Runnable frameAvailableRunnable = new Runnable() {
            public void run() {
                ExternalSurfaceCallback.this.listener.onFrameAvailable();
            }
        };
        private final Handler handler;
        /* access modifiers changed from: private */
        public final GvrLayout.ExternalSurfaceListener listener;

        public ExternalSurfaceCallback(GvrLayout.ExternalSurfaceListener externalSurfaceListener, Handler handler2) {
            this.listener = externalSurfaceListener;
            this.handler = handler2;
        }

        public void postOnAvailable(final Surface surface) {
            this.handler.post(new Runnable() {
                public void run() {
                    ExternalSurfaceCallback.this.listener.onSurfaceAvailable(surface);
                }
            });
        }

        public void postOnFrameAvailable() {
            this.handler.post(this.frameAvailableRunnable);
        }
    }

    private static class ExternalSurfaceData {
        final HashMap<Integer, ExternalSurface> surfaces;
        final HashMap<Integer, ExternalSurface> surfacesToRelease;

        ExternalSurfaceData() {
            this.surfaces = new HashMap<>();
            this.surfacesToRelease = new HashMap<>();
        }

        ExternalSurfaceData(ExternalSurfaceData externalSurfaceData) {
            this.surfaces = new HashMap<>(externalSurfaceData.surfaces);
            this.surfacesToRelease = new HashMap<>(externalSurfaceData.surfacesToRelease);
            Iterator<Map.Entry<Integer, ExternalSurface>> it = this.surfacesToRelease.entrySet().iterator();
            while (it.hasNext()) {
                if (((ExternalSurface) it.next().getValue()).released.get()) {
                    it.remove();
                }
            }
        }
    }

    public ExternalSurfaceManager(GvrApi gvrApi2) {
        this.gvrApi = gvrApi2;
    }

    private int createExternalSurfaceImpl(GvrLayout.ExternalSurfaceListener externalSurfaceListener, Handler handler) {
        int i;
        ExternalSurfaceCallback externalSurfaceCallback = null;
        synchronized (this.surfaceDataUpdateLock) {
            ExternalSurfaceData externalSurfaceData = new ExternalSurfaceData(this.surfaceData);
            i = this.nextID;
            this.nextID = i + 1;
            if (!(externalSurfaceListener == null || handler == null)) {
                externalSurfaceCallback = new ExternalSurfaceCallback(externalSurfaceListener, handler);
            }
            externalSurfaceData.surfaces.put(Integer.valueOf(i), new ExternalSurface(i, externalSurfaceCallback));
            this.surfaceData = externalSurfaceData;
        }
        return i;
    }

    public void consumerAttachToCurrentGLContext() {
        for (ExternalSurface maybeAttachToCurrentGLContext : this.surfaceData.surfaces.values()) {
            maybeAttachToCurrentGLContext.maybeAttachToCurrentGLContext();
        }
    }

    public void consumerDetachFromCurrentGLContext() {
        for (ExternalSurface maybeDetachFromCurrentGLContext : this.surfaceData.surfaces.values()) {
            maybeDetachFromCurrentGLContext.maybeDetachFromCurrentGLContext();
        }
        this.gvrApi.removeAllSurfacesReprojectionThread();
    }

    public void consumerUpdateManagedSurfaces() {
        ExternalSurfaceData externalSurfaceData = this.surfaceData;
        for (ExternalSurface next : externalSurfaceData.surfaces.values()) {
            next.maybeAttachToCurrentGLContext();
            next.updateSurfaceTexture(this.gvrApi);
        }
        for (ExternalSurface shutdown : externalSurfaceData.surfacesToRelease.values()) {
            shutdown.shutdown(this.gvrApi);
        }
    }

    public int createExternalSurface() {
        return createExternalSurfaceImpl((GvrLayout.ExternalSurfaceListener) null, (Handler) null);
    }

    public int createExternalSurface(GvrLayout.ExternalSurfaceListener externalSurfaceListener, Handler handler) {
        if (externalSurfaceListener != null && handler != null) {
            return createExternalSurfaceImpl(externalSurfaceListener, handler);
        }
        throw new IllegalArgumentException("listener and handler must both be both non-null");
    }

    public Surface getSurface(int i) {
        ExternalSurfaceData externalSurfaceData = this.surfaceData;
        if (externalSurfaceData.surfaces.containsKey(Integer.valueOf(i))) {
            return externalSurfaceData.surfaces.get(Integer.valueOf(i)).getSurface();
        }
        Log.e(TAG, new StringBuilder(58).append("Surface with ID ").append(i).append(" does not exist, returning null").toString());
        return null;
    }

    public int getSurfaceCount() {
        return this.surfaceData.surfaces.size();
    }

    public void releaseExternalSurface(int i) {
        synchronized (this.surfaceDataUpdateLock) {
            ExternalSurfaceData externalSurfaceData = new ExternalSurfaceData(this.surfaceData);
            ExternalSurface remove = externalSurfaceData.surfaces.remove(Integer.valueOf(i));
            if (remove == null) {
                Log.e(TAG, new StringBuilder(48).append("Not releasing nonexistent surface ID ").append(i).toString());
            } else {
                externalSurfaceData.surfacesToRelease.put(Integer.valueOf(i), remove);
                this.surfaceData = externalSurfaceData;
            }
        }
    }

    public void shutdown() {
        synchronized (this.surfaceDataUpdateLock) {
            ExternalSurfaceData externalSurfaceData = this.surfaceData;
            this.surfaceData = new ExternalSurfaceData();
            for (ExternalSurface shutdown : externalSurfaceData.surfaces.values()) {
                shutdown.shutdown(this.gvrApi);
            }
            for (ExternalSurface shutdown2 : externalSurfaceData.surfacesToRelease.values()) {
                shutdown2.shutdown(this.gvrApi);
            }
        }
    }
}
