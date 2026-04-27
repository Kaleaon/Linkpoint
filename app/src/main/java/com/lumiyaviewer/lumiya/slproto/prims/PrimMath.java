package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

public class PrimMath {
    public static final float F_DEG_TO_RAD = 0.017453292f;
    public static final float F_PI = 3.1415927f;

    public static float lerp(float f, float f2, float f3) {
        return ((f2 - f) * f3) + f;
    }

    public static float[] lookAt(LLVector3 lLVector3, LLVector3 lLVector32, LLVector3 lLVector33) {
        float[] fArr = new float[16];
        LLVector3 sub = LLVector3.sub(lLVector32, lLVector3);
        sub.normVec();
        LLVector3 lLVector34 = new LLVector3(sub);
        lLVector34.setCross(lLVector33);
        fArr[0] = lLVector34.x;
        fArr[4] = lLVector34.y;
        fArr[8] = lLVector34.z;
        fArr[1] = lLVector33.x;
        fArr[5] = lLVector33.y;
        fArr[9] = lLVector33.z;
        fArr[2] = -sub.x;
        fArr[6] = -sub.y;
        fArr[10] = -sub.z;
        fArr[15] = 1.0f;
        return fArr;
    }
}
