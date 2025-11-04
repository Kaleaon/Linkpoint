package com.lumiyaviewer.lumiya.render;

/* loaded from: classes.dex */
public class HeadTransformCompat {
    public float lastYaw;
    public float neutralYaw;
    public float pitchDegrees;
    public float useButtonsYaw;
    public float yawDegrees;
    public final float[] rotationQuat = new float[4];
    public final float[] translationVector = new float[4];
    public final float[] headTransformMatrix = new float[16];
    public final float[] rightVectorRaw = new float[4];
    public final float[] rightVector = new float[4];
    public final float[] eulerAngles = new float[3];
    public boolean neutralYawValid = false;
    public float viewExtraYaw = 0.0f;
}
