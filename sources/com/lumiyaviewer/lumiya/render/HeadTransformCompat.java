// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render;


public class HeadTransformCompat
{

    public final float eulerAngles[] = new float[3];
    public final float headTransformMatrix[] = new float[16];
    public float lastYaw;
    public float neutralYaw;
    public boolean neutralYawValid;
    public float pitchDegrees;
    public final float rightVector[] = new float[4];
    public final float rightVectorRaw[] = new float[4];
    public final float rotationQuat[] = new float[4];
    public final float translationVector[] = new float[4];
    public float useButtonsYaw;
    public float viewExtraYaw;
    public float yawDegrees;

    public HeadTransformCompat()
    {
        neutralYawValid = false;
        viewExtraYaw = 0.0F;
    }
}
