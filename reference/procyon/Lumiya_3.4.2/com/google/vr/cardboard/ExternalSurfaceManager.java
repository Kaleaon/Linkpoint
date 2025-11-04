// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import java.util.Map;
import java.util.HashMap;
import android.graphics.SurfaceTexture$OnFrameAvailableListener;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.graphics.SurfaceTexture;
import java.util.concurrent.atomic.AtomicBoolean;
import android.util.Log;
import android.view.Surface;
import java.util.Iterator;
import android.os.Handler;
import com.google.vr.ndk.base.GvrApi;
import com.google.vr.ndk.base.GvrLayout;

public class ExternalSurfaceManager implements GvrLayout.ExternalSurfaceManager
{
    private static final String TAG;
    private final GvrApi gvrApi;
    private int nextID;
    private volatile ExternalSurfaceData surfaceData;
    private final Object surfaceDataUpdateLock;
    
    static {
        TAG = ExternalSurfaceManager.class.getSimpleName();
    }
    
    public ExternalSurfaceManager(final GvrApi gvrApi) {
        this.surfaceData = new ExternalSurfaceData();
        this.surfaceDataUpdateLock = new Object();
        this.nextID = 1;
        this.gvrApi = gvrApi;
    }
    
    private int createExternalSurfaceImpl(final ExternalSurfaceListener externalSurfaceListener, final Handler handler) {
        ExternalSurfaceCallback externalSurfaceCallback = null;
        synchronized (this.surfaceDataUpdateLock) {
            final ExternalSurfaceData surfaceData = new ExternalSurfaceData(this.surfaceData);
            final int i = this.nextID++;
            if (externalSurfaceListener != null && handler != null) {
                externalSurfaceCallback = new ExternalSurfaceCallback(externalSurfaceListener, handler);
            }
            surfaceData.surfaces.put(i, new ExternalSurface(i, externalSurfaceCallback));
            this.surfaceData = surfaceData;
            return i;
        }
    }
    
    public void consumerAttachToCurrentGLContext() {
        final Iterator<ExternalSurface> iterator = this.surfaceData.surfaces.values().iterator();
        while (iterator.hasNext()) {
            ((ExternalSurface)iterator.next()).maybeAttachToCurrentGLContext();
        }
    }
    
    public void consumerDetachFromCurrentGLContext() {
        final Iterator<ExternalSurface> iterator = this.surfaceData.surfaces.values().iterator();
        while (iterator.hasNext()) {
            ((ExternalSurface)iterator.next()).maybeDetachFromCurrentGLContext();
        }
        this.gvrApi.removeAllSurfacesReprojectionThread();
    }
    
    public void consumerUpdateManagedSurfaces() {
        final ExternalSurfaceData surfaceData = this.surfaceData;
        for (final ExternalSurface externalSurface : surfaceData.surfaces.values()) {
            externalSurface.maybeAttachToCurrentGLContext();
            externalSurface.updateSurfaceTexture(this.gvrApi);
        }
        final Iterator<ExternalSurface> iterator2 = surfaceData.surfacesToRelease.values().iterator();
        while (iterator2.hasNext()) {
            ((ExternalSurface)iterator2.next()).shutdown(this.gvrApi);
        }
    }
    
    @Override
    public int createExternalSurface() {
        return this.createExternalSurfaceImpl(null, null);
    }
    
    @Override
    public int createExternalSurface(final ExternalSurfaceListener externalSurfaceListener, final Handler handler) {
        if (externalSurfaceListener != null && handler != null) {
            return this.createExternalSurfaceImpl(externalSurfaceListener, handler);
        }
        throw new IllegalArgumentException("listener and handler must both be both non-null");
    }
    
    @Override
    public Surface getSurface(final int i) {
        final ExternalSurfaceData surfaceData = this.surfaceData;
        if (!surfaceData.surfaces.containsKey(i)) {
            Log.e(ExternalSurfaceManager.TAG, new StringBuilder(58).append("Surface with ID ").append(i).append(" does not exist, returning null").toString());
            return null;
        }
        return ((ExternalSurface)surfaceData.surfaces.get(i)).getSurface();
    }
    
    @Override
    public int getSurfaceCount() {
        return this.surfaceData.surfaces.size();
    }
    
    @Override
    public void releaseExternalSurface(final int i) {
        synchronized (this.surfaceDataUpdateLock) {
            final ExternalSurfaceData surfaceData = new ExternalSurfaceData(this.surfaceData);
            final ExternalSurface value = surfaceData.surfaces.remove(i);
            if (value == null) {
                Log.e(ExternalSurfaceManager.TAG, new StringBuilder(48).append("Not releasing nonexistent surface ID ").append(i).toString());
            }
            else {
                surfaceData.surfacesToRelease.put(i, value);
                this.surfaceData = surfaceData;
            }
        }
    }
    
