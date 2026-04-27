// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base;

import com.google.vr.cardboard.UsedByNative;

@UsedByNative
public class Eye
{
    private final float[] eyeView;
    private final FieldOfView fov;
    private float lastZFar;
    private float lastZNear;
    private float[] perspective;
    private volatile boolean projectionChanged;
    private final int type;
    private final Viewport viewport;
    
    @UsedByNative
    public Eye(final int type) {
        this.type = type;
        this.eyeView = new float[16];
        this.viewport = new Viewport();
        this.fov = new FieldOfView();
        this.projectionChanged = true;
    }
    
    @UsedByNative
    private void setValues(final int n, final int n2, final int n3, final int n4, final float n5, final float n6, final float n7, final float n8) {
        this.viewport.setViewport(n, n2, n3, n4);
        this.fov.setAngles(n5, n6, n7, n8);
        this.projectionChanged = true;
    }
    
    @UsedByNative
    public float[] getEyeView() {
        return this.eyeView;
    }
    
    public FieldOfView getFov() {
        return this.fov;
    }
    
    public float[] getPerspective(final float lastZNear, final float lastZFar) {
        if (!this.projectionChanged && this.lastZNear == lastZNear && this.lastZFar == lastZFar) {
            return this.perspective;
        }
        if (this.perspective == null) {
            this.perspective = new float[16];
        }
        this.getFov().toPerspectiveMatrix(lastZNear, lastZFar, this.perspective, 0);
        this.lastZNear = lastZNear;
        this.lastZFar = lastZFar;
        this.projectionChanged = false;
        return this.perspective;
    }
    
    public boolean getProjectionChanged() {
        return this.projectionChanged;
    }
    
    public int getType() {
        return this.type;
    }
    
    public Viewport getViewport() {
        return this.viewport;
    }
    
    public void setProjectionChanged() {
        this.projectionChanged = true;
    }
    
    public abstract static class Type
    {
        public static final int LEFT = 1;
        public static final int MONOCULAR = 0;
        public static final int RIGHT = 2;
    }
}
