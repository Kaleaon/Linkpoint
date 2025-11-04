// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.opengl.GLSurfaceView$Renderer;
import android.opengl.GLSurfaceView$EGLWindowSurfaceFactory;
import android.opengl.GLSurfaceView$EGLContextFactory;
import android.util.AttributeSet;
import android.content.Context;
import java.util.ArrayList;
import com.google.vr.ndk.base.GvrSurfaceView;

public class CardboardGLSurfaceView extends GvrSurfaceView
{
    private static final String TAG;
    private final EglFactory eglFactory;
    private ArrayList<Runnable> eventQueueWhileDetached;
    private boolean isDetached;
    private boolean isRendererSet;
    private final DetachListener listener;
    
    static {
        TAG = CardboardGLSurfaceView.class.getSimpleName();
    }
    
    public CardboardGLSurfaceView(final Context context, final AttributeSet set, final DetachListener listener) {
        super(context, set);
        this.listener = listener;
        this.setEGLContextFactory((GLSurfaceView$EGLContextFactory)(this.eglFactory = new EglFactory()));
        this.setEGLWindowSurfaceFactory((GLSurfaceView$EGLWindowSurfaceFactory)this.eglFactory);
    }
    
    public CardboardGLSurfaceView(final Context context, final DetachListener listener) {
        super(context);
        this.listener = listener;
        this.setEGLContextFactory((GLSurfaceView$EGLContextFactory)(this.eglFactory = new EglFactory()));
        this.setEGLWindowSurfaceFactory((GLSurfaceView$EGLWindowSurfaceFactory)this.eglFactory);
    }
    
    public boolean isDetached() {
        return this.isDetached;
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.isDetached = false;
        if (this.eventQueueWhileDetached != null) {
            final ArrayList<Runnable> list = this.eventQueueWhileDetached;
            final int size = list.size();
            int i = 0;
            while (i < size) {
                final Object value = list.get(i);
                ++i;
                super.queueEvent((Runnable)value);
            }
            this.eventQueueWhileDetached.clear();
        }
    }
    
    @Override
    protected void onDetachedFromWindow() {
        if (this.isRendererSet && this.listener != null) {
            this.listener.onSurfaceViewDetachedFromWindow();
        }
        super.onDetachedFromWindow();
        this.isDetached = true;
    }
    
    @Override
    public void onPause() {
        if (this.isRendererSet) {
            super.onPause();
        }
    }
    
    @Override
    public void onResume() {
        if (this.isRendererSet) {
            super.onResume();
        }
    }
    
    @Override
    public void queueEvent(final Runnable e) {
        if (!this.isRendererSet) {
            e.run();
            return;
        }
        if (!this.isDetached) {
            super.queueEvent(e);
            return;
        }
        if (this.eventQueueWhileDetached == null) {
            this.eventQueueWhileDetached = new ArrayList<Runnable>();
        }
        this.eventQueueWhileDetached.add(e);
    }
    
    @Override
    public void setEGLContextClientVersion(final int n) {
        super.setEGLContextClientVersion(n);
        this.eglFactory.setEGLContextClientVersion(n);
    }
    
    @Override
    public void setRenderer(final GLSurfaceView$Renderer renderer) {
        super.setRenderer(renderer);
        this.isRendererSet = true;
    }
    
    public interface DetachListener
    {
        void onSurfaceViewDetachedFromWindow();
    }
}
