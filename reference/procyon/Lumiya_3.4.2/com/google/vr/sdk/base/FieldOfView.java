// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base;

import android.opengl.Matrix;

public class FieldOfView
{
    private static final float CARDBOARD_V1_MAX_FOV_BOTTOM = 40.0f;
    private static final float CARDBOARD_V1_MAX_FOV_LEFT_RIGHT = 40.0f;
    private static final float CARDBOARD_V1_MAX_FOV_TOP = 40.0f;
    private static final float CARDBOARD_V2_2_MAX_FOV_BOTTOM = 60.0f;
    private static final float CARDBOARD_V2_2_MAX_FOV_LEFT_RIGHT = 60.0f;
    private static final float CARDBOARD_V2_2_MAX_FOV_TOP = 60.0f;
    private float bottom;
    private float left;
    private float right;
    private float top;
    
    public FieldOfView() {
        this.left = 60.0f;
        this.right = 60.0f;
        this.bottom = 60.0f;
        this.top = 60.0f;
    }
    
    public FieldOfView(final float n, final float n2, final float n3, final float n4) {
        this.setAngles(n, n2, n3, n4);
    }
    
    public FieldOfView(final FieldOfView fieldOfView) {
        this.copy(fieldOfView);
    }
    
    public static FieldOfView cardboardV1FieldOfView() {
        final FieldOfView fieldOfView = new FieldOfView();
        fieldOfView.setAngles(40.0f, 40.0f, 40.0f, 40.0f);
        return fieldOfView;
    }
    
    public static FieldOfView parseFromProtobuf(final float[] array) {
        if (array.length == 4) {
            return new FieldOfView(array[0], array[1], array[2], array[3]);
        }
        return null;
    }
    
    public void copy(final FieldOfView fieldOfView) {
        this.left = fieldOfView.left;
        this.right = fieldOfView.right;
        this.bottom = fieldOfView.bottom;
        this.top = fieldOfView.top;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o instanceof FieldOfView) {
            final FieldOfView fieldOfView = (FieldOfView)o;
            if (this.left != fieldOfView.left || this.right != fieldOfView.right || this.bottom != fieldOfView.bottom || this.top != fieldOfView.top) {
                b = false;
            }
            return b;
        }
        return false;
    }
    
    public float getBottom() {
        return this.bottom;
    }
    
    public float getLeft() {
        return this.left;
    }
    
    public float getRight() {
        return this.right;
    }
    
    public float getTop() {
        return this.top;
    }
    
    public void setAngles(final float left, final float right, final float bottom, final float top) {
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
    }
    
    public void setBottom(final float bottom) {
        this.bottom = bottom;
    }
    
    public void setLeft(final float left) {
        this.left = left;
    }
    
    public void setRight(final float right) {
        this.right = right;
    }
    
    public void setTop(final float top) {
        this.top = top;
    }
    
    public void toPerspectiveMatrix(final float n, final float n2, final float[] array, final int n3) {
        if (n3 + 16 <= array.length) {
            Matrix.frustumM(array, n3, (float)(-Math.tan(Math.toRadians(this.left))) * n, (float)Math.tan(Math.toRadians(this.right)) * n, (float)(-Math.tan(Math.toRadians(this.bottom))) * n, (float)Math.tan(Math.toRadians(this.top)) * n, n, n2);
            return;
        }
        throw new IllegalArgumentException("Not enough space to write the result");
    }
    
    public float[] toProtobuf() {
        return new float[] { this.left, this.right, this.bottom, this.top };
    }
    
    @Override
    public String toString() {
        return "{\n" + new StringBuilder(25).append("  left: ").append(this.left).append(",\n").toString() + new StringBuilder(26).append("  right: ").append(this.right).append(",\n").toString() + new StringBuilder(27).append("  bottom: ").append(this.bottom).append(",\n").toString() + new StringBuilder(24).append("  top: ").append(this.top).append(",\n").toString() + "}";
    }
}
