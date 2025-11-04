// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render;

public class HeadTransformCompat
{
    public final float[] eulerAngles;
    public final float[] headTransformMatrix;
    public float lastYaw;
    public float neutralYaw;
    public boolean neutralYawValid;
    public float pitchDegrees;
    public final float[] rightVector;
    public final float[] rightVectorRaw;
    public final float[] rotationQuat;
    public final float[] translationVector;
    public float useButtonsYaw;
    public float viewExtraYaw;
    public float yawDegrees;
    
    public HeadTransformCompat() {
        this.rotationQuat = new float[4];
        this.translationVector = new float[4];
        this.headTransformMatrix = new float[16];
        this.rightVectorRaw = new float[4];
        this.rightVector = new float[4];
        this.eulerAngles = new float[3];
        this.neutralYawValid = false;
        this.viewExtraYaw = 0.0f;
    }
}
