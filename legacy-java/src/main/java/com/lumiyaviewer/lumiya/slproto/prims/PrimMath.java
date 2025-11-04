package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

/* loaded from: classes.dex */
public class PrimMath {
    public static final float F_DEG_TO_RAD = 0.017453292f;
    public static final float F_PI = 3.1415927f;

    public static float lerp(float f, float f2, float f3) {
        return ((f2 - f) * f3) + f;
    }

    public static float[] lookAt(LLVector3 lLVector3, LLVector3 lLVector32, LLVector3 lLVector33) {
        LLVector3 sub = LLVector3.sub(lLVector32, lLVector3);
        sub.normVec();
        LLVector3 lLVector34 = new LLVector3(sub);
        lLVector34.setCross(lLVector33);
        return new float[]{lLVector34.x, lLVector33.x, -sub.x, 0.0f, lLVector34.y, lLVector33.y, -sub.y, 0.0f, lLVector34.z, lLVector33.z, -sub.z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    }
}
