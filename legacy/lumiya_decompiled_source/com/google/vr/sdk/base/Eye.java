package com.google.vr.sdk.base;

import com.google.vr.cardboard.UsedByNative;
@UsedByNative
/* loaded from: classes.dex */
public class Eye {
    private float lastZFar;
    private float lastZNear;
    private float[] perspective;
    private final int type;
    private final float[] eyeView = new float[16];
    private final Viewport viewport = new Viewport();
    private final FieldOfView fov = new FieldOfView();
    private volatile boolean projectionChanged = true;

    /* loaded from: classes.dex */
    public static abstract class Type {
        public static final int LEFT = 1;
        public static final int MONOCULAR = 0;
        public static final int RIGHT = 2;
    }

    @UsedByNative
    public Eye(int i) {
        this.type = i;
    }

    @UsedByNative
    private void setValues(int i, int i2, int i3, int i4, float f, float f2, float f3, float f4) {
        this.viewport.setViewport(i, i2, i3, i4);
        this.fov.setAngles(f, f2, f3, f4);
        this.projectionChanged = true;
    }

    @UsedByNative
    public float[] getEyeView() {
        return this.eyeView;
    }

    public FieldOfView getFov() {
        return this.fov;
    }

    public float[] getPerspective(float f, float f2) {
        if (!this.projectionChanged && this.lastZNear == f && this.lastZFar == f2) {
            return this.perspective;
        }
        if (this.perspective == null) {
            this.perspective = new float[16];
        }
        getFov().toPerspectiveMatrix(f, f2, this.perspective, 0);
        this.lastZNear = f;
        this.lastZFar = f2;
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
}
