package com.lumiyaviewer.lumiya.render;

public class HeadTransformCompat {
    public final float[] eulerAngles = new float[3];
    public final float[] headTransformMatrix = new float[16];
    public float lastYaw;
    public float neutralYaw;
    public boolean neutralYawValid = false;
    public float pitchDegrees;
    public final float[] rightVector = new float[4];
    public final float[] rightVectorRaw = new float[4];
    public final float[] rotationQuat = new float[4];
    public final float[] translationVector = new float[4];
    public float useButtonsYaw;
    public float viewExtraYaw = 0.0f;
    public float yawDegrees;
}
