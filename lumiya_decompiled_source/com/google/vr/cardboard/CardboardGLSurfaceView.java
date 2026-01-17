package com.google.vr.cardboard;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import com.google.vr.ndk.base.GvrSurfaceView;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class CardboardGLSurfaceView extends GvrSurfaceView {
    private static final String TAG = CardboardGLSurfaceView.class.getSimpleName();
    private final EglFactory eglFactory;
    private ArrayList<Runnable> eventQueueWhileDetached;
    private boolean isDetached;
    private boolean isRendererSet;
    private final DetachListener listener;

    /* loaded from: classes.dex */
    public interface DetachListener {
        void onSurfaceViewDetachedFromWindow();
    }

    public CardboardGLSurfaceView(Context context, AttributeSet attributeSet, DetachListener detachListener) {
        super(context, attributeSet);
        this.listener = detachListener;
        this.eglFactory = new EglFactory();
        setEGLContextFactory(this.eglFactory);
        setEGLWindowSurfaceFactory(this.eglFactory);
    }

    public CardboardGLSurfaceView(Context context, DetachListener detachListener) {
        super(context);
        this.listener = detachListener;
        this.eglFactory = new EglFactory();
        setEGLContextFactory(this.eglFactory);
        setEGLWindowSurfaceFactory(this.eglFactory);
    }

    public boolean isDetached() {
        return this.isDetached;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.vr.ndk.base.GvrSurfaceView, android.view.SurfaceView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.isDetached = false;
        if (this.eventQueueWhileDetached == null) {
            return;
        }
        ArrayList<Runnable> arrayList = this.eventQueueWhileDetached;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Runnable runnable = arrayList.get(i);
            i++;
            super.queueEvent(runnable);
        }
        this.eventQueueWhileDetached.clear();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.vr.ndk.base.GvrSurfaceView, android.view.SurfaceView, android.view.View
    public void onDetachedFromWindow() {
        if (this.isRendererSet && this.listener != null) {
            this.listener.onSurfaceViewDetachedFromWindow();
        }
        super.onDetachedFromWindow();
        this.isDetached = true;
    }

    @Override // com.google.vr.ndk.base.GvrSurfaceView
    public void onPause() {
        if (this.isRendererSet) {
            super.onPause();
        }
    }

    @Override // com.google.vr.ndk.base.GvrSurfaceView
    public void onResume() {
        if (this.isRendererSet) {
            super.onResume();
        }
    }

    @Override // com.google.vr.ndk.base.GvrSurfaceView
    public void queueEvent(Runnable runnable) {
        if (!this.isRendererSet) {
            runnable.run();
        } else if (!this.isDetached) {
            super.queueEvent(runnable);
        } else {
            if (this.eventQueueWhileDetached == null) {
                this.eventQueueWhileDetached = new ArrayList<>();
            }
            this.eventQueueWhileDetached.add(runnable);
        }
    }

    @Override // com.google.vr.ndk.base.GvrSurfaceView
    public void setEGLContextClientVersion(int i) {
        super.setEGLContextClientVersion(i);
        this.eglFactory.setEGLContextClientVersion(i);
    }

    @Override // com.google.vr.ndk.base.GvrSurfaceView
    public void setRenderer(GLSurfaceView.Renderer renderer) {
        super.setRenderer(renderer);
        this.isRendererSet = true;
    }
}