    public void shutdown() {
        while (true) {
            while (true) {
                final Iterator<ExternalSurface> iterator2;
                synchronized (this.surfaceDataUpdateLock) {
                    final ExternalSurfaceData surfaceData = this.surfaceData;
                    this.surfaceData = new ExternalSurfaceData();
                    final Iterator<ExternalSurface> iterator = surfaceData.surfaces.values().iterator();
                    while (iterator.hasNext()) {
                        ((ExternalSurface)iterator.next()).shutdown(this.gvrApi);
                    }
                    iterator2 = surfaceData.surfacesToRelease.values().iterator();
                    if (!iterator2.hasNext()) {
                        return;
                    }
                }
                iterator2.next().shutdown(this.gvrApi);
                continue;
            }
        }
    }
    
    private static class ExternalSurface
    {
        private final ExternalSurfaceCallback callback;
        private final int[] glTextureId;
        private final AtomicBoolean hasNewFrame;
        private final int id;
        private boolean isAttached;
        private final AtomicBoolean released;
        private volatile Surface surface;
        private volatile SurfaceTexture surfaceTexture;
        private final float[] transformMatrix;
        
        ExternalSurface(final int id, final ExternalSurfaceCallback callback) {
            this.transformMatrix = new float[16];
            this.hasNewFrame = new AtomicBoolean(false);
            this.released = new AtomicBoolean(false);
            this.glTextureId = new int[1];
            this.isAttached = false;
            this.id = id;
            this.callback = callback;
            Matrix.setIdentityM(this.transformMatrix, 0);
        }
        
        Surface getSurface() {
            return this.surface;
        }
        
        void maybeAttachToCurrentGLContext() {
            if (!this.isAttached) {
                GLES20.glGenTextures(1, this.glTextureId, 0);
                if (this.surfaceTexture != null) {
                    this.surfaceTexture.attachToGLContext(this.glTextureId[0]);
                }
                else {
                    (this.surfaceTexture = new SurfaceTexture(this.glTextureId[0])).setOnFrameAvailableListener((SurfaceTexture$OnFrameAvailableListener)new SurfaceTexture$OnFrameAvailableListener() {
                        public void onFrameAvailable(final SurfaceTexture surfaceTexture) {
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
        
        void maybeDetachFromCurrentGLContext() {
            if (this.isAttached) {
                this.surfaceTexture.detachFromGLContext();
                this.isAttached = false;
            }
        }
        
        void shutdown(final GvrApi gvrApi) {
            if (!this.released.getAndSet(true)) {
                if (this.surfaceTexture != null) {
                    this.surfaceTexture.release();
                    this.surfaceTexture = null;
                    this.surface = null;
                }
                gvrApi.updateSurfaceReprojectionThread(this.id, 0, 0L, this.transformMatrix);
            }
        }
        
        void updateSurfaceTexture(final GvrApi gvrApi) {
            if (this.hasNewFrame.getAndSet(false)) {
                this.surfaceTexture.updateTexImage();
                this.surfaceTexture.getTransformMatrix(this.transformMatrix);
                gvrApi.updateSurfaceReprojectionThread(this.id, this.glTextureId[0], this.surfaceTexture.getTimestamp(), this.transformMatrix);
            }
        }
    }
    
    private static class ExternalSurfaceCallback
    {
        private final Runnable frameAvailableRunnable;
        private final Handler handler;
        private final ExternalSurfaceListener listener;
        
        public ExternalSurfaceCallback(final ExternalSurfaceListener listener, final Handler handler) {
            this.listener = listener;
            this.handler = handler;
            this.frameAvailableRunnable = new Runnable() {
                @Override
                public void run() {
                    ExternalSurfaceCallback.this.listener.onFrameAvailable();
                }
            };
        }
        
        public void postOnAvailable(final Surface surface) {
            this.handler.post((Runnable)new Runnable() {
                @Override
                public void run() {
                    ExternalSurfaceCallback.this.listener.onSurfaceAvailable(surface);
                }
            });
        }
        
        public void postOnFrameAvailable() {
            this.handler.post(this.frameAvailableRunnable);
        }
    }
    
    private static class ExternalSurfaceData
    {
        final HashMap<Integer, ExternalSurface> surfaces;
        final HashMap<Integer, ExternalSurface> surfacesToRelease;
        
        ExternalSurfaceData() {
            this.surfaces = new HashMap<Integer, ExternalSurface>();
            this.surfacesToRelease = new HashMap<Integer, ExternalSurface>();
        }
        
        ExternalSurfaceData(final ExternalSurfaceData externalSurfaceData) {
            this.surfaces = new HashMap<Integer, ExternalSurface>(externalSurfaceData.surfaces);
            this.surfacesToRelease = new HashMap<Integer, ExternalSurface>(externalSurfaceData.surfacesToRelease);
            final Iterator<Map.Entry<Integer, ExternalSurface>> iterator = this.surfacesToRelease.entrySet().iterator();
            while (iterator.hasNext()) {
                if (((Map.Entry<K, ExternalSurface>)iterator.next()).getValue().released.get()) {
                    iterator.remove();
                }
            }
        }
    }
}
